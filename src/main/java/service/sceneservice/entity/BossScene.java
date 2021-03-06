package service.sceneservice.entity;

import config.impl.excel.BossSceneConfigResourceLoad;
import config.impl.excel.BuffResourceLoad;
import config.impl.excel.SceneResourceLoad;
import core.component.boss.BossSceneConfig;
import core.component.monster.Monster;
import core.packet.ServerPacket;
import service.attackservice.util.AttackUtil;
import service.broadcastservice.service.BroadcastService;
import service.buffservice.entity.Buff;
import service.buffservice.service.MonsterBuffService;
import service.caculationservice.service.AttackDamageCaculationService;
import service.caculationservice.service.HpCaculationService;
import com.google.common.collect.Lists;
import core.base.parent.BaseThread;
import service.buffservice.entity.BuffConstant;
import core.config.GrobalConfig;
import core.config.MessageConfig;
import core.channel.ChannelStatus;
import service.rewardservice.service.RewardService;
import core.factory.MonsterFactory;
import io.netty.channel.Channel;
import pojo.User;
import core.component.monster.MonsterSkill;
import service.teamservice.entity.Team;
import service.teamservice.entity.TeamCache;
import service.userservice.service.UserService;
import utils.ChannelUtil;
import utils.MessageUtil;
import utils.SpringContextUtil;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName BossScene
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class BossScene extends BaseThread implements Runnable {
    /**
     * id
     */
    private String bossSceneId;
    /**
     * 关联队伍
     */
    private String teamId;
    /**
     * 名字
     */
    private String name;
    /**
     * boss副本名字
     */
    private String bossName;
    /**
     * 当前场景怪物集合
     */
    private Map<String, Monster> map;
    /**
     * 副本持续时间
     */
    private Long keepTime;
    /**
     * 是否开打
     */
    private volatile boolean isFight;
    /**
     * 副本结束的标识
     */
    private volatile boolean isEnd;
    /**
     * 解决用户手动离线和副本场景线程自己关闭的线程安全问题
     */
    private Lock bossSceneLock = new ReentrantLock();
    /**
     * 最终奖励
     */
    private String finalReward;
    /**
     * boss关数
     */
    private List<String> sequence;
    /**
     * 注入共计计算
     */
    private AttackDamageCaculationService attackDamageCaculationService;
    /**
     * 场景用户
     */
    private Map<String, User> userMap = new ConcurrentHashMap<>();
    /**
     * 场景所有怪物
     */
    private Map<String, Map<String, Monster>> monsters;
    /**
     * 停到线程用的
     */
    private Map<String, Future> futureMap;
    /**
     * 注入奖励
     */
    private RewardService rewardService;
    /**
     * 注入血量计算
     */
    private HpCaculationService hpCaculationService;
    /**
     * 注入怪物工厂
     */
    private MonsterFactory monsterFactory;
    /**
     * 每个关卡是否要求组队
     */
    private Set<String> needMordMen;
    /**
     * 怪物buff
     */
    private MonsterBuffService monsterBuffService;

    /**
     * 广播服务
     *
     * @return
     */
    private BroadcastService broadcastService;
    /**
     * 用户服务
     */
    private UserService userService;
    /**
     * 副本终止时间
     */
    private Long bossSceneEndTime;

    public Long getBossSceneEndTime() {
        return bossSceneEndTime;
    }

    public void setBossSceneEndTime(Long bossSceneEndTime) {
        this.bossSceneEndTime = bossSceneEndTime;
    }

    public Lock getBossSceneLock() {
        return bossSceneLock;
    }

    public void setBossSceneLock(Lock bossSceneLock) {
        this.bossSceneLock = bossSceneLock;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public Set<String> getNeedMordMen() {
        return needMordMen;
    }

    public void setNeedMordMen(Set<String> needMordMen) {
        this.needMordMen = needMordMen;
    }

    public String getFinalReward() {
        return finalReward;
    }

    public void setFinalReward(String finalReward) {
        this.finalReward = finalReward;
    }

    public MonsterFactory getMonsterFactory() {
        return monsterFactory;
    }

    public void setMonsterFactory(MonsterFactory monsterFactory) {
        this.monsterFactory = monsterFactory;
    }

    public Map<String, Future> getFutureMap() {
        return futureMap;
    }

    public void setFutureMap(Map<String, Future> futureMap) {
        this.futureMap = futureMap;
    }

    public Map<String, User> getUserMap() {
        return userMap;
    }

    public void setUserMap(Map<String, User> userMap) {
        this.userMap = userMap;
    }

    public List<String> getSequence() {
        return sequence;
    }

    public void setSequence(List<String> sequence) {
        this.sequence = sequence;
    }

    public Map<String, Map<String, Monster>> getMonsters() {
        return monsters;
    }

    public void setMonsters(Map<String, Map<String, Monster>> monsters) {
        this.monsters = monsters;
    }

    public boolean isFight() {
        return isFight;
    }

    public void setFight(boolean fight) {
        isFight = fight;
    }

    private Map<User, String> damageAll;

    public Map<User, String> getDamageAll() {
        return damageAll;
    }

    public void setDamageAll(Map<User, String> damageAll) {
        this.damageAll = damageAll;
    }

    public Long getKeepTime() {
        return keepTime;
    }

    public void setKeepTime(Long keepTime) {
        this.keepTime = keepTime;
    }

    public Map<String, Monster> getMap() {
        return map;
    }

    public void setMap(Map<String, Monster> map) {
        this.map = map;
    }

    public String getBossName() {
        return bossName;
    }

    public void setBossName(String bossName) {
        this.bossName = bossName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public BossScene(String teamId, String bossSceneId) {
        this.teamId = teamId;
        this.bossSceneId = bossSceneId;
//      填充boss副本配置
        BossSceneConfig bossSceneConfig = BossSceneConfigResourceLoad.bossSceneConfigMap.get(this.bossSceneId);
//      填充时间
        this.keepTime = bossSceneConfig.getKeeptime();
//      填充副本关数
        sequence = new ArrayList<>();
        String[] sequences = bossSceneConfig.getSequences().split("-");
        monsters = new HashMap<>();
        for (String sequenceT : sequences) {
            sequence.add(sequenceT);
//          为每个副本关数填充怪物
            try {
                List<Monster> list = monsterFactory.getMonsterByArea(sequenceT);
                for (Monster monster : list) {
                    if (!monsters.containsKey(sequenceT)) {
                        monsters.put(sequenceT, new HashMap<>());
                        monsters.get(sequenceT).put(monster.getName(), monster);
                    } else {
                        monsters.get(sequenceT).put(monster.getName(), monster);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//      填充必须组队的副本
        String[] needMoreMens = bossSceneConfig.getNeedMoreMen().split("-");
        this.needMordMen = new HashSet<>();
        for (String needMoreMenT : needMoreMens) {
            this.needMordMen.add(needMoreMenT);
        }
//      副本关闭标识，哪个线程抢到就是谁的
        this.isEnd = false;
//      副本的名称
        this.bossName = bossSceneConfig.getBossSceneName();
//      其他参数
        this.isFight = false;
//      副本的伤害伤害计算
        this.damageAll = new HashMap<User, String>();
//      最后一击奖励场景歌
        this.finalReward = bossSceneConfig.getFinalReward();
    }

    /**
     * 注入需要的bean
     */
    @Override
    public void preConstruct() {
        this.attackDamageCaculationService = SpringContextUtil.getBean("attackDamageCaculationService");
        this.hpCaculationService = SpringContextUtil.getBean("hpCaculationService");
        this.rewardService = SpringContextUtil.getBean("rewardService");
        this.monsterBuffService = SpringContextUtil.getBean("monsterBuffService");
        this.monsterFactory = SpringContextUtil.getBean("monsterFactory");
        this.userService = SpringContextUtil.getBean("userService");
        this.broadcastService = SpringContextUtil.getBean("broadcastService");
    }

    /**
     * 场景线程持续心跳
     */
    @Override
    public void run() {
//      场景内所有用户的频帧
        if (!userFrequence()) {
            return;
        }
//      场景内所有怪物,就是怪物攻击人
        if (this.isFight) {
            monsterFrequence();
        }
    }

    /**
     * 消费用户命令包
     */
    private boolean userFrequence() {
//      解决玩家为0时线程回收问题
        if (userMap.size() == 0) {
            stopBossScene();
            return false;
        }
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            User user = entry.getValue();
            user.keepCall();
        }
        return true;
    }

    /**
     * 怪物帧频
     */
    private void monsterFrequence() {
        Map<String, Monster> monsterMap = this.getMonsters().get(this.getSequence().get(0));
        try {
//          时间截止 战斗结束
            if (checkIfTimeOut()) {
                return;
            }

            for (Map.Entry<String, Monster> monsterEntry : monsterMap.entrySet()) {
                Monster monster = monsterEntry.getValue();
//              刷新怪物的buff
                monsterBuffService.bossBuffRefresh(monster, teamId);
                User userTarget = getMaxDamageUser(this);
//              判断某个场景的boss是否都死光，否则重新锁定boss
                if (checkIfCheckArea(monsterMap, this)) {
                    if (this.getSequence().size() > 1) {
//                      切换到下一场景的怪物群
                        monsterMap = checkOutNewSceneMonsters();
//                      判断下个场景boss检查挑战者是否为多人挑战，不为多人不生成boss
                        if (checkIfMoreMen(monsterMap, userTarget)) {
                            return;
                        }
                    } else {
//                      boss场景只剩下一个并且场景下boss死光，游戏结束
                        successMessToAll(this, monster);
                        stopBossScene();
                        moveAllUser();
                        return;
                    }
                }
//              解决死掉的某个玩家
                solveOneDead(monster);
//              怪物攻击,控制频率不要太高
                if (!monster.getStatus().equals(GrobalConfig.DEAD) && monster.getAttackEndTime() < System.currentTimeMillis()) {
                    monster.setAttackEndTime(System.currentTimeMillis() + 1000);
                    attack(monster);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停掉boss场景
     */
    private void stopBossScene() {
        Future future = futureMap.remove(teamId);
        future.cancel(true);
        BossSceneConfigResourceLoad.bossAreaMap.remove(teamId);
    }

    /**
     * 解决死掉的某个玩家
     *
     * @param monster
     */
    private void solveOneDead(Monster monster) {
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            User userTarget = entry.getValue();
            Integer userHp = Integer.parseInt(userTarget.getHp());
            Channel channelTarget = ChannelUtil.userToChannelMap.get(userTarget);
            if (userHp <= GrobalConfig.ZERO) {
                userTarget.setHp(GrobalConfig.MINVALUE);
                userTarget.setStatus(GrobalConfig.DEAD);
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData("你已死亡，可按Y进行复活");
                MessageUtil.sendMessage(channelTarget, builder.build());
//              人物死亡全队提示
                oneDeadMessageToAll(monster, userTarget);
//              全队死亡检查，是否副本失败
                if (checkAllDead()) {
                    stopBossScene();
                    failMessageToAll();
                }
//                  初始化死亡人物的buff
                userService.initUserBuff(userTarget);
//                  移除怪物所针对的boss
                AttackUtil.removeAllMonster(userTarget);
//                  人物战斗中死亡,将人物的状态置为死亡
                ChannelUtil.channelStatus.put(channelTarget, ChannelStatus.DEADSCENE);
//                  把人物从战斗场景移除到初始场景
                Scene scene = SceneResourceLoad.sceneMap.get(GrobalConfig.STARTSCENE);
                scene.getUserMap().put(userTarget.getUsername(), userTarget);
//                  将人物的伤害置为0
                this.getDamageAll().put(userTarget, GrobalConfig.MINVALUE);
//                  人物死亡场景移除人物
                userMap.remove(userTarget.getUsername());
            }
        }
    }

    /**
     * 切换其他场景怪物群
     *
     * @return
     */
    private Map<String, Monster> checkOutNewSceneMonsters() {
        Map<String, Monster> monsterMap;
        this.getSequence().remove(0);
        monsterMap = this.getMonsters().get(this.getSequence().get(0));
        this.setFight(false);
//      获取新场景boss名字
        String bossMessage = getNewBossName(monsterMap);
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(bossMessage);
        broadcastService.sendMessageToAll(teamId, builder.build());
        return monsterMap;
    }

    /**
     * 检查副本是否需要多人挑战
     *
     * @param monsterMap
     * @param userTarget
     * @return
     */
    private boolean checkIfMoreMen(Map<String, Monster> monsterMap, User userTarget) {
        for (Map.Entry<String, Monster> entry : monsterMap.entrySet()) {
//          检查怪物场景是否为要求多人场景
            if (this.needMordMen.contains(entry.getValue().getPos())) {
                if (TeamCache.teamMap.get(teamId).getUserMap().size() == 1) {
//                  人数不够告诉玩家不能继续下去了
                    Future future = futureMap.remove(teamId);
                    future.cancel(true);
//                  提示
                    Channel channelTarget = ChannelUtil.userToChannelMap.get(userTarget);
                    ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                    builder.setData(MessageConfig.NO_ENOUGH_MAN_TO_FIGHT);
                    MessageUtil.sendMessage(channelTarget, builder.build());
//                  移除玩家
                    BossSceneConfigResourceLoad.bossAreaMap.remove(teamId);
                    moveAllUser();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 新生成怪物的怪物名
     *
     * @param monsterMap
     * @return
     */
    private String getNewBossName(Map<String, Monster> monsterMap) {
        String bossMessage = "出现新场景boss:";
        for (Map.Entry<String, Monster> entry : monsterMap.entrySet()) {
            if (entry.getValue().getStatus().equals(GrobalConfig.ALIVE)) {
                bossMessage += (entry.getValue().getName() + " ");
//              移除所有小组成员之前的目标monster
                removeMonster();
            }
        }
        return bossMessage;
    }

    /**
     * 检查副本是否超时
     *
     * @return
     */
    private boolean checkIfTimeOut() {
        if (System.currentTimeMillis() > bossSceneEndTime) {
            BossSceneConfigResourceLoad.bossAreaMap.remove(teamId);
            sendTimeOutToAll(teamId, MessageConfig.BOSS_AREA_TIME_OUT);
            stopBossScene();
            moveAllUser();
            return true;
        }
        return false;
    }

    /**
     * 移除副本中战斗的用户
     */
    private void moveAllUser() {
        Iterator<Map.Entry<String, User>> it = userMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, User> entry = it.next();
            User user = entry.getValue();
//          移除该用户所有的战斗怪物信息
            AttackUtil.removeAllMonster(user);
            if (user.getStatus().equals(GrobalConfig.DEAD)) {
                continue;
            }
//          将用户回退回刚才所在的场景
            Scene scene = SceneResourceLoad.sceneMap.get(user.getPos());
            scene.getUserMap().put(user.getUsername(), user);
//          改变用户渠道转态
            Channel channelT = ChannelUtil.userToChannelMap.get(user);
            ChannelUtil.channelStatus.put(channelT, ChannelStatus.COMMONSCENE);
//          移除该场景的用户
            it.remove();
        }
    }


    private boolean checkIfCheckArea(Map<String, Monster> monsterMap, BossScene bossScene) {
        for (Map.Entry<String, Monster> monsterEntry : monsterMap.entrySet()) {
            if (monsterEntry.getValue().getStatus().equals(GrobalConfig.ALIVE)) {
                return false;
            }
        }
        return true;
    }

    private void removeMonster() {
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            AttackUtil.removeAllMonster(entry.getValue());
        }
    }

    /**
     * 战斗时间结束
     *
     * @param teamId
     * @param msg
     */
    private void sendTimeOutToAll(String teamId, String msg) {
        Map<String, User> map = TeamCache.teamMap.get(teamId).getUserMap();
        for (Map.Entry<String, User> entry : map.entrySet()) {
            Channel channelTemp = ChannelUtil.userToChannelMap.get(entry.getValue());
//          战斗时间结束初始化所有人的buff
            User userTemp = entry.getValue();
            userService.initUserBuff(userTemp);
            if (!ChannelUtil.channelStatus.get(channelTemp).equals(ChannelStatus.DEADSCENE)) {
                ChannelUtil.channelStatus.put(channelTemp, ChannelStatus.COMMONSCENE);
            }
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(msg);
            MessageUtil.sendMessage(channelTemp, builder.build());
        }
    }

    /**
     * 战斗胜利
     *
     * @param bossScene
     * @param monster
     */
    private void successMessToAll(BossScene bossScene, Monster monster) {
        Team team = TeamCache.teamMap.get(bossScene.getTeamId());
        for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
            Channel channelTemp = ChannelUtil.userToChannelMap.get(entry.getValue());
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(bossScene.getBossName() + "副本攻略成功，热烈庆祝各位参与的小伙伴");
            MessageUtil.sendMessage(channelTemp, builder.build());
            ChannelUtil.channelStatus.put(channelTemp, ChannelStatus.COMMONSCENE);
//          初始化所有人的buff
            User userTemp = entry.getValue();
            userService.initUserBuff(userTemp);
            AttackUtil.removeAllMonster(entry.getValue());
            rewardService.getGoods(channelTemp, monster);
        }
        BossSceneConfigResourceLoad.bossAreaMap.remove(team.getTeamId());
    }

    private void oneDeadMessageToAll(Monster monster, User user) {
        Team team = TeamCache.teamMap.get(teamId);
        for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
            Channel channelTemp = ChannelUtil.userToChannelMap.get(entry.getValue());
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(user.getUsername() + "被" + monster.getName() + "打死");
            MessageUtil.sendMessage(channelTemp, builder.build());
        }
    }

    private void failMessageToAll() {
        Team team = TeamCache.teamMap.get(teamId);
        for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
            Channel channelTemp = ChannelUtil.userToChannelMap.get(entry.getValue());
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.BOSS_FAIL);
            MessageUtil.sendMessage(channelTemp, builder.build());
        }
    }

    private boolean checkAllDead() {
        if (userMap.size() == 0) {
            return false;
        }
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            if (entry.getValue().getStatus().equals(GrobalConfig.ALIVE)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 怪物攻击人
     *
     * @param monster
     */
    private void attack(Monster monster) {
        BossScene bossScene = BossSceneConfigResourceLoad.bossAreaMap.get(teamId);
//      锁定攻击目标
        User userTarget = getMaxDamageUser(bossScene);
//      不断随机合适的boss技能
        MonsterSkill monsterSkill = selectMonsterSkill(monster, userTarget);

        for (Map.Entry<String, User> entry : TeamCache.teamMap.get(teamId).getUserMap().entrySet()) {
            String resp = null;
            if (ChannelUtil.channelStatus.get(ChannelUtil.userToChannelMap.get(entry.getValue())).equals(ChannelStatus.COMMONSCENE)) {
                continue;
            }
            Channel channelTemp = ChannelUtil.userToChannelMap.get(entry.getValue());

//          怪物带攻击技能buff
            if (monsterSkill.getBuffMap() != null) {
//              检查是否为集体攻击,锁定我们的攻击目标
                List<User> listTarget = null;
                if (monsterSkill.getBuffMap().containsKey(BuffConstant.ALLPERSON)) {
                    listTarget = Lists.newArrayList(userMap.values());
                } else {
                    listTarget = Lists.newArrayList();
                    listTarget.add(userTarget);
                }

//              走特殊(带buff的)技能不用往下走
                boolean flag = false;
                for (Map.Entry<String, Integer> entryBuf : monsterSkill.getBuffMap().entrySet()) {
                    if (entryBuf.getKey().equals(BuffConstant.SLEEPBUFF) && userTarget.getBuffMap().get(BuffConstant.SLEEPBUFF) != 5001) {
                        sleepAttack(listTarget, monster, monsterSkill, entryBuf.getValue(), flag);
                        flag = true;
                    } else if (entryBuf.getKey().equals(BuffConstant.POISONINGBUFF) && userTarget.getBuffMap().get(BuffConstant.POISONINGBUFF) != 2001) {
                        poisonAttack(listTarget, monster, monsterSkill, entryBuf.getValue(), flag);
                        flag = true;
                    } else {
//                      全体攻击
                        commonAttack(listTarget, monster, monsterSkill, flag);
                        flag = true;
                    }
                }
                if (flag) {
                    return;
                }
            }
//          普通单体攻击
//          减伤buff处理
            Integer monsterSkillDamage = attackDamageCaculationService.dealDefenseBuff(monsterSkill, userTarget, entry.getValue());

            if (entry.getValue().getUsername().equals(userTarget.getUsername())) {
//              扣血
                hpCaculationService.subUserHp(userTarget, String.valueOf(monsterSkillDamage));
                resp = "怪物的仇恨值在你身上"
                        + "----怪物名称:" + monster.getName()
                        + "-----怪物技能:" + monsterSkill.getSkillName()
                        + "-----怪物的伤害:" + monsterSkill.getDamage()
                        + "-----你的剩余血:" + userTarget.getHp()
                        + "-----你的蓝量" + userTarget.getMp()
                        + "-----怪物血量:" + monster.getValueOfLife()
                        + "----你处于[" + ChannelUtil.channelStatus.get(channelTemp) + "]状态";
            } else {
                resp = "怪物名称:" + monster.getName()
                        + "-----怪物技能:" + monsterSkill.getSkillName()
                        + "-----怪物的伤害:" + monsterSkill.getDamage()
                        + "-----对" + userTarget.getUsername() + "造成攻击"
                        + "-----你的剩余血:" + entry.getValue().getHp()
                        + "-----你的蓝量" + entry.getValue().getMp()
                        + "-----怪物血量:" + monster.getValueOfLife()
                        + "----你处于[" + ChannelUtil.channelStatus.get(channelTemp) + "]状态";
            }
            ServerPacket.AttackResp.Builder builder = ServerPacket.AttackResp.newBuilder();
            builder.setData(resp);
            MessageUtil.sendMessage(channelTemp, builder.build());
        }


    }

    private void sleepAttack(List<User> listTarget, Monster monster, MonsterSkill monsterSkill, Integer buffId, boolean flag) {
        Integer monsterSkillDamage = Integer.parseInt(monsterSkill.getDamage());
        for (User user : listTarget) {
            if (!flag) {
//              减伤buff处理
                monsterSkillDamage = attackDamageCaculationService.dealDefenseBuff(monsterSkill, user, user);
            }
//           改变用户buff状态，设置用户buff时间
            Buff buff = BuffResourceLoad.buffMap.get(buffId);
            user.getBuffMap().put(BuffConstant.SLEEPBUFF, 5001);
            user.getUserBuffEndTimeMap().put(BuffConstant.SLEEPBUFF, System.currentTimeMillis() + buff.getKeepTime() * 1000);
            hpCaculationService.subUserHp(user, monsterSkillDamage.toString());
            Channel channelTarget = ChannelUtil.userToChannelMap.get(user);
            ServerPacket.AttackResp.Builder builder = ServerPacket.AttackResp.newBuilder();
            builder.setData("您收到怪物boss击打晕效果,无法使用技能,人物受到" + monsterSkillDamage.toString() + "伤害");
            MessageUtil.sendMessage(channelTarget, builder.build());
        }
    }

    private void poisonAttack(List<User> listTarget, Monster monster, MonsterSkill monsterSkill, Integer buffId, boolean flag) {
        Integer monsterSkillDamage = Integer.parseInt(monsterSkill.getDamage());
        for (User user : listTarget) {
            if (!flag) {
//              减伤buff处理
                monsterSkillDamage = attackDamageCaculationService.dealDefenseBuff(monsterSkill, user, user);
            }
//           改变用户buff状态，设置用户buff时间
            Buff buff = BuffResourceLoad.buffMap.get(buffId);
            user.getBuffMap().put(BuffConstant.POISONINGBUFF, 2001);
            user.getUserBuffEndTimeMap().put(BuffConstant.POISONINGBUFF, System.currentTimeMillis() + buff.getKeepTime() * 1000);
            hpCaculationService.subUserHp(user, monsterSkillDamage.toString());
            Channel channelTarget = ChannelUtil.userToChannelMap.get(user);
            ServerPacket.AttackResp.Builder builder = ServerPacket.AttackResp.newBuilder();
            builder.setData("怪物boss使用中毒绝技,你会持续掉血，人物受到" + monsterSkillDamage.toString() + "伤害");
            MessageUtil.sendMessage(channelTarget, builder.build());
        }
    }

    private MonsterSkill selectMonsterSkill(Monster monster, User userTarget) {
        int randomNumber = (int) (Math.random() * monster.getMonsterSkillList().size());
        MonsterSkill monsterSkill = monster.getMonsterSkillList().get(randomNumber);
        if (monsterSkill.getBuffMap() == null) {
            return monsterSkill;
        }

        for (Map.Entry<String, Integer> entry : monsterSkill.getBuffMap().entrySet()) {
            if (entry.getKey().equals(BuffConstant.SLEEPBUFF) && userTarget.getBuffMap().get(BuffConstant.SLEEPBUFF) != 5001) {
                return monsterSkill;
            }
            if (entry.getKey().equals(BuffConstant.POISONINGBUFF) && userTarget.getBuffMap().get(BuffConstant.POISONINGBUFF) != 2001) {
                return monsterSkill;
            }
            if (entry.getKey().equals(BuffConstant.ALLPERSON)) {
                return monsterSkill;
            }
        }
        return selectMonsterSkill(monster, userTarget);
    }

    /**
     * 普通攻击
     *
     * @param list
     * @param monster
     * @param monsterSkill
     */
    private void commonAttack(List<User> list, Monster monster, MonsterSkill monsterSkill, boolean flag) {
        Integer monsterSkillDamage = Integer.parseInt(monsterSkill.getDamage());
        for (User user : list) {
            if (!flag) {
                monsterSkillDamage = attackDamageCaculationService.dealDefenseBuff(monsterSkill, user, user);
            }
            Channel channelTemp = ChannelUtil.userToChannelMap.get(user);
            hpCaculationService.subUserHp(user, monsterSkillDamage.toString());
            String resp = "怪物使用了全体攻击技能,对所有人造成攻击:"
                    + "-----怪物技能:" + monsterSkill.getSkillName()
                    + "-----怪物的伤害:" + monsterSkill.getDamage()
                    + "-----你的剩余血:" + user.getHp()
                    + "-----你的蓝量" + user.getMp()
                    + "-----怪物血量:" + monster.getValueOfLife();
            ServerPacket.AttackResp.Builder builder = ServerPacket.AttackResp.newBuilder();
            builder.setData(resp);
            MessageUtil.sendMessage(channelTemp, builder.build());
        }
    }

    /**
     * 选取攻击的对象
     *
     * @param bossScene
     * @return
     */
    private User getMaxDamageUser(BossScene bossScene) {
//      选取嘲讽对象
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            if (entry.getValue().getBuffMap().get(BuffConstant.TAUNTBUFF) != GrobalConfig.TAUNT_DEFAULTVALUE) {
                return entry.getValue();
            }
        }
//      无嘲讽对象选取最大伤害
        Integer max = GrobalConfig.ZERO;
        User userTarget = null;
        for (Map.Entry<User, String> entry : bossScene.getDamageAll().entrySet()) {
            Integer temp = Integer.parseInt(entry.getValue());
            if (temp >= max && !entry.getKey().getStatus().equals(GrobalConfig.DEAD) && userMap.containsKey(entry.getKey().getUsername())) {
                max = temp;
                userTarget = entry.getKey();
            }
        }
//      这里是为了解决如果某部分玩家都没有进行攻击，需要重新锁定目标的时候就应该从副本中找，而不是从最大伤害中找
        if (userTarget == null) {
            for (Map.Entry<String, User> entry : TeamCache.teamMap.get(bossScene.getTeamId()).getUserMap().entrySet()) {
                if (!entry.getValue().getStatus().equals(GrobalConfig.DEAD) && userMap.containsKey(entry.getValue().getUsername())) {
                    userTarget = entry.getValue();
                }
            }
        }
        return userTarget;
    }

}
