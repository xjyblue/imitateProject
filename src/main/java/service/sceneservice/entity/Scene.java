package service.sceneservice.entity;

import config.impl.excel.BuffResourceLoad;
import config.impl.excel.SceneResourceLoad;
import core.packet.ServerPacket;
import service.buffservice.service.MonsterBuffService;
import service.caculationservice.service.HpCaculationService;
import core.component.monster.Monster;
import service.npcservice.entity.Npc;
import service.buffservice.entity.Buff;

import core.base.parent.BaseThread;
import service.buffservice.entity.BuffConstant;
import core.config.GrobalConfig;
import core.config.MessageConfig;
import service.buffservice.service.AttackBuffService;
import core.channel.ChannelStatus;
import service.rewardservice.service.RewardService;
import core.factory.MonsterFactory;
import io.netty.channel.Channel;
import pojo.User;
import service.userservice.service.UserService;
import utils.ChannelUtil;
import utils.MessageUtil;
import utils.SpringContextUtil;

import java.io.IOException;
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
public class Scene extends BaseThread implements Runnable {
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

    private UserService userService;

    @Override
    public void preConstruct() {
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

    public RewardService getRewardService() {
        return rewardService;
    }

    public void setRewardService(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    public MonsterBuffService getMonsterBuffService() {
        return monsterBuffService;
    }

    public void setMonsterBuffService(MonsterBuffService monsterBuffService) {
        this.monsterBuffService = monsterBuffService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
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
            Channel channel = ChannelUtil.userToChannelMap.get(user);
            if (ChannelUtil.channelStatus.containsKey(channel) && ChannelUtil.channelStatus.get(channel).equals(ChannelStatus.ATTACK)) {
                try {
                    if (user.getUserToMonsterMap().size() > 0) {
                        Monster monster = null;
//                      获取场景每个用户所攻击的怪物
                        for (Map.Entry<Integer, Monster> monsterEntry : user.getUserToMonsterMap().entrySet()) {
                            monster = monsterEntry.getValue();
                        }
//                      怪物buff刷新,广播场景内所有用户
                        monsterBuffService.monsterBuffRefresh(monster, userMap);

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

                        Integer monsterDamage = Integer.parseInt(monster.getMonsterSkillList().get(0).getDamage());
//                      怪物攻击对人物造成伤害处理buff处理
                        monsterDamage = attackBuffService.monsterAttackDefendBuff(monsterDamage, user);
                        Buff buff = BuffResourceLoad.buffMap.get(user.getBuffMap().get(BuffConstant.DEFENSEBUFF));
                        if (user.getBuffMap().get(BuffConstant.DEFENSEBUFF) != 3000) {
                            ServerPacket.UserbufResp.Builder builder = ServerPacket.UserbufResp.newBuilder();
                            builder.setData("人物减伤buff减伤：" + buff.getInjurySecondValue() + "人物剩余血量：" + user.getHp());
                            MessageUtil.sendMessage(channel, builder.build());
                        }

//                      用户扣血，死亡状态处理
                        hpCaculationService.subUserHp(user, monsterDamage.toString());

//                      刷新攻击信息
                        refreshMonsterAttackInfo(monster);
                        if (user.getHp().equals(GrobalConfig.MINVALUE)) {
//                          人物死亡初始化人物buff
                            userService.initUserBuff(user);
                            ServerPacket.NormalResp.Builder builder1 = ServerPacket.NormalResp.newBuilder();
                            builder1.setData(MessageConfig.SELECT_LIVE_WAY);
                            MessageUtil.sendMessage(channel, builder1.build());
                            return;
                        }
                        ChannelUtil.channelStatus.put(channel, ChannelStatus.ATTACK);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 场景内广播怪物攻击消息
     *
     * @param monster
     */
    private void refreshMonsterAttackInfo(Monster monster) {
        for (Map.Entry<String, User> entry : this.getUserMap().entrySet()) {
            User user = entry.getValue();
            Channel channel = ChannelUtil.userToChannelMap.get(user);
            String resp = "怪物名称:" + monster.getName()
                    + "-----怪物技能:" + monster.getMonsterSkillList().get(0).getSkillName()
                    + "-----怪物的伤害:" + monster.getMonsterSkillList().get(0).getDamage()
                    + "-----你的剩余血:" + user.getHp()
                    + "-----你的蓝量" + user.getMp()
                    + "-----怪物血量:" + monster.getValueOfLife();
            ServerPacket.AttackResp.Builder builder = ServerPacket.AttackResp.newBuilder();
            builder.setData(resp);
            MessageUtil.sendMessage(channel, builder.build());
        }
    }

    /**
     * 处理怪物死亡一些状态更新
     * @param channel
     * @param monster
     * @param user
     * @return
     * @throws IOException
     */
    private boolean checkMonsterStatus(Channel channel, Monster monster, User user) throws IOException {
        if (monster != null && Integer.parseInt(monster.getValueOfLife()) <= GrobalConfig.ZERO) {
            monster.setValueOfLife(GrobalConfig.MINVALUE);
            monster.setStatus(GrobalConfig.DEAD);
//          移除用户攻击的怪物
            user.getUserToMonsterMap().remove(monster.getId());
//          提示
            ServerPacket.AttackResp.Builder builder = ServerPacket.AttackResp.newBuilder();
            builder.setData("怪物已死亡");
            MessageUtil.sendMessage(channel, builder.build());
//          移除地图中死掉的怪物
            List<Monster> monsters = SceneResourceLoad.sceneMap.get(user.getPos()).monsters;
            monsters.remove(monster);
//          填充怪物
            MonsterFactory monsterFactory = SpringContextUtil.getBean("monsterFactory");
            monsters.add(monsterFactory.getMonster(Integer.parseInt(SceneResourceLoad.sceneMap.get(user.getPos() + "").getMonsterS())));
//          退出战斗模式初始化人物buff
            ChannelUtil.channelStatus.put(channel, ChannelStatus.COMMONSCENE);
            userService.initUserBuff(user);
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
}
