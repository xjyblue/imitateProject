package service.attackservice.service;

import service.buffservice.service.RestraintBuffService;
import service.caculationservice.service.AttackDamageCaculationService;
import service.caculationservice.service.HpCaculationService;
import service.caculationservice.service.MpCaculationService;
import service.sceneservice.entity.Scene;
import service.sceneservice.entity.BossScene;
import core.component.good.Equipment;
import core.config.MessageConfig;
import core.config.GrobalConfig;
import service.skillservice.service.SkillService;
import service.userbagservice.service.UserbagService;
import service.weaponservice.service.Weaponservice;
import utils.ReflectMethodUtil;
import service.buffservice.service.AttackBuffService;
import core.ChannelStatus;
import core.factory.MonsterFactory;
import io.netty.channel.Channel;
import core.context.ProjectContext;
import core.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userskillrelation;
import pojo.Weaponequipmentbar;
import service.bossservice.service.BossService;
import service.chatservice.service.ChatService;
import service.rewardservice.service.RewardService;
import service.shopservice.service.ShopService;
import service.skillservice.entity.UserSkill;
import service.attackservice.util.AttackUtil;
import utils.MessageUtil;
import core.component.monster.Monster;
import service.userservice.service.UserService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName AttackService
 * @Description 战斗系统服务
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
public class AttackService {
    @Autowired
    private AttackDamageCaculationService attackDamageCaculationService;
    @Autowired
    private RewardService rewardService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private AttackBuffService attackBuffService;
    @Autowired
    private MonsterFactory monsterFactory;
    @Autowired
    private HpCaculationService hpCaculationService;
    @Autowired
    private BossService bossService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserbagService userbagService;
    @Autowired
    private Weaponservice weaponservice;
    @Autowired
    private MpCaculationService mpCaculationService;
    @Autowired
    private RestraintBuffService restraintBuffService;
    @Autowired
    private SkillService skillService;

    /**
     * 一个人说话
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "chat-")
    public void chatOne(Channel channel, String msg) {
        chatService.chatOne(channel, msg);
    }

    /**
     * 大喇叭全服
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "chatAll")
    public void chatAll(Channel channel, String msg) {
        chatService.chatAll(channel, msg);
    }

    /**
     * 展示商城
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "qs")
    public void queryShop(Channel channel, String msg) {
        shopService.queryShopGood(channel, msg);
    }

    /**
     * 展示背包
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "bg")
    public void buyShopGood(Channel channel, String msg) {
        shopService.buyShopGood(channel, msg);
    }

    /**
     * 退出战斗
     *
     * @param channel
     * @param msg
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @Order(orderMsg = "qf")
    public void quitFight(Channel channel, String msg) throws InvocationTargetException, IllegalAccessException {
        User user = ProjectContext.session2UserIds.get(channel);
        //      boss的战斗中推出
        if (user.getTeamId() != null && ProjectContext.bossAreaMap.containsKey(user.getTeamId())) {
            ReflectMethodUtil.reflectAnnotation(bossService, channel, msg);
        } else {
            //          普通战斗中退出
            ProjectContext.userToMonsterMap.remove(ProjectContext.session2UserIds.get(channel));
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.RETREATFIGHT));
            ProjectContext.eventStatus.put(channel, ChannelStatus.COMMONSCENE);
            return;
        }
    }

    /**
     * 展示装备栏
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "qw")
    public void showUserWeapon(Channel channel, String msg) {
        weaponservice.queryEquipmentBar(channel, msg);
    }

    /**
     * 修复武器
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "fix")
    public void fixWeapon(Channel channel, String msg) {
        weaponservice.fixEquipment(channel, msg);
    }

    /**
     * 卸下武器
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "wq")
    public void takeOffWeapon(Channel channel, String msg) {
        weaponservice.quitEquipment(channel, msg);
    }

    /**
     * 穿戴武器
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "ww")
    public void takeInWeapon(Channel channel, String msg) {
        weaponservice.takeEquipment(channel, msg);
    }

    /**
     * 展示背包
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "qb")
    public void showUserbag(Channel channel, String msg) {
        userbagService.refreshUserbagInfo(channel, msg);
    }

    /**
     * 使用背包道具
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "ub-")
    public void useUserbag(Channel channel, String msg) {
        userbagService.useUserbag(channel, msg);
    }

    /**
     * 随机键位攻击
     *
     * @param channel
     * @param msg
     * @throws IOException
     */
    @Order(orderMsg = "*")
    public void attack(Channel channel, String msg) throws IOException {
        attackKeySolve(channel, msg);
    }

    /**
     * 战斗中转移攻击目标
     *
     * @param channel
     * @param msg
     * @throws IOException
     */
    @Order(orderMsg = "attack")
    public void changeTarget(Channel channel, String msg) throws IOException {
        User user = ProjectContext.session2UserIds.get(channel);
        if (msg == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        String[] temp = msg.split("-");
        if (temp.length != GrobalConfig.THREE) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        //      在副本中打
        if (ProjectContext.bossAreaMap.containsKey(user.getTeamId())) {
            BossScene bossScene = ProjectContext.bossAreaMap.get(user.getTeamId());
            Map<String, Monster> monsterMap = bossScene.getMonsters().get(bossScene.getSequence().get(0));
            Monster monster = null;
            for (Map.Entry<String, Monster> entry : monsterMap.entrySet()) {
                if (temp[1].equals(entry.getValue().getName())) {
                    monster = entry.getValue();
                    //                  填充怪物攻击对象
                    AttackUtil.addMonsterToUserMonsterList(user, monster);
                    //                  让副本继续处于战斗状态
                    if (!bossScene.isFight()) {
                        bossScene.setFight(true);
                    }
                }
            }
            ProjectContext.userToMonsterMap.get(user).remove(monster.getId());
            monster = monsterMap.get(temp[1]);
            ProjectContext.userToMonsterMap.get(user).put(monster.getId(), monster);
            attackKeySolve(channel, temp[2]);
            return;
        }
    }

    /**
     * 战斗完成后恢复血量，仅限副本可用
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "recover")
    public void recoverUserHpAndMp(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        if (user.getTeamId() == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NORECOVERUSERHPANDMP));
            return;
        }
        if (!ProjectContext.bossAreaMap.containsKey(user.getTeamId())) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NORECOVERUSERHPANDMP));
            return;
        }
        BossScene bossScene = ProjectContext.bossAreaMap.get(user.getTeamId());
        if (bossScene.isFight()) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NORECOVERUSERHPANDMP));
            return;
        }
        userService.recoverUser(user);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.RECOVRESUCCESS));
        channel.writeAndFlush(MessageUtil.turnToPacket(user.getUsername() + "的血量为：" + user.getHp() + "蓝量为：" + user.getMp()));
    }


    /**
     * 持续攻击逻辑
     *
     * @param channel
     * @param msg
     * @throws IOException
     */
    private void attackKeySolve(Channel channel, String msg) throws IOException {
        User user = ProjectContext.session2UserIds.get(channel);
        //找到攻击的怪物
        Monster monster = getTargetMonster(user);
        if (monster == null) {
            return;
        }

        //检查技能是否存在
        if (!ProjectContext.userskillrelationMap.get(user).containsKey(msg)) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOKEYSKILL));
            return;
        }
        UserSkill userSkill = ProjectContext.skillMap.get(ProjectContext.userskillrelationMap.get(user).get(msg).getSkillid());
        Userskillrelation userskillrelation = ProjectContext.userskillrelationMap.get(user).get(msg);

        //战斗前置检查，技能buff、mp值、是否被控等
        if (preAttackCheck(channel, user, monster, userSkill, userskillrelation)) {
            return;
        }

        //红蓝计算，返回伤害
        BigInteger attackDamage = attackCaculation(user, monster, userSkill);

        //输出语句拼接
        String resp = out(user, userSkill, monster, attackDamage.toString());

        if (monster.getValueOfLife().equals(GrobalConfig.MINVALUE)) {
            //通知玩家技能伤害情况
            channel.writeAndFlush(MessageUtil.turnToPacket(resp));
            monster.setStatus(GrobalConfig.DEAD);

            //boss战斗场景
            if (monster.getType().equals(Monster.TYPEOFBOSS)) {
                BossScene bossScene = ProjectContext.bossAreaMap.get(user.getTeamId());
                //清除用户攻击的boss，更改所有用户的攻击状态，准备对下一个boss发起攻击
                AttackUtil.changeUserAttackMonster(user, bossScene, monster);
                AttackUtil.killBossMessageToAll(user, monster);
                //额外奖励最后一击的玩家
                if (monster.getPos().equals(bossScene.getFinalReward())) {
                    rewardService.extraBonus(user, channel);
                }
            }

            //普通战斗场景
            if (monster.getType().equals(Monster.TYPEOFCOMMONMONSTER)) {
                //移除死掉的怪物
                ProjectContext.sceneMap.get(user.getPos()).getMonsters().remove(monster);
                //新增怪物
                Scene scene = ProjectContext.sceneMap.get(user.getPos());
                List<Monster> monsters = monsterFactory.getMonsterByArea(user.getPos());
                for (Monster monsterT : monsters) {
                    scene.getMonsters().add(monsterT);
                }
            }
        } else {
            //通知玩家技能伤害情况
            channel.writeAndFlush(MessageUtil.turnToPacket(resp));
            //如果是在副本中战斗更新用户总战斗伤害的值，仇恨值
            if (user.getTeamId() != null && ProjectContext.bossAreaMap.get(user.getTeamId()) != null) {
                refreshUserDamageInBossScene(user, attackDamage);
            }
            channel.writeAndFlush(MessageUtil.turnToPacket(resp));
        }
    }

    /**
     * 战斗前置检查
     *
     * @param channel
     * @param user
     * @param monster
     * @param userSkill
     * @param userskillrelation
     * @return
     */
    private boolean preAttackCheck(Channel channel, User user, Monster monster, UserSkill userSkill, Userskillrelation userskillrelation) {
        //      人物蓝量检查
        if (!mpCaculationService.checkUserMpEnough(user, userSkill)) {
            return true;
        }
        //      技能CD检查
        if (!skillService.checkUserSkillCd(userskillrelation, channel)) {
            return true;
        }
        //      判断用户是否被技能控制，是否为解控技能解除控制
        if (!restraintBuffService.restraintBuff(userSkill, user, userskillrelation, monster)) {
            return true;
        }
        //      更新技能cd
        skillService.refreshUserSkillCd(userSkill, userskillrelation);
        //      技能buff处理
        if (attackBuffService.buffSolve(userskillrelation, userSkill, monster, user) == AttackBuffService.BUFF_ATTACK_FLAG) {
            return true;
        }
        return false;
    }

    /**
     * 拿到攻击的目标
     *
     * @param user
     */
    private Monster getTargetMonster(User user) {
        if (!ProjectContext.userToMonsterMap.containsKey(user)) {
            return null;
        }
        Monster monster = null;
        for (Map.Entry<Integer, Monster> entry : ProjectContext.userToMonsterMap.get(user).entrySet()) {
            monster = entry.getValue();
        }
        return monster;
    }

    /**
     * 刷新用户在boss副本中的伤害值
     *
     * @param user
     * @param attackDamage
     */
    private void refreshUserDamageInBossScene(User user, BigInteger attackDamage) {
        if (!ProjectContext.bossAreaMap.get(user.getTeamId()).getDamageAll().containsKey(user)) {
            ProjectContext.bossAreaMap.get(user.getTeamId()).getDamageAll().put(user, attackDamage.toString());
        } else {
            String newDamageValue = ProjectContext.bossAreaMap.get(user.getTeamId()).getDamageAll().get(user);
            BigInteger newDamageValueI = new BigInteger(newDamageValue).add(attackDamage);
            ProjectContext.bossAreaMap.get(user.getTeamId()).getDamageAll().put(user, newDamageValueI.toString());
        }
    }


    /**
     * 对普通场景的怪物进行第一次攻击
     *
     * @param channel
     * @param msg
     * @throws IOException
     */
    public void attackCommonSceneFirst(Channel channel, String msg) throws IOException {
        User user = ProjectContext.session2UserIds.get(channel);
        String[] temp = msg.split("-");
        //输入的怪物是否存在
        Monster monster = getMonster(user, temp[1], Monster.TYPEOFCOMMONMONSTER);
        if (monster == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOFOUNDMONSTER));
            return;
        }
        //输入的键位是否存在
        if (!(temp.length == GrobalConfig.THREE && ProjectContext.userskillrelationMap.get(user).containsKey(temp[GrobalConfig.TWO]))) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOKEYSKILL));
            return;
        }

        Userskillrelation userskillrelation = ProjectContext.userskillrelationMap.get(user).get(temp[GrobalConfig.TWO]);
        UserSkill userSkill = ProjectContext.skillMap.get(userskillrelation.getSkillid());

        //战斗前置检查
        if (preAttackCheck(channel, user, monster, userSkill, userskillrelation)) {
            return;
        }
        //红、蓝计算
        BigInteger attackDamage = attackCaculation(user, monster, userSkill);

        //输出语句拼接
        String resp = out(user, userSkill, monster, attackDamage.toString());

        if (monster.getValueOfLife().equals(GrobalConfig.MINVALUE)) {
            resp += System.getProperty("line.separator")
                    + "怪物已死亡";
            channel.writeAndFlush(MessageUtil.turnToPacket(resp));
            //修改怪物状态
            monster.setStatus(GrobalConfig.DEAD);
            //爆装备
            rewardService.getGoods(channel, monster);
            //移除死掉的怪物
            ProjectContext.sceneMap.get(user.getPos()).getMonsters().remove(monster);
            //生成新的怪物
            Scene scene = ProjectContext.sceneMap.get(user.getPos());
            List<Monster> monsters = monsterFactory.getMonsterByArea(user.getPos());
            for (Monster monsterT : monsters) {
                scene.getMonsters().add(monsterT);
            }
        } else {
            //切换到攻击模式
            ProjectContext.eventStatus.put(channel, ChannelStatus.ATTACK);
            channel.writeAndFlush(MessageUtil.turnToPacket(resp));
            //记录当前攻击的目标
            Map<Integer, Monster> monsterMap = new HashMap<>(64);
            monsterMap.put(monster.getId(), monster);
            ProjectContext.userToMonsterMap.put(user, monsterMap);
            //提醒用户你已进入战斗模式
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ENTERFIGHT));
        }
    }

    /**
     * 攻击蓝量 血量的计算，返回攻击的伤害值
     *
     * @param user
     * @param monster
     * @param userSkill
     * @return
     */
    private BigInteger attackCaculation(User user, Monster monster, UserSkill userSkill) {
        //      蓝量计算
        mpCaculationService.subUserMp(user, userSkill.getSkillMp());
        //      攻击伤害计算    怪物掉血，生命值计算逻辑
        BigInteger attackDamage = attackDamageCaculationService.caculate(user, userSkill.getDamage());
        hpCaculationService.subMonsterHp(monster, attackDamage.toString());
        return attackDamage;
    }

    /**
     * 获取正在攻击的目标
     *
     * @param user
     * @param monsterName
     * @return
     */
    private Monster getMonster(User user, String monsterName, String monsterType) {
        if (monsterType.equals(Monster.TYPEOFCOMMONMONSTER)) {
            for (Monster monster : ProjectContext.sceneMap.get(user.getPos()).getMonsters()) {
                if (monster.getName().equals(monsterName)) {
                    return monster;
                }
            }
            return null;
        } else if (monsterType.equals(Monster.TYPEOFBOSS)) {
            BossScene bossScene = ProjectContext.bossAreaMap.get(user.getTeamId());
            Map<String, Monster> monsterMap = bossScene.getMonsters().get(bossScene.getSequence().get(0));
            for (Map.Entry<String, Monster> entry : monsterMap.entrySet()) {
                if (entry.getValue().getName().equals(monsterName) && !entry.getValue().getStatus().equals(GrobalConfig.DEAD)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    /**
     * boss场景的第一次攻击
     *
     * @param channel
     * @param msg
     */
    public void bossSceneFirstAttack(Channel channel, String msg) {
        String[] temp = msg.split("-");
        User user = ProjectContext.session2UserIds.get(channel);
        //      锁定怪物  输入的怪物是否存在
        Monster monster = getMonster(user, temp[1], Monster.TYPEOFBOSS);
        if (monster == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOFOUNDMONSTER));
            return;
        }
        //      键位校验
        if (!(temp.length == GrobalConfig.THREE && ProjectContext.userskillrelationMap.get(user).containsKey(temp[GrobalConfig.TWO]))) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOKEYSKILL));
            return;
        }
        Userskillrelation userskillrelation = ProjectContext.userskillrelationMap.get(user).get(temp[2]);
        UserSkill userSkill = ProjectContext.skillMap.get(userskillrelation.getSkillid());

        //      战斗前置检查
        if (preAttackCheck(channel, user, monster, userSkill, userskillrelation)) {
            return;
        }

        //      蓝量计算
        BigInteger attackDamage = attackCaculation(user, monster, userSkill);

        AttackUtil.addMonsterToUserMonsterList(user, monster);

        //      记录伤害作为仇恨值
        refreshUserDamageInBossScene(user, attackDamage);
        //      输出语句拼接
        String resp = out(user, userSkill, monster, attackDamage.toString());

        BossScene bossScene = ProjectContext.bossAreaMap.get(user.getTeamId());
        bossScene.setFight(true);
        if (monster.getValueOfLife().equals(GrobalConfig.MINVALUE)) {
            resp += "怪物已死亡";
            //          修改怪物状态
            monster.setStatus(GrobalConfig.DEAD);
            //          清除用户攻击的boss
            AttackUtil.changeUserAttackMonster(user, bossScene, monster);
            //          直接击杀广播消息提示
            AttackUtil.killBossMessageToAll(user, monster);
            //          检查是否为副本单一boss
            if (!checkBossAreaAllBoss(bossScene)) {
                //              开始副本计时
                ProjectContext.endBossAreaTime.put(user.getTeamId(), System.currentTimeMillis() + bossScene.getKeepTime() * 1000);
            } else {
                //              直接胜利不用倒计时副本
            }
        } else {
            //          切换到攻击模式
            ProjectContext.eventStatus.put(channel, ChannelStatus.ATTACK);
            //          记录人物当前攻击的怪物
            AttackUtil.addMonsterToUserMonsterList(user, monster);
            ProjectContext.eventStatus.put(channel, ChannelStatus.ATTACK);
            ProjectContext.endBossAreaTime.put(user.getTeamId(), System.currentTimeMillis() + bossScene.getKeepTime() * 1000);
        }
        channel.writeAndFlush(MessageUtil.turnToPacket(resp));
    }

    /**
     * 检查场景下的所有boss是否死亡
     *
     * @param bossScene
     * @return
     */
    private boolean checkBossAreaAllBoss(BossScene bossScene) {
        if (bossScene.getSequence().size() > 1) {
            return false;
        }
        for (Map.Entry<String, Monster> entry : bossScene.getMonsters().get(bossScene.getSequence().get(0)).entrySet()) {
            if (entry.getValue().getStatus().equals(GrobalConfig.ALIVE)) {
                return false;
            }
        }
        return true;
    }


    /**
     * 造成伤害和武器耐久度输出
     *
     * @param user
     * @return
     */
    private String out(User user, UserSkill userSkill, Monster monster, String attackDamage) {
        String resp = "";
        for (Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()) {
            Equipment equipment = ProjectContext.equipmentMap.get(weaponequipmentbar.getWid());
            resp += System.getProperty("line.separator")
                    + equipment.getName() + "剩余耐久为:" + weaponequipmentbar.getDurability();
        }
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
                + "[人物剩余蓝量]:" + user.getMp();
        return resp;
    }
}