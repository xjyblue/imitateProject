package buff;

import caculation.HpCaculation;
import component.BossScene;
import component.HpMedicine;
import component.MpMedicine;
import config.BuffConfig;
import config.DeadOrAliveConfig;
import event.EventStatus;
import component.Monster;
import io.netty.channel.Channel;
import context.ProjectContext;
import org.springframework.stereotype.Component;
import packet.PacketType;
import pojo.User;
import team.Team;
import utils.AttackUtil;
import utils.LevelUtil;
import utils.MessageUtil;

import java.math.BigInteger;
import java.util.Map;

@Component
public class BuffTask{
    private BigInteger add = new BigInteger("10");
    private HpCaculation hpCaculation;

    public BuffTask(HpCaculation hpCaculation) {
        this.hpCaculation = hpCaculation;
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
                if (ProjectContext.eventStatus.get(channel).equals(EventStatus.ATTACK) || ProjectContext.eventStatus.get(channel).equals(EventStatus.BOSSAREA)) {
//              更新用户防御buff
                    if (entrySecond.getKey().equals(BuffConfig.DEFENSEBUFF) && entrySecond.getValue() != 3000) {
                        endTime = ProjectContext.userBuffEndTime.get(user).get(BuffConfig.DEFENSEBUFF);
                        if (System.currentTimeMillis() > endTime) {
                            user.getBuffMap().put(BuffConfig.DEFENSEBUFF, 3000);
                        }
                    }

//              更新用户被击晕buff
                    if (entrySecond.getKey().equals(BuffConfig.SLEEPBUFF) && entrySecond.getValue() != 5000) {
                        endTime = ProjectContext.userBuffEndTime.get(user).get(BuffConfig.SLEEPBUFF);
                        if (System.currentTimeMillis() > endTime) {
                            user.getBuffMap().put(BuffConfig.SLEEPBUFF, 5000);
                        }
                    }

//              更新用户中毒buff
                    if (entrySecond.getKey().equals(BuffConfig.POISONINGBUFF) && entrySecond.getValue() != 2000) {
                        endTime = ProjectContext.userBuffEndTime.get(user).get(BuffConfig.POISONINGBUFF);
                        if (System.currentTimeMillis() > endTime) {
                            user.getBuffMap().put(BuffConfig.POISONINGBUFF, 2000);
                        } else {
                            Buff buff = ProjectContext.buffMap.get(entrySecond.getValue());

                            channel.writeAndFlush(MessageUtil.turnToPacket("你受到了怪物的中毒攻击，产生中毒伤害为:" + buff.getAddSecondValue() + "人物剩余血量" + user.getHp(), PacketType.USERBUFMSG));
                        }
                    }

//               更新用户召唤师buff
                    if (entrySecond.getKey().equals(BuffConfig.BABYBUF)) {
                        Buff buff = ProjectContext.buffMap.get(entrySecond.getValue());
                        if (entrySecond.getKey().equals(BuffConfig.BABYBUF) && entrySecond.getValue() != 7000) {
                            endTime = ProjectContext.userBuffEndTime.get(user).get(BuffConfig.BABYBUF);
                            if (System.currentTimeMillis() > endTime) {
                                user.getBuffMap().put(BuffConfig.BABYBUF, 7000);
                            } else {
                                if (ProjectContext.userToMonsterMap.containsKey(user)) {
                                    for (Map.Entry<Integer, Monster> monsterEntry : ProjectContext.userToMonsterMap.get(user).entrySet()) {
                                        monster = monsterEntry.getValue();
                                    }
//                                 后期改成场景线程去心跳执行这一块的内容,通过计算截止错开时间
                                    hpCaculation.subMonsterHp(monster, buff.getAddSecondValue());
                                    if (monster.getValueOfLife().equals("0")) {
                                        monster.setStatus(DeadOrAliveConfig.DEAD);
                                        user.getBuffMap().put(BuffConfig.BABYBUF, 7000);
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
                if (entrySecond.getKey().equals(BuffConfig.TREATMENTBUFF) && entrySecond.getValue() != 6000) {
//                          红药buff处理
                    if (ProjectContext.hpMedicineMap.containsKey(entrySecond.getValue())) {
                        HpMedicine hpMedicine = ProjectContext.hpMedicineMap.get(entrySecond.getValue());
                        if (hpMedicine.isImmediate()) {
                            endTime = hpMedicine.getCd() * 1000 + System.currentTimeMillis();
                            ProjectContext.userBuffEndTime.get(user).put(BuffConfig.TREATMENTBUFF, endTime);
                            hpCaculation.addUserHp(user, hpMedicine.getReplyValue());
                            user.getBuffMap().put(BuffConfig.TREATMENTBUFF, 6000);
                        }
                        return;
                    }
//                      技能buff处理
                    Buff buff = ProjectContext.buffMap.get(entrySecond.getValue());
                    if (user.getTeamId() != null) {
                        Team team = ProjectContext.teamMap.get(user.getTeamId());
                        for (Map.Entry<String, User> entryUser : team.getUserMap().entrySet()) {
                            hpCaculation.addUserHp(entryUser.getValue(), buff.getRecoverValue());
                            Channel channelTemp = ProjectContext.userToChannelMap.get(entryUser.getValue());
                            channelTemp.writeAndFlush(MessageUtil.turnToPacket(user.getUsername() + "使用了全体回血技能,全体回复血量:" + buff.getRecoverValue()));
                        }
                    } else {
                        hpCaculation.addUserHp(user, buff.getRecoverValue());
                        channel.writeAndFlush(MessageUtil.turnToPacket(user.getUsername() + "使用了全体回血技能,全体回复血量:" + buff.getRecoverValue()));
                    }
                    user.getBuffMap().put(BuffConfig.TREATMENTBUFF, 6000);
                }

//               更新用户回蓝buff
                if (entrySecond.getKey().equals(BuffConfig.MPBUFF)) {
//                自动回蓝
                    BigInteger userMp = new BigInteger(user.getMp());
                    BigInteger maxMp = new BigInteger(LevelUtil.getMaxHp(user));
                    if (userMp.compareTo(maxMp) < 0) {
                        if (user.getBuffMap().get(BuffConfig.MPBUFF).equals(1000)) {
                            userMp = userMp.add(add);
                            user.setMp(userMp.toString());
                        } else {
                            MpMedicine mpMedicine = ProjectContext.mpMedicineMap.get(user.getBuffMap().get(BuffConfig.MPBUFF));
                            endTime = null;
                            if (!ProjectContext.userBuffEndTime.containsKey(user)) {
                                endTime = System.currentTimeMillis() + mpMedicine.getKeepTime() * 1000;
                                ProjectContext.userBuffEndTime.get(user).put(BuffConfig.MPBUFF, endTime);
                            } else {
                                endTime = ProjectContext.userBuffEndTime.get(user).get(BuffConfig.MPBUFF);
                            }
                            Long currentTime = System.currentTimeMillis();
                            if (endTime > currentTime) {
                                userMp = userMp.add(new BigInteger(mpMedicine.getSecondValue()));
                                if (userMp.compareTo(maxMp) >= 0) {
                                    userMp = maxMp;
                                }
                                user.setMp(userMp.toString());
                            } else {
                                user.getBuffMap().put(BuffConfig.MPBUFF, 1000);
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
