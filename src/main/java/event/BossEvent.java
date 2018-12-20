package event;

import caculation.AttackCaculation;
import component.BossScene;
import component.Equipment;
import component.Monster;
import component.Scene;
import config.MessageConfig;
import config.DeadOrAliveConfig;
import io.netty.channel.Channel;
import context.ProjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userskillrelation;
import pojo.Weaponequipmentbar;
import skill.UserSkill;
import team.Team;
import utils.AttackUtil;
import utils.MessageUtil;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/11/19 16:04
 */
@Component("bossEvent")
public class BossEvent {
    @Autowired
    private AttackEvent attackEvent;
    @Autowired
    private AttackCaculation attackCaculation;
    @Autowired
    private ShopEvent shopEvent;
    @Autowired
    private CommonEvent commonEvent;
    @Autowired
    private OutfitEquipmentEvent outfitEquipmentEvent;
    @Autowired
    private ChatEvent chatEvent;
    @Autowired
    private BuffEvent buffEvent;

    public void enterBossArea(Channel channel, String msg) {
        User user = getUser(channel);
        Team team = null;
//      处理用户死亡后重连副本逻辑
        if (user.getTeamId() != null && ProjectContext.bossAreaMap.containsKey(user.getTeamId()) && ProjectContext.bossAreaMap.get(user.getTeamId()).isFight()) {
            ProjectContext.eventStatus.put(channel, EventStatus.BOSSAREA);
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.REBRORNANDCONNECTBOSSAREA));
            return;
        }
        if (user.getTeamId() == null) {
            team = new Team();
            team.setTeamId(UUID.randomUUID().toString());
            team.setLeader(user);
            user.setTeamId(team.getTeamId());
            HashMap<String, User> teamUserMap = new HashMap<>();
            teamUserMap.put(user.getUsername(), user);
            team.setUserMap(teamUserMap);
            ProjectContext.teamMap.put(user.getTeamId(), team);
        } else {
            team = getTeam(user);
        }
        if (!team.getLeader().getUsername().equals(user.getUsername())) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.YOUARENOLEADER));
            return;
        }
        BossScene bossScene = new BossScene(outfitEquipmentEvent);
        bossScene.setKeepTime(5000l);
        bossScene.setTeamId(user.getTeamId());


        ProjectContext.bossAreaMap.put(team.getTeamId(), bossScene);

        changeChannelStatus(team, bossScene);
    }

    private void changeChannelStatus(Team team, BossScene bossScene) {
        for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
//          移除之前场景线程的用户
            User user = entry.getValue();
            Scene sceneOld = ProjectContext.sceneMap.get(user.getPos());
            sceneOld.getUserMap().remove(user.getUsername());
//          新场景添加用户
            bossScene.getUserMap().put(user.getUsername(), user);

//          更新渠道的状态
            Channel channel = ProjectContext.userToChannelMap.get(entry.getValue());
            ProjectContext.eventStatus.put(channel, EventStatus.BOSSAREA);

            String resp = "进入" + bossScene.getBossName() + "副本,出现boss有：";
            for (Map.Entry<String, Monster> entryMonster : bossScene.getMonsters().get(bossScene.getSequence().get(0)).entrySet()) {
                resp += entryMonster.getValue().getName() + " ";
            }
            channel.writeAndFlush(MessageUtil.turnToPacket(resp));
        }
        //          开启副本场景帧频线程
        Future future = ProjectContext.bossAreaThreadPool.scheduleAtFixedRate(bossScene, 0, 30, TimeUnit.MILLISECONDS);
        ProjectContext.futureMap.put(bossScene.getTeamId(), future);
        bossScene.setFutureMap(ProjectContext.futureMap);

    }

    public void attack(Channel channel, String msg) {
        if (msg.startsWith("chat")) {
            chatEvent.chat(channel, msg);
            return;
        }
        if (msg.startsWith("s")) {
            shopEvent.shop(channel, msg);
            return;
        }
        if (msg.startsWith("b") || msg.startsWith("w") || msg.startsWith("fix-")) {
            commonEvent.common(channel, msg);
            return;
        }

        String temp[] = msg.split("-");
        if (temp.length == 3 && ProjectContext.userskillrelationMap.get(channel).containsKey(temp[2])) {
            User user = ProjectContext.session2UserIds.get(channel);
            Monster monster = null;
            for (Map.Entry<String, Monster> entry : getMonsterMap(user).entrySet()) {
//             输入的怪物是否存在
                monster = entry.getValue();
                if (monster.getName().equals(temp[1]) && !monster.getStatus().equals(DeadOrAliveConfig.DEAD)) {
                    Userskillrelation userskillrelation = ProjectContext.userskillrelationMap.get(channel).get(temp[2]);
                    UserSkill userSkill = ProjectContext.skillMap.get(userskillrelation.getSkillid());
//                  判断人物MP量是否足够
                    BigInteger userMp = new BigInteger(user.getMp());
                    BigInteger skillMp = new BigInteger(userSkill.getSkillMp());
                    if (userMp.compareTo(skillMp) > 0) {
//                      蓝量计算
                        userMp = userMp.subtract(skillMp);
                        user.setMp(userMp.toString());
//                      判断技能冷却
                        if (System.currentTimeMillis() > userskillrelation.getSkillcds() + userSkill.getAttackCd()) {

//                          技能buff处理
                            buffEvent.buffSolve(userskillrelation, userSkill, monster, user);

                            AttackUtil.addMonsterToUserMonsterList(user, monster);

//                          判断攻击完怪物是否死亡，生命值计算逻辑
                            BigInteger attackDamage = new BigInteger(userSkill.getDamage());
//                          攻击逻辑计算
                            attackDamage = attackCaculation.caculate(user, attackDamage);
//                          怪物掉血，生命值计算逻辑
                            BigInteger monsterLife = monster.subLife(attackDamage);
                            String resp = out(user);
                            BigInteger minValueOfLife = new BigInteger("0");

//                          记录伤害为AI做准备
                            if (!ProjectContext.bossAreaMap.get(user.getTeamId()).getDamageAll().containsKey(user)) {
                                ProjectContext.bossAreaMap.get(user.getTeamId()).getDamageAll().put(user, attackDamage.toString());
                            } else {
                                String newDamageValue = ProjectContext.bossAreaMap.get(user.getTeamId()).getDamageAll().get(user);
                                BigInteger newDamageValueI = new BigInteger(newDamageValue).add(attackDamage);
                                ProjectContext.bossAreaMap.get(user.getTeamId()).getDamageAll().put(user, newDamageValueI.toString());
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
                                monster.setStatus(DeadOrAliveConfig.DEAD);

//                              更改用户攻击的boss
                                if (monster.getType().equals(Monster.TYPEOFBOSS)) {
                                    BossScene bossScene = ProjectContext.bossAreaMap.get(user.getTeamId());
                                    bossScene.getMonsters().get(bossScene.getSequence().get(0)).get(monster.getName()).setStatus(DeadOrAliveConfig.DEAD);
                                    AttackUtil.changeUserAttackMonster(user, bossScene);
                                    AttackUtil.killBossMessageToAll(user, monster);
                                }

                                BossScene bossScene = ProjectContext.bossAreaMap.get(user.getTeamId());
                                if (!checkBossAreaAllBoss(bossScene)) {
                                    if (ProjectContext.bossAreaMap.get(user.getTeamId()).isFight()) {
                                        sendMessToAll(user, resp, monster);
                                    } else {
                                        bossScene.setFight(true);
                                        ProjectContext.endBossAreaTime.put(user.getTeamId(),System.currentTimeMillis()+bossScene.getKeepTime()*1000);

                                        sendMessToAll(user, resp, monster);
                                    }
                                } else {
//                                   直接打死
                                    successMessToAll(user, monster, resp);
                                }
                            } else {
                                Map<String, Userskillrelation> map = ProjectContext.userskillrelationMap.get(channel);
//                                    切换到攻击模式
                                ProjectContext.eventStatus.put(channel, EventStatus.ATTACK);
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
                                channel.writeAndFlush(MessageUtil.turnToPacket(resp));

//                             刷新技能时间
                                userskillrelation.setSkillcds(System.currentTimeMillis());
//                             记录任务当前攻击的怪物
                                AttackUtil.addMonsterToUserMonsterList(user, monster);

                                ProjectContext.eventStatus.put(channel, EventStatus.ATTACK);
                                BossScene bossScene = ProjectContext.bossAreaMap.get(user.getTeamId());

                                ProjectContext.bossAreaMap.get(user.getTeamId()).setFight(true);
                                ProjectContext.endBossAreaTime.put(user.getTeamId(),System.currentTimeMillis()+bossScene.getKeepTime()*1000);
                            }
                        }
                    } else {
                        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNENOUGHMP));
                    }
                    break;
                }
            }
        }
    }

    private void sendMessToAll(User user, String resp, Monster monster) {
        Map<String, User> map = ProjectContext.teamMap.get(user.getTeamId()).getUserMap();
        for (Map.Entry<String, User> entry : map.entrySet()) {
            Channel channelTemp = ProjectContext.userToChannelMap.get(entry.getValue());
            if (entry.getValue() == user) {
                ProjectContext.eventStatus.put(channelTemp, EventStatus.ATTACK);
                channelTemp.writeAndFlush(MessageUtil.turnToPacket(resp));
                channelTemp.writeAndFlush(MessageUtil.turnToPacket("成功击杀" + monster.getName()));
            } else {
                channelTemp.writeAndFlush(MessageUtil.turnToPacket("成功击杀" + monster.getName()));
            }
        }
    }

    private boolean checkBossAreaAllBoss(BossScene bossScene) {
        for (Map.Entry<String, Monster> entry : bossScene.getMonsters().get(bossScene.getSequence().get(0)).entrySet()) {
            if (entry.getValue().getStatus().equals("1")) {
                return false;
            }
        }
        return true;
    }

    private void successMessToAll(User user, Monster monster, String resp) {
        Team team = ProjectContext.teamMap.get(user.getTeamId());
        boolean flag = checkAllBossStatus(team);
        for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
            Channel channelTemp = ProjectContext.userToChannelMap.get(entry.getValue());
            ProjectContext.eventStatus.put(channelTemp, EventStatus.STOPAREA);
            if (entry.getValue() == user) {
                resp += System.getProperty("line.separator")
                        + monster.getName() + MessageConfig.BOSSAREASUCCESS;
                channelTemp.writeAndFlush(MessageUtil.turnToPacket(resp));
                if (flag) {
                    outfitEquipmentEvent.getGoods(ProjectContext.userToChannelMap.get(user), monster);
                }
            } else {
                channelTemp.writeAndFlush(MessageUtil.turnToPacket(monster.getName() + MessageConfig.BOSSAREASUCCESS));
            }
        }
    }

    private boolean checkAllBossStatus(Team team) {
        for (Map.Entry<String, Monster> entry : ProjectContext.bossAreaMap.get(team.getTeamId()).getMap().entrySet()) {
            if (entry.getValue().getStatus().equals("1")) {
                return false;
            }
        }
        return true;
    }

    private String out(User user) {
        String resp = "";
        for (Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()) {
            Equipment equipment = ProjectContext.equipmentMap.get(weaponequipmentbar.getWid());
            resp += System.getProperty("line.separator")
                    + equipment.getName() + "剩余耐久为:" + weaponequipmentbar.getDurability()
            ;
        }
        return resp;
    }

    private Map<String, Monster> getMonsterMap(User user) {
        BossScene bossScene = ProjectContext.bossAreaMap.get(user.getTeamId());
        return bossScene.getMonsters().get(bossScene.getSequence().get(0));
    }

    private Team getTeam(User user) {
        if (!ProjectContext.teamMap.containsKey(user.getTeamId())) return null;
        return ProjectContext.teamMap.get(user.getTeamId());
    }

    private User getUser(Channel channel) {
        return ProjectContext.session2UserIds.get(channel);
    }

}
