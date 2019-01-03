package service.buffservice.service;

import service.caculationservice.service.HpCaculationService;
import service.sceneservice.entity.BossScene;
import core.component.good.HpMedicine;
import core.component.good.MpMedicine;
import service.buffservice.entity.BuffConstant;
import core.config.GrobalConfig;
import core.ChannelStatus;
import core.component.monster.Monster;
import io.netty.channel.Channel;
import core.context.ProjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import packet.PacketType;
import pojo.User;
import service.buffservice.entity.Buff;
import service.teamservice.entity.Team;
import service.attackservice.util.AttackUtil;
import service.levelservice.service.LevelService;
import utils.MessageUtil;

import java.math.BigInteger;
import java.util.Map;

@Component
public class BuffTask{
    @Autowired
    private LevelService levelService;

    private BigInteger add = new BigInteger(GrobalConfig.MPSECONDVALUE);
    private HpCaculationService hpCaculationService;

    public BuffTask(HpCaculationService hpCaculationService) {
        this.hpCaculationService = hpCaculationService;
    }

    public void refresh(User user) {
        try {
            Channel channel = ProjectContext.userToChannelMap.get(user);
            Long endTime = null;

            Monster monster = null;
            if (ProjectContext.userToMonsterMap.containsKey(user)) {
                for (Map.Entry<Integer, Monster> monsterEntry : ProjectContext.userToMonsterMap.get(user).entrySet()) {
                    monster = monsterEntry.getValue();
                }
            }

            Map<String, Integer> bufferMap = user.getBuffMap();
            for (Map.Entry<String, Integer> entrySecond : bufferMap.entrySet()) {
                if (ProjectContext.eventStatus.get(channel).equals(ChannelStatus.ATTACK) || ProjectContext.eventStatus.get(channel).equals(ChannelStatus.BOSSSCENE)) {
//              更新用户防御buff
                    if (entrySecond.getKey().equals(BuffConstant.DEFENSEBUFF) && entrySecond.getValue() != 3000) {
                        endTime = ProjectContext.userBuffEndTime.get(user).get(BuffConstant.DEFENSEBUFF);
                        if (System.currentTimeMillis() > endTime) {
                            user.getBuffMap().put(BuffConstant.DEFENSEBUFF, 3000);
                        }
                    }

//              更新用户被击晕buff
                    if (entrySecond.getKey().equals(BuffConstant.SLEEPBUFF) && entrySecond.getValue() != 5000) {
                        endTime = ProjectContext.userBuffEndTime.get(user).get(BuffConstant.SLEEPBUFF);
                        if (System.currentTimeMillis() > endTime) {
                            user.getBuffMap().put(BuffConstant.SLEEPBUFF, 5000);
                        }
                    }

//              更新用户中毒buff
                    if (entrySecond.getKey().equals(BuffConstant.POISONINGBUFF) && entrySecond.getValue() != 2000) {
                        endTime = ProjectContext.userBuffEndTime.get(user).get(BuffConstant.POISONINGBUFF);
                        if (System.currentTimeMillis() > endTime) {
                            user.getBuffMap().put(BuffConstant.POISONINGBUFF, 2000);
                        } else {
                            Buff buff = ProjectContext.buffMap.get(entrySecond.getValue());

                            channel.writeAndFlush(MessageUtil.turnToPacket("你受到了怪物的中毒攻击，产生中毒伤害为:" + buff.getAddSecondValue() + "人物剩余血量" + user.getHp(), PacketType.USERBUFMSG));
                        }
                    }

//               更新用户召唤师buff
                    if (entrySecond.getKey().equals(BuffConstant.BABYBUF)) {
                        Buff buff = ProjectContext.buffMap.get(entrySecond.getValue());
                        if (entrySecond.getKey().equals(BuffConstant.BABYBUF) && entrySecond.getValue() != 7000) {
                            endTime = ProjectContext.userBuffEndTime.get(user).get(BuffConstant.BABYBUF);
                            if (System.currentTimeMillis() > endTime) {
                                user.getBuffMap().put(BuffConstant.BABYBUF, 7000);
                            } else {
                                if (ProjectContext.userToMonsterMap.containsKey(user)) {
                                    for (Map.Entry<Integer, Monster> monsterEntry : ProjectContext.userToMonsterMap.get(user).entrySet()) {
                                        monster = monsterEntry.getValue();
                                    }
//                                 后期改成场景线程去心跳执行这一块的内容,通过计算截止错开时间
                                    hpCaculationService.subMonsterHp(monster, buff.getAddSecondValue());
                                    if (monster.getValueOfLife().equals(GrobalConfig.MINVALUE)) {
                                        monster.setStatus(GrobalConfig.DEAD);
                                        user.getBuffMap().put(BuffConstant.BABYBUF, 7000);
                                        if (user.getTeamId() != null && ProjectContext.bossAreaMap.containsKey(user.getTeamId())) {
                                            BossScene bossScene = ProjectContext.bossAreaMap.get(user.getTeamId());
//                                            AttackUtil.changeUserAttackMonster(user, bossScene,monster);
                                            AttackUtil.killBossMessageToAll(user, monster);
                                        }
                                    }
                                    channel.writeAndFlush(MessageUtil.turnToPacket("你的召唤兽[" + buff.getName() + "]正在对" + monster.getName() + "造成" + buff.getAddSecondValue() + "点攻击", PacketType.ATTACKMSG));
                                }
                            }
                        }
                    }
                }

//               更新用户回血buff
                if (entrySecond.getKey().equals(BuffConstant.TREATMENTBUFF) && entrySecond.getValue() != 6000) {
//                          红药buff处理
                    if (ProjectContext.hpMedicineMap.containsKey(entrySecond.getValue())) {
                        HpMedicine hpMedicine = ProjectContext.hpMedicineMap.get(entrySecond.getValue());
                        if (hpMedicine.isImmediate()) {
                            endTime = hpMedicine.getCd() * 1000 + System.currentTimeMillis();
                            ProjectContext.userBuffEndTime.get(user).put(BuffConstant.TREATMENTBUFF, endTime);
                            hpCaculationService.addUserHp(user, hpMedicine.getReplyValue());
                            user.getBuffMap().put(BuffConstant.TREATMENTBUFF, 6000);
                        }
                        return;
                    }
//                      技能buff处理
                    Buff buff = ProjectContext.buffMap.get(entrySecond.getValue());
                    if (user.getTeamId() != null) {
                        Team team = ProjectContext.teamMap.get(user.getTeamId());
                        for (Map.Entry<String, User> entryUser : team.getUserMap().entrySet()) {
                            hpCaculationService.addUserHp(entryUser.getValue(), buff.getRecoverValue());
                            Channel channelTemp = ProjectContext.userToChannelMap.get(entryUser.getValue());
                            channelTemp.writeAndFlush(MessageUtil.turnToPacket(user.getUsername() + "使用了全体回血技能,全体回复血量:" + buff.getRecoverValue()));
                        }
                    } else {
                        hpCaculationService.addUserHp(user, buff.getRecoverValue());
                        channel.writeAndFlush(MessageUtil.turnToPacket(user.getUsername() + "使用了全体回血技能,全体回复血量:" + buff.getRecoverValue()));
                    }
                    user.getBuffMap().put(BuffConstant.TREATMENTBUFF, 6000);
                }

//               更新用户回蓝buff
                if (entrySecond.getKey().equals(BuffConstant.MPBUFF)) {
//                自动回蓝
                    BigInteger userMp = new BigInteger(user.getMp());
                    BigInteger maxMp = new BigInteger(levelService.getMaxHp(user));
                    if (userMp.compareTo(maxMp) < 0) {
                        if (user.getBuffMap().get(BuffConstant.MPBUFF).equals(1000)) {
                            userMp = userMp.add(add);
                            user.setMp(userMp.toString());
                        } else {
                            MpMedicine mpMedicine = ProjectContext.mpMedicineMap.get(user.getBuffMap().get(BuffConstant.MPBUFF));
                            endTime = null;
                            if (!ProjectContext.userBuffEndTime.containsKey(user)) {
                                endTime = System.currentTimeMillis() + mpMedicine.getKeepTime() * 1000;
                                ProjectContext.userBuffEndTime.get(user).put(BuffConstant.MPBUFF, endTime);
                            } else {
                                endTime = ProjectContext.userBuffEndTime.get(user).get(BuffConstant.MPBUFF);
                            }
                            Long currentTime = System.currentTimeMillis();
                            if (endTime > currentTime) {
                                userMp = userMp.add(new BigInteger(mpMedicine.getSecondValue()));
                                if (userMp.compareTo(maxMp) >= 0) {
                                    userMp = maxMp;
                                }
                                user.setMp(userMp.toString());
                            } else {
                                user.getBuffMap().put(BuffConstant.MPBUFF, 1000);
                                userMp = userMp.add(new BigInteger("10"));
                                if (userMp.compareTo(maxMp) >= 0) {
                                    userMp = maxMp;
                                }
                                user.setMp(userMp.toString());
                            }
                        }
                    }
                    System.out.println(user.getUsername() + "--的蓝量--" + userMp.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
