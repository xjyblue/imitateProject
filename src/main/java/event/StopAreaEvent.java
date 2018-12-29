package event;

import achievement.Achievement;
import achievement.AchievementExecutor;
import caculation.AttackCaculation;
import caculation.HpCaculation;
import caculation.MoneyCaculation;
import caculation.UserbagCaculation;
import component.Scene;
import component.Equipment;
import component.Monster;
import component.NPC;
import component.parent.PGood;
import config.MessageConfig;
import config.GrobalConfig;
import context.ProjectUtil;
import factory.MonsterFactory;
import io.netty.channel.Channel;
import mapper.UserMapper;
import mapper.UserbagMapper;
import context.ProjectContext;
import order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.*;
import skill.UserSkill;
import utils.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.*;

@Component("stopAreaEvent")
public class StopAreaEvent {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommonEvent commonEvent;
    @Autowired
    private AttackCaculation attackCaculation;
    @Autowired
    private TeamEvent teamEvent;
    @Autowired
    private BossEvent bossEvent;
    @Autowired
    private ShopEvent shopEvent;
    @Autowired
    private OutfitEquipmentEvent outfitEquipmentEvent;
    @Autowired
    private ChatEvent chatEvent;
    @Autowired
    private EmailEvent emailEvent;
    @Autowired
    private PKEvent pkEvent;
    @Autowired
    private BuffEvent buffEvent;
    @Autowired
    private MonsterFactory monsterFactory;
    @Autowired
    private TransactionEvent transactionEvent;
    @Autowired
    private LabourUnionEvent labourUnionEvent;
    @Autowired
    private AchievementExecutor achievementExecutor;
    @Autowired
    private FriendEvent friendEvent;
    @Autowired
    private UserbagMapper userbagMapper;
    @Autowired
    private HpCaculation hpCaculation;
    @Autowired
    private UserbagCaculation userbagCaculation;
    @Autowired
    private MoneyCaculation moneyCaculation;

    @Order(orderMsg = "aoi,qb,ub-,qw,fix,wq,ww")
    public void commonMethod(Channel channel, String msg) throws InvocationTargetException, IllegalAccessException {
        ProjectUtil.reflectAnnotation(commonEvent, channel, msg);
    }

    @Order(orderMsg = "iftrade,ytrade,ntrade")
    public void tradeMethod(Channel channel, String msg) throws InvocationTargetException, IllegalAccessException {
        ProjectUtil.reflectAnnotation(transactionEvent, channel, msg);
    }

    @Order(orderMsg = "pk")
    public void pkEvent(Channel channel, String msg) throws InvocationTargetException, IllegalAccessException {
        ProjectUtil.reflectAnnotation(pkEvent, channel, msg);
    }

    @Order(orderMsg = "qemail,send=email,receive=email")
    public void emailEvent(Channel channel, String msg) throws InvocationTargetException, IllegalAccessException {
        ProjectUtil.reflectAnnotation(emailEvent, channel, msg);
    }

    @Order(orderMsg = "chat-,chatAll")
    public void chatEvent(Channel channel, String msg) throws InvocationTargetException, IllegalAccessException {
        ProjectUtil.reflectAnnotation(chatEvent, channel, msg);
    }

    @Order(orderMsg = "bg,qs")
    public void shopEvent(Channel channel, String msg) throws InvocationTargetException, IllegalAccessException {
        ProjectUtil.reflectAnnotation(shopEvent, channel, msg);
    }

    @Order(orderMsg = "p")
    public void friendEvent(Channel channel, String msg) throws InvocationTargetException, IllegalAccessException {
        ProjectUtil.reflectAnnotation(friendEvent, channel, msg);
    }

    @Order(orderMsg = "g")
    public void labourUnionEvent(Channel channel, String msg) throws InvocationTargetException, IllegalAccessException {
        ProjectUtil.reflectAnnotation(labourUnionEvent, channel, msg);
    }

    @Order(orderMsg = "ef")
    public void enterBossArea(Channel channel, String msg) throws InvocationTargetException, IllegalAccessException {
        ProjectUtil.reflectAnnotation(bossEvent, channel, msg);
    }

    @Order(orderMsg = "t")
    public void teamEvent(Channel channel, String msg) throws InvocationTargetException, IllegalAccessException {
        ProjectUtil.reflectAnnotation(teamEvent, channel, msg);
    }

    @Order(orderMsg = "npctalk")
    public void talkMethod(Channel channel, String msg) {
        String[] temp = msg.split("-");
        User user = ProjectContext.session2UserIds.get(channel);
        if (temp.length != 2) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
        } else {
            List<NPC> npcs = ProjectContext.sceneMap.get(ProjectContext.session2UserIds.get(channel).getPos())
                    .getNpcs();
            for (NPC npc : npcs) {
                if (npc.getName().equals(temp[1])) {
                    channel.writeAndFlush(MessageUtil.turnToPacket(npc.getTalk()));
//                      人物任务触发
                    for (Achievementprocess achievementprocess : user.getAchievementprocesses()) {
                        Achievement achievement = ProjectContext.achievementMap.get(achievementprocess.getAchievementid());
                        if (achievementprocess.getType().equals(Achievement.TALKTONPC)) {
                            achievementExecutor.executeTalkNPC(achievementprocess, user, achievement, npc);
                        }
                    }
                    return;
                }
            }
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOFOUNDNPC));
        }
    }

    @Order(orderMsg = "attack-")
    public void attackFirst(Channel channel, String msg) throws IOException {
        User user = ProjectContext.session2UserIds.get(channel);
        String[] temp = msg.split("-");
//      输入的键位是否存在
        if (!(temp.length == 3 && ProjectContext.userskillrelationMap.get(user).containsKey(temp[2]))) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOKEYSKILL));
            return;
        }

//      输入的怪物是否存在
        Monster monster = getMonster(user, temp[1]);
        if (monster == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOFOUNDMONSTER));
            return;
        }

        Userskillrelation userskillrelation = ProjectContext.userskillrelationMap.get(user).get(temp[2]);
        UserSkill userSkill = ProjectContext.skillMap.get(userskillrelation.getSkillid());

//      判断人物MP量是否足够
        Integer userMp = Integer.parseInt(user.getMp());
        Integer skillMp = Integer.parseInt(userSkill.getSkillMp());
        if (userMp < skillMp) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNENOUGHMP));
            return;
        }

//      判断技能冷却
        if (System.currentTimeMillis() < userskillrelation.getSkillcds()) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNSKILLCD));
            return;
        }
//      更新技能cd
        userskillrelation.setSkillcds(System.currentTimeMillis() + userSkill.getAttackCd());

//      蓝量计算
        userMp -= skillMp;
        user.setMp(userMp + "");

//      用户buff计算
        buffEvent.buffSolve(userskillrelation, userSkill, monster, user);

//      攻击伤害计算
        BigInteger attackDamage = new BigInteger(userSkill.getDamage());
        attackDamage = attackCaculation.caculate(user, attackDamage);

//      怪物掉血，生命值计算逻辑
        hpCaculation.subMonsterHp(monster, attackDamage.toString());
        String resp = out(user);
        resp += System.getProperty("line.separator")
                + "[技能]:" + userSkill.getSkillName()
                + System.getProperty("line.separator")
                + "对[" + monster.getName()
                + "]造成了" + attackDamage + "点伤害"
                + System.getProperty("line.separator")
                + "[怪物血量]:" + monster.getValueOfLife()
                + System.getProperty("line.separator")
                + "[消耗蓝量]:" + userSkill.getSkillMp()
                + System.getProperty("line.separator")
                + "[人物剩余蓝量]:" + user.getMp()
                + System.getProperty("line.separator");

        if (monster.getValueOfLife().equals(GrobalConfig.MINVALUE)) {
            resp += System.getProperty("line.separator")
                    + "怪物已死亡";
            channel.writeAndFlush(MessageUtil.turnToPacket(resp));
//          修改怪物状态
            monster.setStatus(GrobalConfig.DEAD);
//          爆装备
            outfitEquipmentEvent.getGoods(channel, monster);
//          移除死掉的怪物
            ProjectContext.sceneMap.get(user.getPos()).getMonsters().remove(monster);
//          生成新的怪物
            Scene scene = ProjectContext.sceneMap.get(user.getPos());
            List<Monster> monsters = monsterFactory.getMonsterByArea(user.getPos());
            for (Monster monsterT : monsters) {
                scene.getMonsters().add(monsterT);
            }
        } else {
//          切换到攻击模式
            ProjectContext.eventStatus.put(channel, EventStatus.ATTACK);
            channel.writeAndFlush(MessageUtil.turnToPacket(resp));
//          记录当前攻击的目标
            Map<Integer, Monster> monsterMap = new HashMap<>();
            monsterMap.put(monster.getId(), monster);
            ProjectContext.userToMonsterMap.put(user, monsterMap);
//          提醒用户你已进入战斗模式
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ENTERFIGHT));
        }
    }

    private Monster getMonster(User user, String monsterName) {
        for (Monster monster : ProjectContext.sceneMap.get(user.getPos()).getMonsters()) {
            if (monster.getName().equals(monsterName)) {
                return monster;
            }
        }
        return null;
    }

    @Order(orderMsg = "iu")
    public void upStartLevel(Channel channel, String msg) {
        upEquipmentStartlevel(channel, msg);
    }

    @Order(orderMsg = "npcGet")
    public void getEquipFromNpc(Channel channel, String msg) {
        String temp[] = msg.split("-");
        if (temp.length != 2) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        User user = ProjectContext.session2UserIds.get(channel);
        Scene scene = ProjectContext.sceneMap.get(user.getPos());
        NPC npc = null;
        for (NPC npcT : scene.getNpcs()) {
            if (npcT.getName().equals(temp[1])) {
                npc = npcT;
            }
        }
        if (npc == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOFOUNDNPC));
            return;
        }
        if (npc.getGetGoods().equals(GrobalConfig.NULL)) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOEXACHANGEFORNPC));
            return;
        }

//      扣去兑换品
        String[] target = npc.getGetTarget().split(":");
        Userbag userbagTarget = null;
        for (Userbag userbag : user.getUserBag()) {
            if (userbag.getWid().equals(Integer.parseInt(target[0]))) {
                if (userbag.getNum() < Integer.parseInt(target[1])) {
                    channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOENOUGHCHANGEGOOD));
                    return;
                }
                userbagTarget = userbag;
            }
        }
        userbagCaculation.removeUserbagFromUser(user, userbagTarget, Integer.parseInt(target[1]));

//        新增武器
        String[] getGoods = npc.getGetGoods().split("-");
        for(String getGood : getGoods){
            Equipment equipment = ProjectContext.equipmentMap.get(Integer.parseInt(getGood));
            Userbag userbag1 = new Userbag();
            userbag1.setWid(equipment.getId());
            userbag1.setNum(1);
            userbag1.setStartlevel(equipment.getStartLevel());
            userbag1.setId(UUID.randomUUID().toString());
            userbag1.setDurability(equipment.getDurability());
            userbag1.setName(user.getUsername());
            userbag1.setTypeof(PGood.EQUIPMENT);
            userbagCaculation.addUserBagForUser(user, userbag1);
        }
    }

    @Order(orderMsg = "viewSkill")
    public void skillCheckOut(Channel channel, String msg) {
        ProjectContext.eventStatus.put(channel, EventStatus.SKILLMANAGER);
        channel.writeAndFlush(MessageUtil.turnToPacket("请输入lookSkill查看技能，请输入change-技能名-键位配置技能,请输入quitSkill退出技能管理界面"));
    }

    @Order(orderMsg = "move")
    public void moveScene(Channel channel, String msg) {
        String temp[] = msg.split("-");
        if (temp.length != 2) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }

        User user = ProjectContext.session2UserIds.get(channel);
        if (temp[1].equals(ProjectContext.sceneMap.get(user.getPos()).getName())) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNMOVELOCAL));
            return;
        }

        Scene sceneTarget = ProjectContext.sceneMap.get(SceneUtil.getSceneByName(temp[1]).getId());
        if (LevelUtil.getLevelByExperience(user.getExperience()) < Integer.parseInt(sceneTarget.getNeedLevel())) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOLEVELTOMOVE));
            return;
        }

//      起始之地有特殊功效，能让人物的血量和蓝量回满
        if (sceneTarget.getName().equals("起始之地")) {
            UserUtil.recoverUser(user);
        }

//      场景的移动切换用户到不同的场景线程
        Scene scene = ProjectContext.sceneMap.get(user.getPos());
        if (!ProjectContext.sceneSet.contains(temp[1])) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOTARGETTOMOVE));
            return;
        }

        if (!ProjectContext.sceneMap.get(user.getPos()).getSceneSet().contains(temp[1])) {
//           场景切换
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.REMOTEMOVEMESSAGE));
            return;
        }

        scene.getUserMap().remove(user.getUsername());
        sceneTarget.getUserMap().put(user.getUsername(), user);
        user.setPos(sceneTarget.getId());
        userMapper.updateByPrimaryKeySelective(user);
        ProjectContext.session2UserIds.put(channel, user);
        channel.writeAndFlush(MessageUtil.turnToPacket("已移动到" + temp[1]));
    }

    //  装备升星，先不注入后期注入
    private void upEquipmentStartlevel(Channel channel, String msg) {
        String[] temp = msg.split("=");
        if (temp.length != 2) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        User user = ProjectContext.session2UserIds.get(channel);
        Userbag userbag = UserbagUtil.getUserbagByUserbagId(user, temp[1]);
        if (userbag == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.GOODNOEXISTBAG));
            return;
        }
//      开始升星
        if (userbag.getStartlevel() == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOTOUPSTARTLEVEL));
            return;
        }

        moneyCaculation.removeMoneyToUser(user, "100000");

        userbag.setStartlevel(userbag.getStartlevel() + 1);
        userbagMapper.updateByPrimaryKeySelective(userbag);
        channel.writeAndFlush(MessageUtil.turnToPacket("升星成功，升星花费100000金币,当前装备星级" + userbag.getStartlevel()));
        UserbagUtil.refreshUserbagInfo(channel);
    }

    private String out(User user) {
        String resp = "";
        for (Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()) {
            Equipment equipment = ProjectContext.equipmentMap.get(weaponequipmentbar.getWid());
            resp += System.getProperty("line.separator")
                    + equipment.getName() + "剩余耐久为:" + weaponequipmentbar.getDurability();
        }
        return resp;
    }

}
