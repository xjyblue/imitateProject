package task;

import component.Monster;
import config.MessageConfig;
import event.EventStatus;
import io.netty.channel.Channel;
import memory.NettyMemory;
import pojo.User;
import utils.MessageUtil;
import utils.MonsterRebornUtil;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/11/19 21:17
 */
public class MonsterAttackTask implements Runnable {

    private Channel channel;

    private String jobId;

    private ConcurrentHashMap<String, Future> futureMap;

    public MonsterAttackTask(Channel channel, String jobId, ConcurrentHashMap<String, Future> futureMap) {
        this.channel = channel;
        this.jobId = jobId;
        this.futureMap = futureMap;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public ConcurrentHashMap<String, Future> getFutureMap() {
        return futureMap;
    }

    public void setFutureMap(ConcurrentHashMap<String, Future> futureMap) {
        this.futureMap = futureMap;
    }

    @Override
    public void run() {
//          推送战斗消息
        User user = NettyMemory.session2UserIds.get(channel);
        if (NettyMemory.eventStatus.get(channel).equals(EventStatus.STOPAREA)) {
            Future future = futureMap.remove(jobId);
            NettyMemory.monsterMap.remove(user);
            future.cancel(true);
            return;
        }
        //TODO:解决攻击多只怪物
        if (NettyMemory.monsterMap.containsKey(user)) {
            Monster monster = NettyMemory.monsterMap.get(user).get(0);
            if (monster != null && NettyMemory.monsterMap.get(user).get(0).getStatus().equals("0")) {
                NettyMemory.monsterMap.remove(user);
                channel.writeAndFlush(MessageUtil.turnToPacket("怪物已死亡"));
                List<Monster> monsters =  NettyMemory.areaMap.get(user.getPos()).monsters;
                monsters.remove(monster);
//              重新生成新的monster
                MonsterRebornUtil.rebornNewMonster(monsters);
                NettyMemory.eventStatus.put(channel, EventStatus.STOPAREA);
                return;
            }
            BigInteger monsterDamage = new BigInteger(monster.getMonsterSkillList().get(0).getDamage());
            user.subHp(monsterDamage.toString());
            BigInteger userHp = new BigInteger(user.getHp());
            if (userHp.compareTo(new BigInteger("0")) <= 0 && user.getStatus().equals("1")) {
                user.setHp("0");
                user.setStatus("0");
                NettyMemory.monsterMap.remove(user);
                NettyMemory.eventStatus.put(channel, EventStatus.DEADAREA);
            }
            String resp = "怪物名称:" + monster.getName()
                    + "-----怪物技能:" + monster.getMonsterSkillList().get(0).getSkillName()
                    + "-----怪物的伤害:" + monster.getMonsterSkillList().get(0).getDamage()
                    + "-----你的剩余血:" + user.getHp()
                    + "-----你的蓝量" + user.getMp()
                    + "-----怪物血量:" + monster.getValueOfLife();
            channel.writeAndFlush(MessageUtil.turnToPacket(resp));
            if(user.getHp().equals("0")){
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.SELECTLIVEWAY));
                return;
            }
            NettyMemory.eventStatus.put(channel, EventStatus.ATTACK);
        }
    }
}
