package task;

import buff.Buff;
import component.BossArea;
import component.Monster;
import config.BuffConfig;
import config.MessageConfig;
import config.StatusConfig;
import event.BuffEvent;
import event.EventStatus;
import event.OutfitEquipmentEvent;
import io.netty.channel.Channel;
import memory.NettyMemory;
import netscape.security.UserTarget;
import packet.PacketType;
import pojo.User;
import skill.MonsterSkill;
import team.Team;
import utils.MessageUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
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

    public BossAttackTask(Monster monster, String teamId, Channel channel, String jobId, ConcurrentHashMap<String, Future> futureMap, OutfitEquipmentEvent outfitEquipmentEvent) {
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
        try {
//          处理最后一名玩家退出,停掉线程
            if (!NettyMemory.bossAreaMap.containsKey(teamId)) {
                Future future = futureMap.remove(jobId);
                future.cancel(true);
                return;
            }
            BossArea bossArea = NettyMemory.bossAreaMap.get(teamId);

            User userTarget = getMaxDamageUser(bossArea);
//          重新锁定boss
            if (monster == null || (monster != null && monster.getStatus().equals(StatusConfig.DEAD))) {
                for (Map.Entry<String, Monster> entry : bossArea.getMap().entrySet()) {
                    if (entry.getValue().getStatus().equals("1")) {
                        if (monster != null) {
                            sendMessageToAll(teamId, "出现第二boss" + entry.getValue().getName());
//                          移除所有小组成员
                            removeMonster(teamId);
                            addMonster(teamId, entry.getValue());
                        }
//                        NettyMemory.monsterBuffEndTime.remove(monster);
                        monster = entry.getValue();
//                        NettyMemory.monsterBuffEndTime.put(monster);
                        break;
                    }
                }
            }

            BigInteger userHp = new BigInteger(userTarget.getHp());
            Channel channelTarget = NettyMemory.userToChannelMap.get(userTarget);
            if (userHp.compareTo(new BigInteger("0")) <= 0) {
                userTarget.setHp("0");
                userTarget.setStatus(StatusConfig.DEAD);
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
                NettyMemory.bossAreaMap.remove(userTarget.getTeamId());
                sendTimeOutToAll(teamId, MessageConfig.BOSSAREATIMEOUT);
                return;
            }
//     战斗胜利
            if (monster != null && monster.getStatus().equals(StatusConfig.DEAD)) {
                successMessToAll(bossArea, channel);
                Future future = futureMap.remove(jobId);
                future.cancel(true);
                return;
            }
//           此处构建最简单的bossAI，就是根据谁的伤害最高打谁
            attack(userTarget, monster);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMonster(String teamId, Monster monster) {
        Map<String, User> map = NettyMemory.teamMap.get(teamId).getUserMap();
        for (Map.Entry<String, User> entry : map.entrySet()) {
            List<Monster> list = new ArrayList<>();
            list.add(monster);
            NettyMemory.monsterMap.put(entry.getValue(), list);
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
            if (!NettyMemory.eventStatus.get(channelTemp).equals(EventStatus.DEADAREA)) {
                NettyMemory.eventStatus.put(channelTemp, EventStatus.STOPAREA);
            }
            channelTemp.writeAndFlush(MessageUtil.turnToPacket(msg));
        }
    }

    private void successMessToAll(BossArea bossArea, Channel channel) {
        Team team = NettyMemory.teamMap.get(bossArea.getTeamId());
        for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
            Channel channelTemp = NettyMemory.userToChannelMap.get(entry.getValue());
            channelTemp.writeAndFlush(MessageUtil.turnToPacket(bossArea.getBossName() + "副本攻略成功，热烈庆祝各位参与的小伙伴"));
            NettyMemory.eventStatus.put(channelTemp, EventStatus.STOPAREA);
            if (NettyMemory.monsterMap.containsKey(entry.getValue())) {
                NettyMemory.monsterMap.remove(entry.getValue());
            }
        }
        outfitEquipmentEvent.getGoods(channel, monster);
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
//      锁定攻击目标
        User userTarget = getMaxDamageUser(bossArea);

//      不断随机合适的boss技能
        MonsterSkill monsterSkill = selectMonsterSkill(monster, userTarget);

        for (Map.Entry<String, User> entry : NettyMemory.teamMap.get(user.getTeamId()).getUserMap().entrySet()) {
            String resp = null;
            if (NettyMemory.eventStatus.get(NettyMemory.userToChannelMap.get(entry.getValue())).equals(EventStatus.STOPAREA)) {
                continue;
            }
            Channel channelTemp = NettyMemory.userToChannelMap.get(entry.getValue());

//           怪物带攻击技能buff
            if (monsterSkill.getBuffMap() != null) {
                for (Map.Entry<String, Integer> entryBuf : monsterSkill.getBuffMap().entrySet()) {
                    if (entryBuf.getKey().equals(BuffConfig.ALLPERSON)) {
//                       全体攻击
                        attackToAll(teamId, monster, monsterSkill);
                        return;
                    }
                    if (entryBuf.getKey().equals(BuffConfig.SLEEPBUFF) && userTarget.getBufferMap().get(BuffConfig.SLEEPBUFF) != 5001) {
//                      减伤buff处理
                        BigInteger monsterSkillDamage = dealDefenseBuff(monsterSkill, userTarget,entry.getValue());


//                      改变用户buff状态，设置用户buff时间
                        Buff buff = NettyMemory.buffMap.get(entryBuf.getValue());
                        userTarget.getBufferMap().put(BuffConfig.SLEEPBUFF, 5001);
                        NettyMemory.userBuffEndTime.get(userTarget).put(BuffConfig.SLEEPBUFF, System.currentTimeMillis() + buff.getKeepTime() * 1000);
                        userTarget.subHp(monsterSkillDamage.toString());
                        channel.writeAndFlush(MessageUtil.turnToPacket("您收到怪物boss击打晕效果,无法使用技能,人物受到" + monsterSkillDamage.toString() + "伤害", PacketType.ATTACKMSG));
                        return;
                    }
                    if (entryBuf.getKey().equals(BuffConfig.POISONINGBUFF) && userTarget.getBufferMap().get(BuffConfig.POISONINGBUFF) != 2001) {
//                     减伤buff处理
                        BigInteger monsterSkillDamage = dealDefenseBuff(monsterSkill, userTarget,entry.getValue());

//                      改变用户buff状态，设置用户buff时间
                        Buff buff = NettyMemory.buffMap.get(entryBuf.getValue());
                        userTarget.getBufferMap().put(BuffConfig.POISONINGBUFF, 2001);
                        NettyMemory.userBuffEndTime.get(userTarget).put(BuffConfig.POISONINGBUFF, System.currentTimeMillis() + buff.getKeepTime() * 1000);
                        userTarget.subHp(monsterSkillDamage.toString());
                        channel.writeAndFlush(MessageUtil.turnToPacket("怪物boss使用中毒绝技,你会持续掉血，人物受到" + monsterSkillDamage.toString() + "伤害", PacketType.ATTACKMSG));
                        return;
                    }
                }
            }

//          减伤buff处理
            BigInteger monsterSkillDamage = dealDefenseBuff(monsterSkill, userTarget, entry.getValue());

            if (entry.getValue().getUsername().equals(userTarget.getUsername())) {
                BigInteger userHp = new BigInteger(userTarget.getHp());
                BigInteger minHp = new BigInteger("0");
                userTarget.subHp(monsterSkillDamage.toString());
                if (userHp.compareTo(minHp) <= 0) {
                    userTarget.setHp("0");
                }
                resp = "怪物的仇恨值在你身上"
                        + "----怪物名称:" + monster.getName()
                        + "-----怪物技能:" + monsterSkill.getSkillName()
                        + "-----怪物的伤害:" + monsterSkill.getDamage()
                        + "-----你的剩余血:" + userTarget.getHp()
                        + "-----你的蓝量" + userTarget.getMp()
                        + "-----怪物血量:" + monster.getValueOfLife()
                        + "----你处于[" + NettyMemory.eventStatus.get(channelTemp) + "]状态";
                //TODO:更新用户血量到数据库
            } else {
                resp = "怪物名称:" + monster.getName()
                        + "-----怪物技能:" + monsterSkill.getSkillName()
                        + "-----怪物的伤害:" + monsterSkill.getDamage()
                        + "-----对" + userTarget.getUsername() + "造成攻击"
                        + "-----你的剩余血:" + entry.getValue().getHp()
                        + "-----你的蓝量" + entry.getValue().getMp()
                        + "-----怪物血量:" + monster.getValueOfLife()
                        + "----你处于[" + NettyMemory.eventStatus.get(channelTemp) + "]状态";
            }
            channelTemp.writeAndFlush(MessageUtil.turnToPacket(resp, PacketType.ATTACKMSG));
        }
    }

    private MonsterSkill selectMonsterSkill(Monster monster, User userTarget) {
        int randomNumber = (int) (Math.random() * monster.getMonsterSkillList().size());
        MonsterSkill monsterSkill = monster.getMonsterSkillList().get(randomNumber);
        if (monsterSkill.getBuffMap() == null) {
            return monsterSkill;
        }

        for (Map.Entry<String, Integer> entry : monsterSkill.getBuffMap().entrySet()) {
            if (entry.getKey().equals(BuffConfig.SLEEPBUFF) && userTarget.getBufferMap().get(BuffConfig.SLEEPBUFF) != 5001) {
                return monsterSkill;
            }
            if (entry.getKey().equals(BuffConfig.POISONINGBUFF) && userTarget.getBufferMap().get(BuffConfig.POISONINGBUFF) != 2001) {
                return monsterSkill;
            }
            if (entry.getKey().equals(BuffConfig.ALLPERSON)) {
                return monsterSkill;
            }
        }
        return selectMonsterSkill(monster, userTarget);
    }

    private BigInteger dealDefenseBuff(MonsterSkill monsterSkill, User user, User target) {
        if (user.getBufferMap().get(BuffConfig.DEFENSEBUFF) != 3000 && user == target) {
            Buff buff = NettyMemory.buffMap.get(user.getBufferMap().get(BuffConfig.DEFENSEBUFF));
            BigInteger mosterSkillDamage = new BigInteger(monsterSkill.getDamage());
            BigInteger buffDefenceDamage = new BigInteger(buff.getInjurySecondValue());
            mosterSkillDamage = mosterSkillDamage.subtract(buffDefenceDamage);
            Channel channelTemp = NettyMemory.userToChannelMap.get(user);
            channelTemp.writeAndFlush(MessageUtil.turnToPacket("人物减伤buff减伤：" + buff.getInjurySecondValue() + "人物剩余血量：" + user.getHp(), PacketType.USERBUFMSG));
            return mosterSkillDamage;
        }
        return new BigInteger(monsterSkill.getDamage());
    }

    private void attackToAll(String teamId, Monster monster, MonsterSkill monsterSkill) {
        Team team = NettyMemory.teamMap.get(teamId);
        for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
            User userTemp = entry.getValue();
            BigInteger monsterSkillDamage = dealDefenseBuff(monsterSkill, userTemp,entry.getValue());
            Channel channelTemp = NettyMemory.userToChannelMap.get(userTemp);
            userTemp.subHp(monsterSkillDamage.toString());
            String resp = "怪物使用了全体攻击技能,对所有人造成攻击:"
                    + "-----怪物技能:" + monsterSkill.getSkillName()
                    + "-----怪物的伤害:" + monsterSkill.getDamage()
                    + "-----你的剩余血:" + userTemp.getHp()
                    + "-----你的蓝量" + userTemp.getMp()
                    + "-----怪物血量:" + monster.getValueOfLife();
            channelTemp.writeAndFlush(MessageUtil.turnToPacket(resp, PacketType.ATTACKMSG));
        }
    }

    private User getMaxDamageUser(BossArea bossArea) {
        BigInteger max = new BigInteger("0");
        User userTarget = null;
        for (Map.Entry<User, String> entry : bossArea.getDamageAll().entrySet()) {
            BigInteger temp = new BigInteger(entry.getValue());
            if (temp.compareTo(max) > 0 && !entry.getKey().getStatus().equals(StatusConfig.DEAD)) {
                max = temp;
                userTarget = entry.getKey();
            }
        }
        if (userTarget == null) {
            for (Map.Entry<String, User> entry : NettyMemory.teamMap.get(bossArea.getTeamId()).getUserMap().entrySet()) {
                if (!entry.getValue().getStatus().equals(StatusConfig.DEAD)) {
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
