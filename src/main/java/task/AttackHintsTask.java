package task;

import component.Monster;
import Event.EventStatus;
import io.netty.channel.Channel;
import memory.NettyMemory;
import pojo.User;
import utils.DelimiterUtils;

import java.math.BigInteger;
import java.util.Map;

public class AttackHintsTask implements Runnable{
    @Override
    public void run() {
        Map<Channel, User> map = NettyMemory.session2UserIds;
        for (Map.Entry<Channel, User> entry : map.entrySet()) {
//          推送战斗消息
            Channel channel = entry.getKey();
            User user = entry.getValue();
            BigInteger userHp = new BigInteger(user.getHp());
            if (userHp.compareTo(new BigInteger("0")) <= 0 && user.getStatus().equals("1")) {
                channel.writeAndFlush(DelimiterUtils.addDelimiter("人物已死亡"));
                user.setHp("0");
                user.setStatus("0");
                NettyMemory.monsterMap.remove(user);
                NettyMemory.eventStatus.put(channel, EventStatus.STOPAREA);
            } else {
                //TODO:解决攻击多只怪物
                if (NettyMemory.monsterMap.containsKey(user)) {
                    Monster monster = NettyMemory.monsterMap.get(user).get(0);
                    if(monster!=null&&NettyMemory.monsterMap.get(user).get(0).getStatus().equals("0")) {
                        NettyMemory.monsterMap.remove(user);
                        channel.writeAndFlush(DelimiterUtils.addDelimiter("怪物已死亡"));
                        NettyMemory.eventStatus.put(channel, EventStatus.STOPAREA);
                        return;
                    }
                    BigInteger monsterDamage = new BigInteger(monster.getMonsterSkillList().get(0).getDamage());
                    userHp = userHp.subtract(monsterDamage);
                    String resp = "怪物名称:" + monster.getName()
                            + "-----怪物技能:" + monster.getMonsterSkillList().get(0).getSkillName()
                            + "-----怪物的伤害:" + monster.getMonsterSkillList().get(0).getDamage()
                            + "-----你的剩余血:" + userHp.toString()
                            + "-----你的蓝量：" + user.getMp()
                            + "-----怪物血量:" + monster.getValueOfLife()
                            + System.getProperty("line.separator");
                    user.setHp(userHp.toString());
                    NettyMemory.session2UserIds.put(channel, user);
                    //TODO:更新用户血量到数据库
                    channel.writeAndFlush(DelimiterUtils.addDelimiter(resp));
                    NettyMemory.eventStatus.put(channel, EventStatus.ATTACK);
                }
            }
        }
    }
}
