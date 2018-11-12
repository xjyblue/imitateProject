package task;

import Component.Monster;
import Event.EventStatus;
import io.netty.channel.Channel;
import memory.NettyMemory;
import pojo.User;
import utils.DelimiterUtils;

import java.math.BigInteger;
import java.util.Map;

public class MyTask implements Runnable {
    @Override
    public void run() {
        Map<Channel, User> map = NettyMemory.session2UserIds;
        for (Map.Entry<Channel, User> entry : map.entrySet()) {
//          推送战斗消息
            Channel channel = entry.getKey();
            User user = entry.getValue();
            BigInteger userHp = new BigInteger(user.getHp());
            if (userHp.compareTo(new BigInteger("0")) <= 0&&user.getStatus().equals("1")) {
                channel.writeAndFlush(DelimiterUtils.addDelimiter("人物已死亡"));
                user.setHp("0");
                user.setStatus("0");
                NettyMemory.monsterMap.remove(user);
                NettyMemory.eventStatus.put(channel, EventStatus.STOPAREA);
            } else {
                //TODO:解决攻击多只怪物
                if (NettyMemory.monsterMap.containsKey(user)) {
                    Monster monster = NettyMemory.monsterMap.get(user).get(0);
                    BigInteger monsterDamage = new BigInteger(monster.getMonsterSkillList().get(0).getDamage());
                    userHp = userHp.subtract(monsterDamage);
                    String resp = "怪物名称:" + monster.getName()
                            + "-----怪物技能:" + monster.getMonsterSkillList().get(0).getSkillName()
                            + "-----怪物的伤害:" + monster.getMonsterSkillList().get(0).getDamage()
                            + "-----你的剩余血:" + userHp.toString()
                            + "-----你的蓝量：" + user.getMp()
                            + System.getProperty("line.separator");
                    user.setHp(userHp.toString());
                    NettyMemory.session2UserIds.put(channel, user);
                    //TODO:更新用户血量到数据库
                    channel.writeAndFlush(DelimiterUtils.addDelimiter(resp));
                    NettyMemory.eventStatus.put(channel, EventStatus.ATTACK);
                }
            }


//          自动回蓝
            BigInteger userMp = new BigInteger(entry.getValue().getMp());
            BigInteger maxMp = new BigInteger("10000");
            if (userMp.compareTo(maxMp) < 0) {
                userMp = userMp.add(new BigInteger("10"));
                entry.getValue().setMp(userMp.toString());
            }
        }

    }
}
