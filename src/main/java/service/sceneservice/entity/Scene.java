package service.sceneservice.entity;

import service.buffservice.service.MonsterBuffService;
import service.caculationservice.service.HpCaculationService;
import core.component.monster.Monster;
import service.npcservice.entity.Npc;
import service.buffservice.entity.Buff;

import service.sceneservice.entity.parent.AbstractScene;
import service.buffservice.entity.BuffConstant;
import core.config.GrobalConfig;
import core.config.MessageConfig;
import service.buffservice.service.AttackBuffService;
import core.ChannelStatus;
import service.rewardservice.service.RewardService;
import core.factory.MonsterFactory;
import io.netty.channel.Channel;
import core.context.ProjectContext;
import core.packet.PacketType;
import pojo.User;
import utils.MessageUtil;
import utils.SpringContextUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName Scene
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class Scene extends AbstractScene implements Runnable {
    /**
     * 场景的id
     */
    private String id;
    /**
     * 场景的名字
     */
    private String name;
    /**
     * 场景关联的ids
     */
    private String sceneIds;
    /**
     * 场景关联的npc
     */
    private String npcS;
    /**
     * 场景关联的怪物
     */
    private String monsterS;
    /**
     * 关联的场景
     */
    private Set<String> sceneSet;
    /**
     * 场景下的npc
     */
    private List<Npc> npcs;
    /**
     * 场景下的怪物
     */
    private List<Monster> monsters;
    /**
     * 场景下的玩家
     */
    private String needLevel;

    private RewardService rewardService;

    private AttackBuffService attackBuffService;

    private HpCaculationService hpCaculationService;

    private MonsterBuffService monsterBuffService;

    /**
     * 注入一些要用的单例
     */
    @Override
    public void preConstruct() {
        AttackBuffService attackBuffService = SpringContextUtil.getBean("attackBuffService");
        this.attackBuffService = attackBuffService;
        RewardService rewardService = SpringContextUtil.getBean("rewardService");
        this.rewardService = rewardService;
        HpCaculationService hpCaculationService = SpringContextUtil.getBean("hpCaculationService");
        this.hpCaculationService = hpCaculationService;
        MonsterBuffService monsterBuffService = SpringContextUtil.getBean("monsterBuffService");
        this.monsterBuffService = monsterBuffService;
    }

    private Map<String, User> userMap = new ConcurrentHashMap<>();

    public String getNeedLevel() {
        return needLevel;
    }

    public void setNeedLevel(String needLevel) {
        this.needLevel = needLevel;
    }

    public String getMonsterS() {
        return monsterS;
    }

    public void setMonsterS(String monsterS) {
        this.monsterS = monsterS;
    }

    public String getNpcS() {
        return npcS;
    }

    public void setNpcS(String npcS) {
        this.npcS = npcS;
    }

    public String getSceneIds() {
        return sceneIds;
    }

    public void setSceneIds(String sceneIds) {
        this.sceneIds = sceneIds;
    }

    public RewardService getOutfitEquipmentEvent() {
        return rewardService;
    }

    public HpCaculationService getHpCaculationService() {
        return hpCaculationService;
    }

    public void setHpCaculationService(HpCaculationService hpCaculationService) {
        this.hpCaculationService = hpCaculationService;
    }

    public void setOutfitEquipmentEvent(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    public AttackBuffService getAttackBuffService() {
        return attackBuffService;
    }

    public void setAttackBuffService(AttackBuffService attackBuffService) {
        this.attackBuffService = attackBuffService;
    }

    public Map<String, User> getUserMap() {
        return userMap;
    }

    public void setUserMap(Map<String, User> userMap) {
        this.userMap = userMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Monster> getMonsters() {
        return monsters;
    }

    public void setMonsters(List<Monster> monsters) {
        this.monsters = monsters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Npc> getNpcs() {
        return npcs;
    }

    public void setNpcs(List<Npc> npcs) {
        this.npcs = npcs;
    }

    public Set<String> getSceneSet() {
        return sceneSet;
    }

    public void setSceneSet(Set<String> sceneSet) {
        this.sceneSet = sceneSet;
    }


    @Override
    public void run() {
        try {
            while (true) {
//      场景内所有用户的频帧
                userFrequence();
//      场景内所有怪物帧频
                monsterFrequence();
//      每隔一段事件持续刷新帧频
                Thread.sleep(50);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void monsterFrequence() {
//      推送战斗消息,触发处于战斗状态的用户 场景内所有用户在战斗的怪物
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            User user = entry.getValue();

            Channel channel = ProjectContext.userToChannelMap.get(user);
            if (ProjectContext.eventStatus.containsKey(channel) && ProjectContext.eventStatus.get(channel).equals(ChannelStatus.ATTACK)) {
                try {
                    if (ProjectContext.userToMonsterMap.containsKey(user)) {
                        Monster monster = null;
                        for (Map.Entry<Integer, Monster> monsterEntry : ProjectContext.userToMonsterMap.get(user).entrySet()) {
                            monster = monsterEntry.getValue();
                        }
//                      怪物buff刷新
                        monsterBuffService.monsterBuffRefresh(monster, channel);
//                      检查怪物是否死亡
                        if (checkMonsterStatus(channel, monster, user)) {
                            continue;
                        }

//                      怪物攻击的频率，不要跟着初始帧频
                        if (monster.getAttackEndTime() > System.currentTimeMillis()) {
                            continue;
                        } else {
                            monster.setAttackEndTime(System.currentTimeMillis() + 1000);
                        }

                        BigInteger monsterDamage = new BigInteger(monster.getMonsterSkillList().get(0).getDamage());
//                      怪物攻击对人物造成伤害处理buff处理
                        monsterDamage = attackBuffService.monsterAttackDefendBuff(monsterDamage, user);
                        Buff buff = ProjectContext.buffMap.get(user.getBuffMap().get(BuffConstant.DEFENSEBUFF));
                        if (user.getBuffMap().get(BuffConstant.DEFENSEBUFF) != 3000) {
                            channel.writeAndFlush(MessageUtil.turnToPacket("人物减伤buff减伤：" + buff.getInjurySecondValue() + "人物剩余血量：" + user.getHp(), PacketType.USERBUFMSG));
                        }

//                      用户扣血，死亡状态处理
                        hpCaculationService.subUserHp(user, monsterDamage.toString());

//                      刷新攻击信息
                        String resp = "怪物名称:" + monster.getName()
                                + "-----怪物技能:" + monster.getMonsterSkillList().get(0).getSkillName()
                                + "-----怪物的伤害:" + monster.getMonsterSkillList().get(0).getDamage()
                                + "-----你的剩余血:" + user.getHp()
                                + "-----你的蓝量" + user.getMp()
                                + "-----怪物血量:" + monster.getValueOfLife();
                        channel.writeAndFlush(MessageUtil.turnToPacket(resp, PacketType.ATTACKMSG));
                        if (user.getHp().equals(GrobalConfig.MINVALUE)) {
                            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.SELECTLIVEWAY, PacketType.ATTACKMSG));
                            return;
                        }
                        ProjectContext.eventStatus.put(channel, ChannelStatus.ATTACK);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private boolean checkMonsterStatus(Channel channel, Monster monster, User user) throws IOException {
        if (monster != null && new BigInteger(monster.getValueOfLife()).compareTo(new BigInteger(GrobalConfig.MINVALUE)) <= 0) {
            monster.setValueOfLife(GrobalConfig.MINVALUE);
            monster.setStatus(GrobalConfig.DEAD);
            ProjectContext.userToMonsterMap.remove(user);
            broadcastMessage(monster.getName() + "已死亡", null);
            channel.writeAndFlush(MessageUtil.turnToPacket("怪物已死亡", PacketType.ATTACKMSG));
            List<Monster> monsters = ProjectContext.sceneMap.get(user.getPos()).monsters;
            monsters.remove(monster);
//          填充因buff中毒而死的怪物
            MonsterFactory monsterFactory = SpringContextUtil.getBean("monsterFactory");
            monsters.add(monsterFactory.getMonster(Integer.parseInt(ProjectContext.sceneMap.get(user.getPos() + "").getMonsterS())));

            ProjectContext.eventStatus.put(channel, ChannelStatus.COMMONSCENE);
            rewardService.getGoods(channel, monster);
            return true;
        }
        return false;
    }

    private void userFrequence() {
//      持续触发玩家帧频
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            User user = entry.getValue();
            user.keepCall();
        }
    }

    /**
     * 场景内广播消息
     *
     * @param msg
     * @param packetType
     */
    private void broadcastMessage(String msg, String packetType) {
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            Channel channelT = ProjectContext.userToChannelMap.get(entry.getValue());
            if (packetType == null) {
                channelT.writeAndFlush(MessageUtil.turnToPacket(msg));
            } else {
                channelT.writeAndFlush(MessageUtil.turnToPacket(msg, packetType));
            }
        }
    }
}
