package task;

import component.BossArea;
import component.Monster;
import config.MessageConfig;
import event.EventStatus;
import event.OutfitEquipmentEvent;
import io.netty.channel.Channel;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import memory.NettyMemory;
import pojo.User;
import team.Team;
import utils.MessageUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
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

    private Monster monster = null;

    private OutfitEquipmentEvent outfitEquipmentEvent;



    public BossAttackTask(Monster monster,String teamId, Channel channel, String jobId, ConcurrentHashMap<String, Future> futureMap,OutfitEquipmentEvent outfitEquipmentEvent) {
        this.monster = monster;
        this.channel = channel;
        this.jobId = jobId;
        this.futureMap = futureMap;
        this.teamId = teamId;
        this.outfitEquipmentEvent = outfitEquipmentEvent;
    }

    public Monster getMonster() {
        return monster;
    }

    public void setMonster(Monster monster) {
        this.monster = monster;
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

//      重新锁定boss
        if (monster == null || (monster != null && monster.getStatus().equals("0"))) {
            for (Map.Entry<String, Monster> entry : bossArea.getMap().entrySet()) {
                if (entry.getValue().getStatus().equals("1")) {
                    if (monster != null) {
                        sendMessageToAll(teamId, "出现第二boss" + monster.getName());
//                      移除所有小组成员
                        removeMonster(teamId);
                        addMonster(teamId, entry.getValue());
                    }
                    monster = entry.getValue();
                    break;
                }
            }
        }

        BigInteger userHp = new BigInteger(userTarget.getHp());
        Channel channelTarget = NettyMemory.userToChannelMap.get(userTarget);
        if (userHp.compareTo(new BigInteger("0")) <= 0) {
            userTarget.setHp("0");
            userTarget.setStatus("0");
//            NettyMemory.monsterMap.remove(userTarget);

//          人物战斗中死亡
            NettyMemory.eventStatus.put(channelTarget, EventStatus.DEADAREA);
            channelTarget.writeAndFlush(MessageUtil.turnToPacket("你已死亡"));

//          全队死亡检查，是否副本失败
            if (checkAllDead(bossArea, userTarget)) {
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
            sendTimeOutToAll(teamId,MessageConfig.BOSSAREATIMEOUT);
            return;
        }
//     战斗胜利
        if (monster != null && monster.getStatus().equals("0")) {
            successMessToAll(bossArea,channel);
            Future future = futureMap.remove(jobId);
            future.cancel(true);
            return;
        }
//           此处构建最简单的bossAI，就是根据谁的伤害最高打谁
        attack(user, monster);

    }

    private void addMonster(String teamId, Monster monster) {
        Map<String, User> map = NettyMemory.teamMap.get(teamId).getUserMap();
        for (Map.Entry<String, User> entry : map.entrySet()) {
            List<Monster> list = new ArrayList<>();
            list.add(monster);
            NettyMemory.monsterMap.put(entry.getValue(),list);
        }
    }

    private void removeMonster(String teamId) {
        Map<String, User> map = NettyMemory.teamMap.get(teamId).getUserMap();
        for (Map.Entry<String, User> entry : map.entrySet()) {
            if (NettyMemory.monsterMap.containsKey(entry.getValue())) {
                NettyMemory.monsterMap.remove(entry.getValue());
            }
        }
    }

    private void sendMessageToAll(String teamId, String msg) {
        Map<String, User> map = NettyMemory.teamMap.get(teamId).getUserMap();
        for (Map.Entry<String, User> entry : map.entrySet()) {
            Channel channelTemp = NettyMemory.userToChannelMap.get(entry.getValue());
            channelTemp.writeAndFlush(MessageUtil.turnToPacket(msg));
        }
    }

    private void sendTimeOutToAll(String teamId, String msg) {
        Map<String, User> map = NettyMemory.teamMap.get(teamId).getUserMap();
        for (Map.Entry<String, User> entry : map.entrySet()) {
            Channel channelTemp = NettyMemory.userToChannelMap.get(entry.getValue());
            if(!NettyMemory.eventStatus.get(channelTemp).equals(EventStatus.DEADAREA)){
                NettyMemory.eventStatus.put(channelTemp,EventStatus.STOPAREA);
            }
            channelTemp.writeAndFlush(MessageUtil.turnToPacket(msg));
        }
    }

    private void successMessToAll(BossArea bossArea,Channel channel) {
        Team team = NettyMemory.teamMap.get(bossArea.getTeamId());
        for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
            Channel channelTemp = NettyMemory.userToChannelMap.get(entry.getValue());
            channelTemp.writeAndFlush(MessageUtil.turnToPacket(bossArea.getBossName() + "副本攻略成功，热烈庆祝各位参与的小伙伴"));
            NettyMemory.eventStatus.put(channelTemp, EventStatus.STOPAREA);
            if (NettyMemory.monsterMap.containsKey(entry.getValue())) {
                NettyMemory.monsterMap.remove(entry.getValue());
            }
        }
        outfitEquipmentEvent.getGoods(channel,monster);
        NettyMemory.bossAreaMap.remove(team.getTeamId());
    }

    private void oneDeadMessageToAll(BossArea bossArea, User user) {
        Map<String, User> userMap = NettyMemory.teamMap.get(bossArea.getTeamId()).getUserMap();
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            Channel channelTemp = NettyMemory.userToChannelMap.get(entry.getValue());
            channelTemp.writeAndFlush(MessageUtil.turnToPacket(user.getUsername() + "被" + monster.getName() + "打死"));
        }
    }

    private void failMessageToAll(BossArea bossArea) {
        Map<String, User> userMap = NettyMemory.teamMap.get(bossArea.getTeamId()).getUserMap();
        NettyMemory.bossAreaMap.remove(bossArea.getTeamId());
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            Channel channelTemp = NettyMemory.userToChannelMap.get(entry.getValue());
            NettyMemory.eventStatus.put(channelTemp, EventStatus.DEADAREA);
            channelTemp.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.BOSSFAIL));
        }
    }

    private boolean checkAllDead(BossArea bossArea, User userTarget) {
        for (Map.Entry<String, User> entry : NettyMemory.teamMap.get(bossArea.getTeamId()).getUserMap().entrySet()) {
            if (entry.getValue().getStatus().equals("1") && entry.getValue() != userTarget) {
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
            if(NettyMemory.eventStatus.get(NettyMemory.userToChannelMap.get(entry.getValue())).equals(EventStatus.STOPAREA)){
                continue;
            }
            Channel channelTemp = NettyMemory.userToChannelMap.get(entry.getValue());
            if (entry.getValue().getUsername().equals(userTarget.getUsername())) {
                BigInteger userHp = new BigInteger(userTarget.getHp());
                BigInteger minHp = new BigInteger("0");
                userHp = userHp.subtract(monsterDamage);
                userTarget.setHp(userHp.toString());
                if (userHp.compareTo(minHp) <= 0) {
                    userTarget.setHp("0");
                }
                resp = "怪物的仇恨值在你身上"
                        + "----怪物名称:" + monster.getName()
                        + "-----怪物技能:" + monster.getMonsterSkillList().get(0).getSkillName()
                        + "-----怪物的伤害:" + monster.getMonsterSkillList().get(0).getDamage()
                        + "-----你的剩余血:" + userTarget.getHp()
                        + "-----你的蓝量" + userTarget.getMp()
                        + "-----怪物血量:" + monster.getValueOfLife()
                        + "----你处于["+NettyMemory.eventStatus.get(channelTemp)+"]状态"
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
                        + "----你处于["+NettyMemory.eventStatus.get(channelTemp)+"]状态"
                        + System.getProperty("line.separator");
            }
            channelTemp.writeAndFlush(MessageUtil.turnToPacket(resp));
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
        if (userTarget == null) {
            for (Map.Entry<String, User> entry : NettyMemory.teamMap.get(bossArea.getTeamId()).getUserMap().entrySet()) {
                if (!entry.getValue().getStatus().equals("0")) {
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
            if (NettyMemory.monsterMap.containsKey(entry.getValue())) {
                NettyMemory.monsterMap.remove(entry.getValue());
            }
            channelTemp.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.BOSSAREATIMEOUT));
        }
        NettyMemory.bossAreaMap.remove(team.getTeamId());
    }
}
