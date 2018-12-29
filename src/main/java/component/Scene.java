package component;

import buff.Buff;
import caculation.HpCaculation;

import component.parent.PScene;
import config.BuffConfig;
import config.GrobalConfig;
import config.MessageConfig;
import event.BuffEvent;
import event.EventStatus;
import event.OutfitEquipmentEvent;
import factory.MonsterFactory;
import io.netty.channel.Channel;
import context.ProjectContext;
import packet.PacketType;
import pojo.User;
import utils.MessageUtil;
import utils.SpringContextUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Scene extends PScene implements Runnable {

    private String id;

    private String name;

    private String sceneIds;

    private String npcS;

    private String monsterS;
    //  关联的场景
    private Set<String> sceneSet;
    //  场景下的npc
    private List<NPC> npcs;
    //  场景下的怪物
    private List<Monster> monsters;
    //  场景下的玩家
    private String needLevel;

    private OutfitEquipmentEvent outfitEquipmentEvent;

    private BuffEvent buffEvent;

    private HpCaculation hpCaculation;

    //注入一些要用的单例
    public void init() {
        BuffEvent buffEvent = SpringContextUtil.getBean("buffEvent");
        this.buffEvent = buffEvent;
        OutfitEquipmentEvent outfitEquipmentEvent = SpringContextUtil.getBean("outfitEquipmentEvent");
        this.outfitEquipmentEvent = outfitEquipmentEvent;
        HpCaculation hpCaculation = SpringContextUtil.getBean("hpCaculation");
        this.hpCaculation = hpCaculation;
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

    public OutfitEquipmentEvent getOutfitEquipmentEvent() {
        return outfitEquipmentEvent;
    }

    public HpCaculation getHpCaculation() {
        return hpCaculation;
    }

    public void setHpCaculation(HpCaculation hpCaculation) {
        this.hpCaculation = hpCaculation;
    }

    public void setOutfitEquipmentEvent(OutfitEquipmentEvent outfitEquipmentEvent) {
        this.outfitEquipmentEvent = outfitEquipmentEvent;
    }

    public BuffEvent getBuffEvent() {
        return buffEvent;
    }

    public void setBuffEvent(BuffEvent buffEvent) {
        this.buffEvent = buffEvent;
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

    public List<NPC> getNpcs() {
        return npcs;
    }

    public void setNpcs(List<NPC> npcs) {
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
//      推送战斗消息,触发处于战斗状态的用户

//      场景内所有用户在战斗的怪物
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            User user = entry.getValue();

            Channel channel = ProjectContext.userToChannelMap.get(user);
            if (user.isIfOnline() && ProjectContext.eventStatus.containsKey(channel) && ProjectContext.eventStatus.get(channel).equals(EventStatus.ATTACK)) {
                try {
                    if (ProjectContext.userToMonsterMap.containsKey(user)) {
                        Monster monster = null;
                        for (Map.Entry<Integer, Monster> monsterEntry : ProjectContext.userToMonsterMap.get(user).entrySet()) {
                            monster = monsterEntry.getValue();
                        }
//                      怪物buff刷新
                        monsterBuffRefresh(monster, channel);
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
                        monsterDamage = buffEvent.defendBuff(monsterDamage, user, channel);
                        Buff buff = ProjectContext.buffMap.get(user.getBuffMap().get(BuffConfig.DEFENSEBUFF));
                        if (user.getBuffMap().get(BuffConfig.DEFENSEBUFF) != 3000) {
                            channel.writeAndFlush(MessageUtil.turnToPacket("人物减伤buff减伤：" + buff.getInjurySecondValue() + "人物剩余血量：" + user.getHp(), PacketType.USERBUFMSG));
                        }

//                      用户扣血，死亡状态处理
                        hpCaculation.subUserHp(user, monsterDamage.toString());

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
                        ProjectContext.eventStatus.put(channel, EventStatus.ATTACK);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void monsterBuffRefresh(Monster monster, Channel channel) {
        if (monster.getBuffRefreshTime() < System.currentTimeMillis()) {
            monster.setBuffRefreshTime(System.currentTimeMillis() + 1000);
        } else {
            return;
        }

        if (monster != null && monster.getBufMap().containsKey(BuffConfig.POISONINGBUFF) && monster.getBufMap().get(BuffConfig.POISONINGBUFF) != 2000) {
            Long endTime = ProjectContext.monsterBuffEndTime.get(monster).get(BuffConfig.POISONINGBUFF);
            if (System.currentTimeMillis() < endTime && !monster.getValueOfLife().equals(GrobalConfig.MINVALUE)) {
                Buff buff = ProjectContext.buffMap.get(monster.getBufMap().get(BuffConfig.POISONINGBUFF));

                monster.subLife(new BigInteger(buff.getAddSecondValue()));
//               处理中毒扣死
                if (new BigInteger(monster.getValueOfLife()).compareTo(new BigInteger(GrobalConfig.MINVALUE)) < 0) {
                    monster.setValueOfLife(GrobalConfig.MINVALUE);
                    monster.setStatus(GrobalConfig.DEAD);
                    monster.getBufMap().put(BuffConfig.POISONINGBUFF, 2000);
                }
                channel.writeAndFlush(MessageUtil.turnToPacket("怪物中毒掉血[" + buff.getAddSecondValue() + "]+怪物剩余血量为:" + monster.getValueOfLife(), PacketType.MONSTERBUFMSG));
            } else {
                monster.getBufMap().put(BuffConfig.POISONINGBUFF, 2000);
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

            ProjectContext.eventStatus.put(channel, EventStatus.STOPAREA);
            outfitEquipmentEvent.getGoods(channel, monster);
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

    //  场景内广播消息
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
