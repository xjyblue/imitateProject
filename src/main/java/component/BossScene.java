package component;

import buff.Buff;
import config.BuffConfig;
import config.DeadOrAliveConfig;
import config.MessageConfig;
import event.EventStatus;
import event.OutfitEquipmentEvent;
import io.netty.channel.Channel;
import context.ProjectContext;
import packet.PacketType;
import pojo.User;
import skill.MonsterSkill;
import team.Team;
import test.ExcelUtil;
import utils.MessageUtil;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/11/19 16:24
 * 副本
 */
public class BossScene implements Runnable {

    private String teamId;

    private String name;

    private String bossName;

    private Map<String, Monster> map;

    private Long keepTime;

    private volatile boolean isEnd;

    private volatile boolean isFight;

    private Monster firstMonster;

    private List<String> sequence;

    private Map<String, User> userMap = new ConcurrentHashMap<>();

    private Map<String, Map<String, Monster>> monsters;

    private ConcurrentHashMap<String, Future> futureMap;

    private OutfitEquipmentEvent outfitEquipmentEvent;

    public ConcurrentHashMap<String, Future> getFutureMap() {
        return futureMap;
    }

    public void setFutureMap(ConcurrentHashMap<String, Future> futureMap) {
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

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public Long getKeepTime() {
        return keepTime;
    }

    public void setKeepTime(Long keepTime) {
        this.keepTime = keepTime;
    }

    public Monster getFirstMonster() {
        return firstMonster;
    }

    public void setFirstMonster(Monster firstMonster) {
        this.firstMonster = firstMonster;
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

    public BossScene(OutfitEquipmentEvent outfitEquipmentEvent) {
        this.outfitEquipmentEvent = outfitEquipmentEvent;
        init();
    }

    public void init() {
        sequence = new ArrayList<>();
        sequence.add("A1");
        sequence.add("A2");
        monsters = new HashMap<>();

        Map<String, Monster> monsterMap = new HashMap<>();
        try {
            FileInputStream fis = new FileInputStream(new File("C:\\Users\\server\\IdeaProjects\\imitateProject\\src\\main\\resources\\Monster.xls"));
            LinkedHashMap<String, String> alias = new LinkedHashMap<>();
            alias.put("怪物id", "id");
            alias.put("怪物名称", "name");
            alias.put("怪物类别", "type");
            alias.put("怪物生命值", "valueOfLife");
            alias.put("怪物状态", "status");
            alias.put("怪物技能", "skillIds");
            alias.put("出生地点", "pos");
            alias.put("怪物经验值", "experience");
            List<Monster> monsterList = ExcelUtil.excel2Pojo(fis, Monster.class, alias);

            for (Monster monster : monsterList) {
                if (monster.getPos().equals("A1") || monster.getPos().equals("A2")) {
//                  怪物buff初始化
                    Map<String, Integer> map = new HashMap<>();
                    map.put(BuffConfig.MPBUFF, 1000);
                    map.put(BuffConfig.POISONINGBUFF, 2000);
                    map.put(BuffConfig.DEFENSEBUFF, 3000);
                    monster.setBufMap(map);
//                  初始化每个怪物buff的终止时间
                    Map<String, Long> mapSecond = new HashMap<>();
                    mapSecond.put(BuffConfig.MPBUFF, 1000l);
                    mapSecond.put(BuffConfig.POISONINGBUFF, 2000l);
                    mapSecond.put(BuffConfig.DEFENSEBUFF, 3000l);
                    ProjectContext.monsterBuffEndTime.put(monster, mapSecond);
//                  怪物buff初始化结束

//                  初始化怪物技能
                    String s[] = monster.getSkillIds().split("-");
                    List<MonsterSkill> list = new ArrayList<>();
                    for (int i = 0; i < s.length; i++) {
                        list.add(ProjectContext.monsterSkillMap.get(Integer.parseInt(s[i])));
                    }
                    monster.setMonsterSkillList(list);
                    monster.setAttackEndTime(0L);
                    monster.setBuffRefreshTime(0L);

                    if (!monsters.containsKey(monster.getPos())) {
                        monsters.put(monster.getPos(), new HashMap<>());
                        monsters.get(monster.getPos()).put(monster.getName(), monster);
                    } else {
                        monsters.get(monster.getPos()).put(monster.getName(), monster);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.bossName = "天灵魔殿";
        this.isEnd = false;
        this.isFight = false;
        this.damageAll = new HashMap<User, String>();
    }


    @Override
    public void run() {
//      场景内所有用户的频帧
        userFrequence();
//      场景内所有怪物,就是怪物攻击人
        if (this.isFight) {
            monsterFrequence();
        }
    }

    private void monsterBuffRefresh(Monster monster) {
        if (monster.getBuffRefreshTime() < System.currentTimeMillis()) {
            monster.setBuffRefreshTime(System.currentTimeMillis() + 1000);
        } else {
            return;
        }

        if (monster != null && monster.getBufMap().containsKey(BuffConfig.POISONINGBUFF) && monster.getBufMap().get(BuffConfig.POISONINGBUFF) != 2000) {
            Long endTime = ProjectContext.monsterBuffEndTime.get(monster).get(BuffConfig.POISONINGBUFF);
            if (System.currentTimeMillis() < endTime && !monster.getValueOfLife().equals("0")) {
                Buff buff = ProjectContext.buffMap.get(monster.getBufMap().get(BuffConfig.POISONINGBUFF));

                monster.subLife(new BigInteger(buff.getAddSecondValue()));
//               处理中毒扣死
                if (new BigInteger(monster.getValueOfLife()).compareTo(new BigInteger("0")) < 0) {
                    monster.setValueOfLife("0");
                    monster.setStatus("0");
                    monster.getBufMap().put(BuffConfig.POISONINGBUFF, 2000);
                }

//              怪物中毒将buff推送给所有玩家
                if (monster.getType().equals(Monster.TYPEOFBOSS)) {
                    sendMessageToAll("怪物中毒掉血[" + buff.getAddSecondValue() + "]+怪物剩余血量为:" + monster.getValueOfLife(), PacketType.MONSTERBUFMSG);
                } else {
                    sendMessageToAll("怪物中毒掉血[" + buff.getAddSecondValue() + "]+怪物剩余血量为:" + monster.getValueOfLife(), PacketType.MONSTERBUFMSG);
//                    channel.writeAndFlush(MessageUtil.turnToPacket("怪物中毒掉血[" + buff.getAddSecondValue() + "]+怪物剩余血量为:" + monster.getValueOfLife(), PacketType.MONSTERBUFMSG));
                }
            } else {
                monster.getBufMap().put(BuffConfig.POISONINGBUFF, 2000);
            }
        }

    }

    private void userFrequence() {
//      持续触发玩家帧频
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            User user = entry.getValue();
            user.keepCall();
        }
    }

    //      怪物帧频
    private void monsterFrequence() {
        Map<String, Monster> monsterMap = this.getMonsters().get(this.getSequence().get(0));
        try {
//          时间截止 战斗结束
            if (System.currentTimeMillis() > ProjectContext.endBossAreaTime.get(teamId)) {
                ProjectContext.bossAreaMap.remove(teamId);
                sendTimeOutToAll(teamId, MessageConfig.BOSSAREATIMEOUT);
                Future future = futureMap.remove(teamId);
                future.cancel(true);
                moveAllUser();
                return;
            }


//              处理最后一名玩家退出,停掉线程
            if (!ProjectContext.bossAreaMap.containsKey(teamId)) {
                Future future = futureMap.remove(teamId);
                future.cancel(true);
                ProjectContext.bossAreaMap.remove(teamId);
                return;
            }

            for (Map.Entry<String, Monster> monsterEntry : monsterMap.entrySet()) {
                Monster monster = monsterEntry.getValue();

                monsterBuffRefresh(monster);
                BossScene bossScene = ProjectContext.bossAreaMap.get(teamId);
                User userTarget = getMaxDamageUser(bossScene);
//              判断某个场景的boss是否都死光，否则重新锁定boss
                if (checkIfCheckArea(monsterMap, bossScene)) {
                    if (bossScene.getSequence().size() > 1) {
                        bossScene.getSequence().remove(0);

//                      切换到第二场景的怪物群
                        monsterMap = bossScene.getMonsters().get(bossScene.getSequence().get(0));
                        for (Map.Entry<String, Monster> entry : monsterMap.entrySet()) {
                            if (entry.getValue().getStatus().equals("1")) {
                                if (monster != null) {
                                    sendMessageToAll("出现第二boss" + entry.getValue().getName(),null);
//                                  移除所有小组成员
                                    removeMonster(teamId);
                                    addMonster(teamId, entry.getValue());
                                }
                                monster = entry.getValue();
                                break;
                            }
                        }
                    } else {
//                      boss场景只剩下一个并且场景下boss死光，游戏结束
                        successMessToAll(bossScene, monster);
                        Future future = futureMap.remove(teamId);
                        future.cancel(true);
                        moveAllUser();
                        return;
                    }
                }

                BigInteger userHp = new BigInteger(userTarget.getHp());
                Channel channelTarget = ProjectContext.userToChannelMap.get(userTarget);
                if (userHp.compareTo(new BigInteger("0")) <= 0) {
                    userTarget.setHp("0");
                    userTarget.setStatus(DeadOrAliveConfig.DEAD);
//                  人物战斗中死亡
                    ProjectContext.eventStatus.put(channelTarget, EventStatus.DEADAREA);
                    channelTarget.writeAndFlush(MessageUtil.turnToPacket("你已死亡"));
//                  把人物从战斗场景移除到初始场景
                    Scene scene = ProjectContext.sceneMap.get("0");
                    scene.getUserMap().put(userTarget.getUsername(), userTarget);
//                  全队死亡检查，是否副本失败
                    if (checkAllDead()) {
                        Future future = futureMap.remove(teamId);
                        future.cancel(true);
                        failMessageToAll(bossScene);
                        userMap.remove(userTarget.getUsername());
                        return;
                    }
                    userMap.remove(userTarget.getUsername());
//                  人物死亡全队提示
                    oneDeadMessageToAll(bossScene, monster, userTarget);
//                  之前的人物死了，重选一次目标对象
                    userTarget = getMaxDamageUser(bossScene);
                }


//              此处构建最简单的bossAI，就是根据谁的伤害最高打谁
                if (!monster.getStatus().equals(DeadOrAliveConfig.DEAD) && monster.getAttackEndTime() < System.currentTimeMillis()) {
                    monster.setAttackEndTime(System.currentTimeMillis() + 1000);
                    attack(userTarget, monster);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void moveAllUser() {
        Iterator<Map.Entry<String, User>> it = userMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, User> entry = it.next();
            User user = entry.getValue();
//          将用户回退会刚才所在的场景
            Scene scene = ProjectContext.sceneMap.get(user.getPos());
            scene.getUserMap().put(user.getUsername(), user);
//          移除该场景的用户
            it.remove();
        }

    }


    private boolean checkIfCheckArea(Map<String, Monster> monsterMap, BossScene bossScene) {
        for (Map.Entry<String, Monster> monsterEntry : monsterMap.entrySet()) {
            if (monsterEntry.getValue().getStatus().equals(DeadOrAliveConfig.ALIVE)) {
                return false;
            }
        }
        return true;
    }

    private void addMonster(String teamId, Monster monster) {
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            Map<Integer, Monster> monsterMap = new HashMap<>();
            monsterMap.put(monster.getId(), monster);
            ProjectContext.userToMonsterMap.put(entry.getValue(), monsterMap);
        }
    }

    private void removeMonster(String teamId) {
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            if (ProjectContext.userToMonsterMap.containsKey(entry.getValue())) {
                ProjectContext.userToMonsterMap.remove(entry.getValue());
            }
        }
    }

    private void sendMessageToAll(String msg, String type) {
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            Channel channelTemp = ProjectContext.userToChannelMap.get(entry.getValue());
            if (type == null) {
                channelTemp.writeAndFlush(MessageUtil.turnToPacket(msg));
            } else {
                channelTemp.writeAndFlush(MessageUtil.turnToPacket(msg, type));
            }
        }
    }

    private void sendTimeOutToAll(String teamId, String msg) {
        Map<String, User> map = ProjectContext.teamMap.get(teamId).getUserMap();
        for (Map.Entry<String, User> entry : map.entrySet()) {
            Channel channelTemp = ProjectContext.userToChannelMap.get(entry.getValue());
            if (!ProjectContext.eventStatus.get(channelTemp).equals(EventStatus.DEADAREA)) {
                ProjectContext.eventStatus.put(channelTemp, EventStatus.STOPAREA);
            }
            channelTemp.writeAndFlush(MessageUtil.turnToPacket(msg));
        }
    }

    private void successMessToAll(BossScene bossScene, Monster monster) {
        Team team = ProjectContext.teamMap.get(bossScene.getTeamId());
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            Channel channelTemp = ProjectContext.userToChannelMap.get(entry.getValue());
            channelTemp.writeAndFlush(MessageUtil.turnToPacket(bossScene.getBossName() + "副本攻略成功，热烈庆祝各位参与的小伙伴"));
            ProjectContext.eventStatus.put(channelTemp, EventStatus.STOPAREA);
            if (ProjectContext.userToMonsterMap.containsKey(entry.getValue())) {
                ProjectContext.userToMonsterMap.remove(entry.getValue());
            }
            outfitEquipmentEvent.getGoods(channelTemp, monster);
        }
        ProjectContext.bossAreaMap.remove(team.getTeamId());
    }

    private void oneDeadMessageToAll(BossScene bossScene, Monster monster, User user) {
        Map<String, User> userMap = ProjectContext.teamMap.get(bossScene.getTeamId()).getUserMap();
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            Channel channelTemp = ProjectContext.userToChannelMap.get(entry.getValue());
            channelTemp.writeAndFlush(MessageUtil.turnToPacket(user.getUsername() + "被" + monster.getName() + "打死"));
        }
    }

    private void failMessageToAll(BossScene bossScene) {
        ProjectContext.bossAreaMap.remove(bossScene.getTeamId());
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            Channel channelTemp = ProjectContext.userToChannelMap.get(entry.getValue());
            ProjectContext.eventStatus.put(channelTemp, EventStatus.DEADAREA);
            channelTemp.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.BOSSFAIL));
        }
    }

    private boolean checkAllDead() {
        for (Map.Entry<String, User> entry : userMap.entrySet()) {
            if (entry.getValue().getStatus().equals("1")) {
                return false;
            }
        }
        return true;
    }


    private void attack(User user, Monster monster) {
        BossScene bossScene = ProjectContext.bossAreaMap.get(user.getTeamId());
//      锁定攻击目标
        User userTarget = getMaxDamageUser(bossScene);
        Channel channelTarget = ProjectContext.userToChannelMap.get(userTarget);
//      不断随机合适的boss技能
        MonsterSkill monsterSkill = selectMonsterSkill(monster, userTarget);

        for (Map.Entry<String, User> entry : ProjectContext.teamMap.get(user.getTeamId()).getUserMap().entrySet()) {
            String resp = null;
            if (ProjectContext.eventStatus.get(ProjectContext.userToChannelMap.get(entry.getValue())).equals(EventStatus.STOPAREA)) {
                continue;
            }
            Channel channelTemp = ProjectContext.userToChannelMap.get(entry.getValue());

//           怪物带攻击技能buff
            if (monsterSkill.getBuffMap() != null) {
                for (Map.Entry<String, Integer> entryBuf : monsterSkill.getBuffMap().entrySet()) {
                    if (entryBuf.getKey().equals(BuffConfig.ALLPERSON)) {
//                       全体攻击
                        attackToAll(teamId, monster, monsterSkill);
                        return;
                    }
                    if (entryBuf.getKey().equals(BuffConfig.SLEEPBUFF) && userTarget.getBuffMap().get(BuffConfig.SLEEPBUFF) != 5001) {
//                      减伤buff处理
                        BigInteger monsterSkillDamage = dealDefenseBuff(monsterSkill, userTarget, entry.getValue());


//                      改变用户buff状态，设置用户buff时间
                        Buff buff = ProjectContext.buffMap.get(entryBuf.getValue());
                        userTarget.getBuffMap().put(BuffConfig.SLEEPBUFF, 5001);
                        ProjectContext.userBuffEndTime.get(userTarget).put(BuffConfig.SLEEPBUFF, System.currentTimeMillis() + buff.getKeepTime() * 1000);
                        userTarget.subHp(monsterSkillDamage.toString());
                        channelTarget.writeAndFlush(MessageUtil.turnToPacket("您收到怪物boss击打晕效果,无法使用技能,人物受到" + monsterSkillDamage.toString() + "伤害", PacketType.ATTACKMSG));
                        return;
                    }
                    if (entryBuf.getKey().equals(BuffConfig.POISONINGBUFF) && userTarget.getBuffMap().get(BuffConfig.POISONINGBUFF) != 2001) {
//                     减伤buff处理
                        BigInteger monsterSkillDamage = dealDefenseBuff(monsterSkill, userTarget, entry.getValue());

//                      改变用户buff状态，设置用户buff时间
                        Buff buff = ProjectContext.buffMap.get(entryBuf.getValue());
                        userTarget.getBuffMap().put(BuffConfig.POISONINGBUFF, 2001);
                        ProjectContext.userBuffEndTime.get(userTarget).put(BuffConfig.POISONINGBUFF, System.currentTimeMillis() + buff.getKeepTime() * 1000);
                        userTarget.subHp(monsterSkillDamage.toString());
                        channelTarget.writeAndFlush(MessageUtil.turnToPacket("怪物boss使用中毒绝技,你会持续掉血，人物受到" + monsterSkillDamage.toString() + "伤害", PacketType.ATTACKMSG));
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
                        + "----你处于[" + ProjectContext.eventStatus.get(channelTemp) + "]状态";
            } else {
                resp = "怪物名称:" + monster.getName()
                        + "-----怪物技能:" + monsterSkill.getSkillName()
                        + "-----怪物的伤害:" + monsterSkill.getDamage()
                        + "-----对" + userTarget.getUsername() + "造成攻击"
                        + "-----你的剩余血:" + entry.getValue().getHp()
                        + "-----你的蓝量" + entry.getValue().getMp()
                        + "-----怪物血量:" + monster.getValueOfLife()
                        + "----你处于[" + ProjectContext.eventStatus.get(channelTemp) + "]状态";
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
            if (entry.getKey().equals(BuffConfig.SLEEPBUFF) && userTarget.getBuffMap().get(BuffConfig.SLEEPBUFF) != 5001) {
                return monsterSkill;
            }
            if (entry.getKey().equals(BuffConfig.POISONINGBUFF) && userTarget.getBuffMap().get(BuffConfig.POISONINGBUFF) != 2001) {
                return monsterSkill;
            }
            if (entry.getKey().equals(BuffConfig.ALLPERSON)) {
                return monsterSkill;
            }
        }
        return selectMonsterSkill(monster, userTarget);
    }

    private BigInteger dealDefenseBuff(MonsterSkill monsterSkill, User user, User target) {
        if (user.getBuffMap().get(BuffConfig.DEFENSEBUFF) != 3000 && user == target) {
            Buff buff = ProjectContext.buffMap.get(user.getBuffMap().get(BuffConfig.DEFENSEBUFF));
            BigInteger mosterSkillDamage = new BigInteger(monsterSkill.getDamage());
            BigInteger buffDefenceDamage = new BigInteger(buff.getInjurySecondValue());
            mosterSkillDamage = mosterSkillDamage.subtract(buffDefenceDamage);
            Channel channelTemp = ProjectContext.userToChannelMap.get(user);
            channelTemp.writeAndFlush(MessageUtil.turnToPacket("人物减伤buff减伤：" + buff.getInjurySecondValue() + "人物剩余血量：" + user.getHp(), PacketType.USERBUFMSG));
            return mosterSkillDamage;
        }
        return new BigInteger(monsterSkill.getDamage());
    }

    private void attackToAll(String teamId, Monster monster, MonsterSkill monsterSkill) {
        Team team = ProjectContext.teamMap.get(teamId);
        for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
            User userTemp = entry.getValue();
            BigInteger monsterSkillDamage = dealDefenseBuff(monsterSkill, userTemp, entry.getValue());
            Channel channelTemp = ProjectContext.userToChannelMap.get(userTemp);
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

    private User getMaxDamageUser(BossScene bossScene) {
        BigInteger max = new BigInteger("0");
        User userTarget = null;
        for (Map.Entry<User, String> entry : bossScene.getDamageAll().entrySet()) {
            BigInteger temp = new BigInteger(entry.getValue());
            if (temp.compareTo(max) > 0 && !entry.getKey().getStatus().equals(DeadOrAliveConfig.DEAD)) {
                max = temp;
                userTarget = entry.getKey();
            }
        }
        if (userTarget == null) {
            for (Map.Entry<String, User> entry : ProjectContext.teamMap.get(bossScene.getTeamId()).getUserMap().entrySet()) {
                if (!entry.getValue().getStatus().equals(DeadOrAliveConfig.DEAD)) {
                    userTarget = entry.getValue();
                }
            }
        }
        return userTarget;
    }

}
