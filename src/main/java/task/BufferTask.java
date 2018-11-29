package task;

import component.MpMedicine;
import config.BuffConfig;
import event.EventStatus;
import buff.Buff;
import component.Monster;
import io.netty.channel.Channel;
import memory.NettyMemory;
import packet.PacketType;
import pojo.User;
import utils.MessageUtil;

import java.math.BigInteger;
import java.util.Map;

public class BufferTask implements Runnable {
    private BigInteger add = new BigInteger("10");
    private Map<Channel, User> map = NettyMemory.session2UserIds;

    public void run() {
        try{
            for (Map.Entry<Channel, User> entry : map.entrySet()) {
                User user = entry.getValue();
                Channel channel = entry.getKey();
                Long endTime = null;
//          更新怪物中毒buff
                if (NettyMemory.monsterMap.containsKey(user)) {
                    Monster monster = NettyMemory.monsterMap.get(user).get(0);
                    if (monster != null && monster.getBufMap() != null && monster.getBufMap().containsKey(BuffConfig.POISONINGBUFF) && monster.getBufMap().get(BuffConfig.POISONINGBUFF) != 2000) {
                        if (NettyMemory.eventStatus.get(channel).equals(EventStatus.ATTACK)) {
                            endTime = NettyMemory.monsterBuffEndTime.get(monster).get(BuffConfig.POISONINGBUFF);
                            if (System.currentTimeMillis() < endTime&&!monster.getValueOfLife().equals("0")) {
                                monster = NettyMemory.monsterMap.get(user).get(0);
                                Buff buff = NettyMemory.buffMap.get(monster.getBufMap().get(BuffConfig.POISONINGBUFF));

                                monster.subLife(new BigInteger(buff.getAddSecondValue()));
//                           处理中毒扣死
                                if (new BigInteger(monster.getValueOfLife()).compareTo(new BigInteger("0")) < 0) {
                                    monster.setValueOfLife("0");
                                    monster.setStatus("0");
                                    monster.getBufMap().put(BuffConfig.POISONINGBUFF, 2000);
                                }
//                          怪物中毒将buff推送给所有玩家
                                if(monster.getType().equals(Monster.TYPEOFBOSS)){
                                    sendMessageToAll(user, buff, monster);
                                }else {
                                    channel.writeAndFlush(MessageUtil.turnToPacket("怪物中毒掉血[" + buff.getAddSecondValue() + "]+怪物剩余血量为:" + monster.getValueOfLife(), PacketType.MONSTERBUFMSG));
                                }
                            } else {
                                monster.getBufMap().put(BuffConfig.POISONINGBUFF, 2000);
                            }
                        }
                    }
                }


                Map<String, Integer> bufferMap = user.getBufferMap();
                for (Map.Entry<String, Integer> entrySecond : bufferMap.entrySet()) {
                    if (NettyMemory.eventStatus.get(channel).equals(EventStatus.ATTACK)) {
//                  更新用户防御buff
                        if (entrySecond.getKey().equals(BuffConfig.DEFENSEBUFF) && entrySecond.getValue() != 3000) {
                            endTime = NettyMemory.userBuffEndTime.get(user).get(BuffConfig.DEFENSEBUFF);
                            if (System.currentTimeMillis() > endTime) {
                                user.getBufferMap().put(BuffConfig.DEFENSEBUFF, 3000);
                            }
                        }

//              更新用户被击晕buff
                        if (entrySecond.getKey().equals(BuffConfig.SLEEPBUFF) && entrySecond.getValue() != 5000) {
                            endTime = NettyMemory.userBuffEndTime.get(user).get(BuffConfig.SLEEPBUFF);
                            if (System.currentTimeMillis() > endTime) {
                                user.getBufferMap().put(BuffConfig.SLEEPBUFF, 5000);
                            }
                        }

//              更新用户中毒buff
                        if (entrySecond.getKey().equals(BuffConfig.POISONINGBUFF) && entrySecond.getValue() != 2000) {
                            endTime = NettyMemory.userBuffEndTime.get(user).get(BuffConfig.POISONINGBUFF);
                            if (System.currentTimeMillis() > endTime) {
                                user.getBufferMap().put(BuffConfig.POISONINGBUFF, 2000);
                            } else {
                                Buff buff = NettyMemory.buffMap.get(entrySecond.getValue());
                                user.subHp(buff.getAddSecondValue());
                                channel.writeAndFlush(MessageUtil.turnToPacket("你受到了怪物的中毒攻击，产生中毒伤害为:" + buff.getAddSecondValue() + "人物剩余血量" + user.getHp(), PacketType.USERBUFMSG));
                            }
                        }
                    }

//               更新用户回蓝buff
                    if (entrySecond.getKey().equals(BuffConfig.MPBUFF)) {
//                自动回蓝
                        BigInteger userMp = new BigInteger(entry.getValue().getMp());
                        BigInteger maxMp = new BigInteger("10000");
                        if (userMp.compareTo(maxMp) < 0) {
                            if (user.getBufferMap().get(BuffConfig.MPBUFF).equals(1000)) {
                                userMp = userMp.add(add);
                                entry.getValue().setMp(userMp.toString());
                            } else {
                                MpMedicine mpMedicine = NettyMemory.mpMedicineMap.get(user.getBufferMap().get(BuffConfig.MPBUFF));
                                endTime = null;
                                if (!NettyMemory.userBuffEndTime.containsKey(user)) {
                                    endTime = System.currentTimeMillis() + mpMedicine.getKeepTime() * 1000;
                                    NettyMemory.userBuffEndTime.get(user).put(BuffConfig.MPBUFF, endTime);
                                } else {
                                    endTime = NettyMemory.userBuffEndTime.get(user).get(BuffConfig.MPBUFF);
                                }
                                Long currentTime = System.currentTimeMillis();
                                if (endTime > currentTime) {
                                    userMp = userMp.add(new BigInteger(mpMedicine.getSecondValue()));
                                    if (userMp.compareTo(maxMp) >= 0) {
                                        userMp = maxMp;
                                    }
                                    user.setMp(userMp.toString());
                                } else {
                                    user.getBufferMap().put(BuffConfig.MPBUFF, 1000);
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
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendMessageToAll(User user, Buff buff, Monster monster) {
        for (Map.Entry<String, User> entry : NettyMemory.teamMap.get(user.getTeamId()).getUserMap().entrySet()) {
            Channel channelTemp = NettyMemory.userToChannelMap.get(entry.getValue());
            channelTemp.writeAndFlush(MessageUtil.turnToPacket("怪物中毒掉血[" + buff.getAddSecondValue() + "]+怪物剩余血量为:" + monster.getValueOfLife(), PacketType.MONSTERBUFMSG));
        }
    }
}
