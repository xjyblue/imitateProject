package event;

import caculation.AttackCaculation;
import component.BossArea;
import component.Equipment;
import component.Monster;
import io.netty.channel.Channel;
import memory.NettyMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userskillrelation;
import pojo.Weaponequipmentbar;
import skill.MonsterSkill;
import skill.UserSkill;
import task.BossAttackTask;
import team.Team;
import utils.DelimiterUtils;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/11/19 16:04
 */
@Component("bossEvent")
public class BossEvent {
    @Autowired
    private AttackEvent attackEvent;
    @Autowired
    private AttackCaculation attackCaculation;
    public void enterBossArea(Channel channel, String msg) {
        User user = getUser(channel);
        Team team = null;
        if(user.getTeamId()==null){
            team = new Team();
            team.setTeamId(UUID.randomUUID().toString());
            team.setLeader(user);
            user.setTeamId(team.getTeamId());
            HashMap<String,User> teamUserMap = new HashMap<>();
            teamUserMap.put(user.getUsername(),user);
            team.setUserMap(teamUserMap);
            NettyMemory.teamMap.put(user.getTeamId(),team);
        }else {
            team = getTeam(user);
        }
        if(!team.getLeader().getUsername().equals(user.getUsername())){
            channel.writeAndFlush(DelimiterUtils.addDelimiter("你无权带队进入副本，你不是队长"));
            return;
        }
        BossArea bossArea = new BossArea();
        bossArea.setKeepTime(60l);
        bossArea.setName("七天连锁酒店本");
        bossArea.setTeamId(user.getTeamId());
        NettyMemory.bossAreaMap.put(team.getTeamId(),bossArea);
        //改变多个人的副本状态
        changeChannelStatus(team,bossArea);
    }

    private void changeChannelStatus(Team team,BossArea bossArea) {
        for(Map.Entry<String,User>entry:team.getUserMap().entrySet()){
            Channel channel = NettyMemory.userToChannelMap.get(entry.getValue());
            NettyMemory.eventStatus.put(channel,EventStatus.BOSSAREA);
            channel.writeAndFlush(DelimiterUtils.addDelimiter("进入boss副本,出现boss"+bossArea.getBossName()+""));
        }
    }

    public void attack(Channel channel, String msg) {
        String temp[] = msg.split("-");
        if (temp.length == 3 && NettyMemory.userskillrelationMap.get(channel).containsKey(temp[2])) {
            User user = NettyMemory.session2UserIds.get(channel);
            Monster monster = null;
            for(Map.Entry<String, Monster> entry:getMonsterMap(user).entrySet()){
//             输入的怪物是否存在
                monster = entry.getValue();
                if (monster.getName().equals(temp[1]) && !monster.getStatus().equals("0")) {
                    Userskillrelation userskillrelation = NettyMemory.userskillrelationMap.get(channel).get(temp[2]);
                    UserSkill userSkill = NettyMemory.SkillMap.get(userskillrelation.getSkillid());
//                                    判断人物MP量是否足够
                    BigInteger userMp = new BigInteger(user.getMp());
                    BigInteger skillMp = new BigInteger(userSkill.getSkillMp());
                    if (userMp.compareTo(skillMp) > 0) {
//                      蓝量计算
                        userMp = userMp.subtract(skillMp);
                        user.setMp(userMp.toString());
//                                    判断技能冷却
                        if (System.currentTimeMillis() > userskillrelation.getSkillcds() + userSkill.getAttackCd()) {
//                               判断攻击完怪物是否死亡，生命值计算逻辑
                            BigInteger attackDamage = new BigInteger(userSkill.getDamage());
//                              攻击逻辑计算
                            attackDamage = attackCaculation.caculate(user, attackDamage);
//                              怪物掉血，生命值计算逻辑
                            BigInteger monsterLife = monster.subLife(attackDamage);
                            String resp = out(user);
                            BigInteger minValueOfLife = new BigInteger("0");

//                          记录伤害为AI做准备
                            if(!NettyMemory.bossAreaMap.get(user.getTeamId()).getDamageAll().containsKey(user)){
                                NettyMemory.bossAreaMap.get(user.getTeamId()).getDamageAll().put(user,attackDamage.toString());
                            }else {
                                String newDamageValue = NettyMemory.bossAreaMap.get(user.getTeamId()).getDamageAll().get(user);
                                BigInteger newDamageValueI = new BigInteger(newDamageValue).add(attackDamage);
                                NettyMemory.bossAreaMap.get(user.getTeamId()).getDamageAll().put(user,newDamageValueI.toString());
                            }

                            if (monsterLife.compareTo(minValueOfLife) <= 0) {
                                monster.setValueOfLife(minValueOfLife.toString());
                                resp += System.getProperty("line.separator")
                                        + "[技能]:" + userSkill.getSkillName()
                                        + System.getProperty("line.separator")
                                        + "对[" + monster.getName()
                                        + "]造成了" + attackDamage + "点伤害"
                                        + System.getProperty("line.separator")
                                        + "[怪物血量]:" + 0
                                        + System.getProperty("line.separator")
                                        + "[消耗蓝量]:" + userSkill.getSkillMp()
                                        + System.getProperty("line.separator")
                                        + "[人物剩余蓝量]:" + user.getMp()
                                        + System.getProperty("line.separator")
                                        + "怪物已死亡";
                                monster.setValueOfLife("0");
//                              修改怪物状态
                                monster.setStatus("0");
                                successMessToAll(user,monster,resp);
                            } else {
                                Map<String, Userskillrelation> map = NettyMemory.userskillrelationMap.get(channel);
//                                    切换到攻击模式
                                NettyMemory.eventStatus.put(channel, EventStatus.ATTACK);
                                resp += System.getProperty("line.separator")
                                        + "[技能]:" + userSkill.getSkillName()
                                        + System.getProperty("line.separator")
                                        + "对[" + monster.getName()
                                        + "]造成了" + attackDamage + "点伤害"
                                        + System.getProperty("line.separator")
                                        + "[怪物血量]:" + monster.getValueOfLife()
                                        + System.getProperty("line.separator")
                                        + "[消耗蓝量]:" + userSkill.getSkillMp()
                                        + System.getProperty("line.separator")
                                        + "[人物剩余蓝量]:" + user.getMp()
                                        + System.getProperty("line.separator");
                                channel.writeAndFlush(DelimiterUtils.addDelimiter(resp));
                                //TODO:更新数据库人物技能蓝量
//                                    刷新技能时间
                                userskillrelation.setSkillcds(System.currentTimeMillis());
//                                  记录任务当前攻击的怪物
                                List<Monster> monsters = new ArrayList<Monster>();
                                monsters.add(monster);
                                NettyMemory.monsterMap.put(user, monsters);
                                NettyMemory.eventStatus.put(channel, EventStatus.ATTACK);
//                                    提醒用户你已进入战斗模式
                                if(!NettyMemory.bossAreaMap.get(user.getTeamId()).isFight()){
                                    NettyMemory.bossAreaMap.get(user.getTeamId()).setFight(true);
                                    channel.writeAndFlush(DelimiterUtils.addDelimiter("你已经进入战斗模式"));
                                    String jobId= UUID.randomUUID().toString();
                                    BossAttackTask bossAttackTask = new BossAttackTask(user.getTeamId(),channel,jobId, NettyMemory.futureMap);
                                    Future future = NettyMemory.bossAreaThreadPool.scheduleAtFixedRate(bossAttackTask, 0, 1, TimeUnit.SECONDS);
                                    BossArea  bossArea = NettyMemory.bossAreaMap.get(user.getTeamId());
                                    NettyMemory.endBossAreaTime.put(user.getTeamId(),(System.currentTimeMillis()+ bossArea.getKeepTime()*1000));
                                    NettyMemory.futureMap.put(bossAttackTask.getJobId(), future);
                                }
                            }
                        }
                    } else {
                        channel.writeAndFlush(DelimiterUtils.addDelimiter("人物MP值不足"));
                    }
                    break;
                }
            }
        }
    }

    private void successMessToAll(User user,Monster monster,String resp) {
        Team team = NettyMemory.teamMap.get(user.getTeamId());
        for(Map.Entry<String,User>entry:team.getUserMap().entrySet()){
            Channel channelTemp = NettyMemory.userToChannelMap.get(entry.getValue());
            NettyMemory.eventStatus.put(channelTemp,EventStatus.STOPAREA);
            if(entry.getValue()==user){
                resp += System.getProperty("line.separator")
                        + monster.getName()+"副本攻略成功，热烈庆祝各位参与的小伙伴";
                channelTemp.writeAndFlush(DelimiterUtils.addDelimiter(resp));
            }else {
                channelTemp.writeAndFlush(DelimiterUtils.addDelimiter(monster.getName()+"副本攻略成功，热烈庆祝各位参与的小伙伴"));
            }
        }
    }

    private String out(User user) {
        String resp = "";
        for (Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()) {
            Equipment equipment = NettyMemory.equipmentMap.get(weaponequipmentbar.getWid());
            resp += System.getProperty("line.separator")
                    + equipment.getName() + "剩余耐久为:" + weaponequipmentbar.getDurability()
            ;
        }
        return resp;
    }

    private Map<String,Monster> getMonsterMap(User user){
       return NettyMemory.bossAreaMap.get(getTeam(user).getTeamId()).getMap();
    }

    private Team getTeam(User user) {
        if(!NettyMemory.teamMap.containsKey(user.getTeamId()))return null;
       return NettyMemory.teamMap.get(user.getTeamId());
    }

    private User getUser(Channel channel) {
        return NettyMemory.session2UserIds.get(channel);
    }


}