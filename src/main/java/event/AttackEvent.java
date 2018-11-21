package event;

import caculation.AttackCaculation;
import component.Equipment;
import config.MessageConfig;
import io.netty.channel.Channel;
import memory.NettyMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userskillrelation;
import pojo.Weaponequipmentbar;
import skill.UserSkill;
import team.Team;
import utils.DelimiterUtils;
import component.Monster;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("attackEvent")
public class AttackEvent {
    @Autowired
    private CommonEvent commonEvent;
    @Autowired
    private AttackCaculation attackCaculation;
    public void attack(Channel channel, String msg) {
        if(msg.startsWith("b")||msg.startsWith("w")||msg.startsWith("fix-")){
            commonEvent.common(channel,msg);
            return;
        }
        if (msg.equals("q")) {
            NettyMemory.monsterMap.remove(NettyMemory.session2UserIds.get(channel));
            channel.writeAndFlush(DelimiterUtils.turnToPacket(MessageConfig.RETREATFIGHT));
            NettyMemory.eventStatus.put(channel,EventStatus.STOPAREA);
        } else {
            if (NettyMemory.userskillrelationMap.get(channel).containsKey(msg)) {
                User user = NettyMemory.session2UserIds.get(channel);
                Map<String,Monster>monsterMap = null;
                if(user.getTeamId()!=null&&NettyMemory.bossAreaMap.containsKey(user.getTeamId())){
                    monsterMap = getMonsterMap(user);
                }else{
                    monsterMap = changeToMap(user);
                }
                for (Map.Entry<String, Monster> entry:monsterMap.entrySet()) {
                    Monster monster = entry.getValue();
                    UserSkill userSkill = NettyMemory.SkillMap.get(NettyMemory.userskillrelationMap.get(channel).get(msg).getSkillid());
                    Userskillrelation userskillrelation = NettyMemory.userskillrelationMap.get(channel).get(msg);
//                  技能CD检查
                    if (System.currentTimeMillis() > userskillrelation.getSkillcds() + userSkill.getAttackCd()) {
//                     人物蓝量检查
                        BigInteger userMp = new BigInteger(user.getMp());
                        BigInteger skillMp = new BigInteger(userSkill.getSkillMp());
                        if (userMp.compareTo(skillMp) > 0) {
                            BigInteger attackDamage = new BigInteger(userSkill.getDamage());
//                          增加装备攻击属性
//                          更新武器耐久度
                            attackDamage = attackCaculation.caculate(user,attackDamage);
//                          攻击逻辑
                            BigInteger monsterLife = monster.subLife(attackDamage);
//                         蓝量计算
                            userMp = userMp.subtract(skillMp);
                            user.setMp(userMp.toString());
                            String resp = out(user);
                            BigInteger minValueOfLife = new BigInteger("0");
//                         检查怪物血量
                            if (monsterLife.compareTo(minValueOfLife) <= 0) {
//                              蓝量计算逻辑
                                monster.setValueOfLife(minValueOfLife.toString());
                                user.subMp(skillMp.toString());
                                resp  += System.getProperty("line.separator")
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
                                channel.writeAndFlush(DelimiterUtils.turnToPacket(resp));
//                                         修改怪物状态
                                monster.setValueOfLife("0");
                                monster.setStatus("0");
//                                  移除任务攻击记录
                                NettyMemory.monsterMap.remove(user);
//                              切换场景
                                if(NettyMemory.eventStatus.get(channel).equals(EventStatus.ATTACK)){
                                    NettyMemory.eventStatus.put(channel, EventStatus.STOPAREA);
                                }
                            } else {
                                resp +=
                                        System.getProperty("line.separator")
                                                + "[" + userSkill.getSkillName()
                                                + "]技能对" + monster.getName()
                                                + "造成了" + attackDamage + "点伤害"
                                                + System.getProperty("line.separator")
                                                + "[消耗蓝量:]" + userSkill.getSkillMp()
                                                + System.getProperty("line.separator")
                                                + "[人物剩余蓝量值:]" + user.getMp()
                                                + System.getProperty("line.separator")
                                                + "怪物剩余血量:" + monster.getValueOfLife();
                                userskillrelation.setSkillcds(System.currentTimeMillis());
                                //TODO:数据库更新技能时间
                                //  更新用户总战斗伤害的值
                                if(user.getTeamId()!=null&&NettyMemory.bossAreaMap.get(user.getTeamId())!=null){
                                    if(!NettyMemory.bossAreaMap.get(user.getTeamId()).getDamageAll().containsKey(user)){
                                        NettyMemory.bossAreaMap.get(user.getTeamId()).getDamageAll().put(user,attackDamage.toString());
                                    }else {
                                        String newDamageValue = NettyMemory.bossAreaMap.get(user.getTeamId()).getDamageAll().get(user);
                                        BigInteger newDamageValueI = new BigInteger(newDamageValue).add(attackDamage);
                                        NettyMemory.bossAreaMap.get(user.getTeamId()).getDamageAll().put(user,newDamageValueI.toString());
                                    }
                                }
                                channel.writeAndFlush(DelimiterUtils.turnToPacket(resp));
                            }
                        } else {
                            channel.writeAndFlush(DelimiterUtils.turnToPacket(MessageConfig.UNENOUGHMP));
                        }
                    } else {
                            channel.writeAndFlush(DelimiterUtils.turnToPacket(MessageConfig.UNSKILLCD));
                    }
                }
            }
        }
    }

    private Map<String, Monster> changeToMap(User user) {
        List<Monster> monsterList = NettyMemory.areaMap.get(user.getPos()).getMonsters();
        Map<String,Monster> map = new HashMap<>();
        for(Monster monster:monsterList){
            map.put(monster.getId()+"",monster);
        }
        return map;
    }

    private String out(User user) {
        String resp = "";
        for(Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()){
            Equipment equipment = NettyMemory.equipmentMap.get(weaponequipmentbar.getWid());
            resp += System.getProperty("line.separator")
                    + equipment.getName()+"剩余耐久为:" +weaponequipmentbar.getDurability()
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
}
