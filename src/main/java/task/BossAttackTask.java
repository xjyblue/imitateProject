package task;

import component.BossArea;
import component.Monster;
import event.EventStatus;
import io.netty.channel.Channel;
import memory.NettyMemory;
import pojo.User;
import team.Team;
import utils.DelimiterUtils;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/11/19 20:18
 */
public class BossAttackTask implements Runnable {

    private Channel channel;

    private String jobId;

    private ConcurrentHashMap<String, Future> futureMap;

    private String teamId;

    public BossAttackTask(String teamId, Channel channel, String jobId, ConcurrentHashMap<String, Future> futureMap) {
        this.channel = channel;
        this.jobId = jobId;
        this.futureMap = futureMap;
        this.teamId = teamId;
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
        User user = NettyMemory.session2UserIds.get(channel);
        BossArea bossArea = NettyMemory.bossAreaMap.get(user.getTeamId());
        User userTarget = getMaxDamageUser(bossArea);
        BigInteger userHp = new BigInteger(userTarget.getHp());
        if (userHp.compareTo(new BigInteger("0")) <= 0 ) {
            userTarget.setHp("0");
            userTarget.setStatus("0");
            NettyMemory.monsterMap.remove(user);
            NettyMemory.eventStatus.put(channel, EventStatus.BOSSAREA);
//          全队死亡检查，是否副本失败
            if (checkAllDead(bossArea,userTarget)) {
                Future future = futureMap.remove(jobId);
                future.cancel(true);
                failMessageToAll(bossArea);
                return;
            }
//          人物死亡全队提示
            oneDeadMessageToAll(bossArea, userTarget);
//          之前的人物死了，重选一次目标对象
            userTarget = getMaxDamageUser(bossArea);
        }

//        时间截止 战斗结束
        if (System.currentTimeMillis() > NettyMemory.endBossAreaTime.get(teamId)) {
            if (bossArea.isEnd()) {
                return;
            }
            bossArea.setEnd(true);
            Future future = futureMap.remove(jobId);
            future.cancel(true);
            NettyMemory.bossAreaMap.remove(user.getTeamId());
//          改变小组所有队员的副本状态
            changeEventStatus(user, EventStatus.STOPAREA);
            return;
        }
//        推送战斗消息
        for(Map.Entry<String,Monster> entry:bossArea.getMap().entrySet()){
            Monster monster = entry.getValue();
//            战斗胜利
            if (monster != null &&monster.getStatus().equals("0")) {
                successMessToAll(bossArea);
                Future future = futureMap.remove(jobId);
                future.cancel(true);
                return;
            }
//           此处构建最简单的bossAI，就是根据谁的伤害最高打谁
            attack(user, monster);
        }
    }

    private void successMessToAll(BossArea bossArea) {
        Team team = NettyMemory.teamMap.get(bossArea.getTeamId());
        for(Map.Entry<String,User> entry:team.getUserMap().entrySet()){
            Channel channelTemp = NettyMemory.userToChannelMap.get(entry.getValue());
            channelTemp.writeAndFlush(DelimiterUtils.turnToPacket(bossArea.getBossName() + "副本攻略成功，热烈庆祝各位参与的小伙伴"));
            NettyMemory.eventStatus.put(channelTemp,EventStatus.STOPAREA);
            if(NettyMemory.monsterMap.containsKey(entry.getValue())){
                NettyMemory.monsterMap.remove(entry.getValue());
            }
        }
        NettyMemory.bossAreaMap.remove(team.getTeamId());
    }

    private void oneDeadMessageToAll(BossArea bossArea, User user) {
        Map<String, User> userMap = NettyMemory.teamMap.get(bossArea.getTeamId()).getUserMap();
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            Channel channelTemp = NettyMemory.userToChannelMap.get(entry.getValue());
            if(NettyMemory.monsterMap.containsKey(entry.getValue())){
                NettyMemory.monsterMap.remove(entry.getValue());
            }
            channelTemp.writeAndFlush(DelimiterUtils.turnToPacket(user.getUsername() + "被" + bossArea.getBossName() + "打死"));
        }
    }

    private void failMessageToAll(BossArea bossArea) {
        Map<String, User> userMap = NettyMemory.teamMap.get(bossArea.getTeamId()).getUserMap();
        NettyMemory.bossAreaMap.remove(bossArea.getTeamId());
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            Channel channelTemp = NettyMemory.userToChannelMap.get(entry.getValue());
            NettyMemory.eventStatus.put(channelTemp,EventStatus.STOPAREA);
            channelTemp.writeAndFlush(DelimiterUtils.turnToPacket("挑战失败，人物已死光,可按f重新挑战"));
        }
    }

    private boolean checkAllDead(BossArea bossArea,User userTarget) {
        for (Map.Entry<String, User> entry : NettyMemory.teamMap.get(bossArea.getTeamId()).getUserMap().entrySet()) {
            if (entry.getValue().getStatus().equals("1")&&entry.getValue()!=userTarget) {
                return false;
            }
        }
        return true;
    }


    private void attack(User user, Monster monster) {
        BossArea bossArea = NettyMemory.bossAreaMap.get(user.getTeamId());
        User userTarget = getMaxDamageUser(bossArea);

        BigInteger monsterDamage = new BigInteger(monster.getMonsterSkillList().get(0).getDamage());
        for (Map.Entry<String, User> entry : NettyMemory.teamMap.get(user.getTeamId()).getUserMap().entrySet()) {
            String resp = null;
            if (entry.getValue().getUsername().equals(userTarget.getUsername())) {
                BigInteger userHp = new BigInteger(userTarget.getHp());
                BigInteger minHp = new BigInteger("0");
                userHp = userHp.subtract(monsterDamage);
                userTarget.setHp(userHp.toString());
                if(userHp.compareTo(minHp)<=0){
                    userTarget.setHp("0");
                }
                resp = "怪物的仇恨值在你身上"
                        + "----怪物名称:" + monster.getName()
                        + "-----怪物技能:" + monster.getMonsterSkillList().get(0).getSkillName()
                        + "-----怪物的伤害:" + monster.getMonsterSkillList().get(0).getDamage()
                        + "-----你的剩余血:" + userTarget.getHp()
                        + "-----你的蓝量" + userTarget.getMp()
                        + "-----怪物血量:" + monster.getValueOfLife()
                        + System.getProperty("line.separator");
                //TODO:更新用户血量到数据库
            } else {
                resp = "怪物名称:" + monster.getName()
                        + "-----怪物技能:" + monster.getMonsterSkillList().get(0).getSkillName()
                        + "-----怪物的伤害:" + monster.getMonsterSkillList().get(0).getDamage()
                        + "-----对" + userTarget.getUsername() + "造成攻击"
                        + "-----你的剩余血:" + entry.getValue().getHp()
                        + "-----你的蓝量" + entry.getValue().getMp()
                        + "-----怪物血量:" + monster.getValueOfLife()
                        + System.getProperty("line.separator");
            }
            Channel channelTemp = NettyMemory.userToChannelMap.get(entry.getValue());
            channelTemp.writeAndFlush(DelimiterUtils.turnToPacket(resp));
        }
    }

    private User getMaxDamageUser(BossArea bossArea) {
        BigInteger max = new BigInteger("0");
        User userTarget = null;
        for (Map.Entry<User, String> entry : bossArea.getDamageAll().entrySet()) {
            BigInteger temp = new BigInteger(entry.getValue());
            if (temp.compareTo(max) > 0 && !entry.getKey().getStatus().equals("0")) {
                max = temp;
                userTarget = entry.getKey();
            }
        }
        if(userTarget==null){
            for (Map.Entry<String, User> entry : NettyMemory.teamMap.get(bossArea.getTeamId()).getUserMap().entrySet()) {
                if(!entry.getValue().getStatus().equals("0")){
                    userTarget = entry.getValue();
                }
            }
        }
        return userTarget;
    }

    private void changeEventStatus(User user, String stoparea) {
        Team team = NettyMemory.teamMap.get(user.getTeamId());
        for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
            Channel channelTemp = NettyMemory.userToChannelMap.get(entry.getValue());
            NettyMemory.eventStatus.put(channelTemp, stoparea);
            if(NettyMemory.monsterMap.containsKey(entry.getValue())){
                NettyMemory.monsterMap.remove(entry.getValue());
            }
            channelTemp.writeAndFlush(DelimiterUtils.turnToPacket("时间结束挑战副本失败,你已退出副本世界，重刷副本请按F"));
        }
        NettyMemory.bossAreaMap.remove(team.getTeamId());
    }
}
