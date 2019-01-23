package service.attackservice.service;

import config.impl.excel.BossSceneConfigResourceLoad;
import config.impl.excel.EquipmentResourceLoad;
import config.impl.excel.SceneResourceLoad;
import config.impl.excel.UserSkillResourceLoad;
import core.annotation.Region;
import core.packet.ServerPacket;
import service.buffservice.entity.BuffConstant;
import service.buffservice.service.RestraintBuffService;
import service.caculationservice.service.AttackDamageCaculationService;
import service.caculationservice.service.HpCaculationService;
import service.caculationservice.service.MpCaculationService;
import service.levelservice.entity.Level;
import service.levelservice.service.LevelService;
import service.sceneservice.entity.Scene;
import service.sceneservice.entity.BossScene;
import core.component.good.Equipment;
import core.config.MessageConfig;
import core.config.GrobalConfig;
import service.skillservice.service.SkillService;
import service.buffservice.service.AttackBuffService;
import core.channel.ChannelStatus;
import core.factory.MonsterFactory;
import io.netty.channel.Channel;
import core.annotation.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userskillrelation;
import pojo.Weaponequipmentbar;
import service.rewardservice.service.RewardService;
import service.skillservice.entity.UserSkill;
import service.attackservice.util.AttackUtil;
import utils.ChannelUtil;
import utils.MessageUtil;
import core.component.monster.Monster;
import service.userservice.service.UserService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
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
@Region
public class AttackService {
    @Autowired
    private AttackDamageCaculationService attackDamageCaculationService;
    @Autowired
    private RewardService rewardService;
    @Autowired
    private AttackBuffService attackBuffService;
    @Autowired
    private MonsterFactory monsterFactory;
    @Autowired
    private HpCaculationService hpCaculationService;
    @Autowired
    private UserService userService;
    @Autowired
    private MpCaculationService mpCaculationService;
    @Autowired
    private RestraintBuffService restraintBuffService;
    @Autowired
    private SkillService skillService;
    @Autowired
    private LevelService levelService;


    /**
     * 普通场景战斗退出战斗
     *
     * @param channel
     * @param msg
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @Order(orderMsg = "cqf", status = {ChannelStatus.ATTACK})
    public void quitFight(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
//      副本中返回
        if (user.getTeamId() != null && BossSceneConfigResourceLoad.bossAreaMap.containsKey(user.getTeamId())) {
            return;
        }
        //普通战斗中退出
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(MessageConfig.RETREATFIGHT);
        MessageUtil.sendMessage(channel, builder.build());

        ChannelUtil.channelStatus.put(channel, ChannelStatus.COMMONSCENE);
        //移除玩家所有对战怪物
        AttackUtil.removeAllMonster(user);
        //初始化玩家buff
        userService.initUserBuff(user);
    }

    /**
     * boss场景退出副本和退出战斗，退出战斗即是退出场景
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "bqf", status = {ChannelStatus.ATTACK, ChannelStatus.BOSSSCENE})
    public void backBossArea(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
//      无副本返回
        if (user.getTeamId() == null || !BossSceneConfigResourceLoad.bossAreaMap.containsKey(user.getTeamId())) {
            return;
        }
//      boss场景
        BossScene bossScene = BossSceneConfigResourceLoad.bossAreaMap.get(user.getTeamId());
//      移除副本中的map
        bossScene.getDamageAll().remove(user);
        bossScene.getUserMap().remove(user.getUsername());
//      场景还原
        Scene sceneTarget = SceneResourceLoad.sceneMap.get(user.getPos());
        sceneTarget.getUserMap().put(user.getUsername(), user);
//      上下文回收
        BossSceneConfigResourceLoad.bossAreaMap.remove(user.getTeamId());
//      提示
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(MessageConfig.OUTBOSSAREA);
        MessageUtil.sendMessage(channel, builder.build());
//      渠道状态更新
        ChannelUtil.channelStatus.put(channel, ChannelStatus.COMMONSCENE);
//      初始化人物buff
        userService.initUserBuff(user);
    }

    /**
     * boss场景战斗第二到N次发起攻击，不同boss层
     *
     * @param channel
     * @param msg
     * @throws IOException
     */
    @Order(orderMsg = "attack", status = {ChannelStatus.ATTACK})
    public void changeTarget(Channel channel, String msg) throws IOException {
        User user = ChannelUtil.channelToUserMap.get(channel);
        if (!BossSceneConfigResourceLoad.bossAreaMap.containsKey(user.getTeamId())) {
            return;
        }
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.THREE) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERRORORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        //检查技能是否存在
        if (!user.getUserskillrelationMap().containsKey(temp[2])) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NOKEYSKILL);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        //      在副本中转移战斗目标
        if (BossSceneConfigResourceLoad.bossAreaMap.containsKey(user.getTeamId())) {
            BossScene bossScene = BossSceneConfigResourceLoad.bossAreaMap.get(user.getTeamId());
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
            user.getUserToMonsterMap().remove(monster.getId());
            monster = monsterMap.get(temp[1]);
            user.getUserToMonsterMap().put(monster.getId(), monster);
            attackKeySolve(channel, temp[2]);
            return;
        }
    }

    /**
     * boss场景战斗完成后恢复血量，仅限副本可用
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "recover", status = {ChannelStatus.ATTACK})
    public void recoverUserHpAndMp(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        if (user.getTeamId() == null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NORECOVERUSERHPANDMP);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        if (!BossSceneConfigResourceLoad.bossAreaMap.containsKey(user.getTeamId())) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NORECOVERUSERHPANDMP);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        BossScene bossScene = BossSceneConfigResourceLoad.bossAreaMap.get(user.getTeamId());
        if (bossScene.isFight()) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NORECOVERUSERHPANDMP);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        userService.recoverUser(user);
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(MessageConfig.RECOVRESUCCESS + "===" + user.getUsername() + "的血量为：" + levelService.getMaxHp(user) + "蓝量为：" + levelService.getMaxMp(user));
        MessageUtil.sendMessage(channel, builder.build());
    }


    /**
     * 1-9随机键位攻击逻辑
     *
     * @param channel
     * @param msg
     * @throws IOException
     */
    @Order(ifRandomkey = true, status = {ChannelStatus.ATTACK})
    public void attackKeySolve(Channel channel, String msg) throws IOException {
        User user = ChannelUtil.channelToUserMap.get(channel);
        //找到攻击的怪物
        Monster monster = getTargetMonsterFromMap(user);
        if (monster == null) {
            return;
        }

        //检查技能是否存在
        if (!user.getUserskillrelationMap().containsKey(msg)) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NOKEYSKILL);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        UserSkill userSkill = UserSkillResourceLoad.skillMap.get(user.getUserskillrelationMap().get(msg).getSkillid());
        Userskillrelation userskillrelation = user.getUserskillrelationMap().get(msg);

        //战斗前置检查，技能buff、mp值、是否被控等
        if (preAttackCheck(channel, user, monster, userSkill, userskillrelation)) {
            return;
        }

        //红蓝计算，返回伤害
        BigInteger attackDamage = attackCaculation(user, monster, userSkill);

        //输出语句拼接
        String resp = out(user, userSkill, monster, attackDamage.toString());

        if (monster.getValueOfLife().equals(GrobalConfig.MINVALUE)) {
//          通知玩家技能伤害情况
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(resp);
            MessageUtil.sendMessage(channel, builder.build());
            monster.setStatus(GrobalConfig.DEAD);

//          boss战斗场景
            if (monster.getType().equals(Monster.TYPEOFBOSS)) {
                BossScene bossScene = BossSceneConfigResourceLoad.bossAreaMap.get(user.getTeamId());
//              初始化玩家buff
                userService.initUserBuff(user);
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
                SceneResourceLoad.sceneMap.get(user.getPos()).getMonsters().remove(monster);
                //新增怪物
                Scene scene = SceneResourceLoad.sceneMap.get(user.getPos());
                List<Monster> monsters = monsterFactory.getMonsterByArea(user.getPos());
                for (Monster monsterT : monsters) {
                    scene.getMonsters().add(monsterT);
                }
            }
        } else {
            //通知玩家技能伤害情况
            //如果是在副本中战斗更新用户总战斗伤害的值，仇恨值
            if (user.getTeamId() != null && BossSceneConfigResourceLoad.bossAreaMap.get(user.getTeamId()) != null) {
                refreshUserDamageInBossScene(user, attackDamage);
            }
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(resp);
            MessageUtil.sendMessage(channel, builder.build());
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
     * 刷新用户在boss副本中的总伤害值
     *
     * @param user
     * @param attackDamage
     */
    private void refreshUserDamageInBossScene(User user, BigInteger attackDamage) {
        if (!BossSceneConfigResourceLoad.bossAreaMap.get(user.getTeamId()).getDamageAll().containsKey(user)) {
            BossSceneConfigResourceLoad.bossAreaMap.get(user.getTeamId()).getDamageAll().put(user, attackDamage.toString());
        } else {
            String newDamageValue = BossSceneConfigResourceLoad.bossAreaMap.get(user.getTeamId()).getDamageAll().get(user);
            BigInteger newDamageValueI = new BigInteger(newDamageValue).add(attackDamage);
            BossSceneConfigResourceLoad.bossAreaMap.get(user.getTeamId()).getDamageAll().put(user, newDamageValueI.toString());
        }
    }


    /**
     * 对普通场景的怪物进行第一次攻击
     *
     * @param channel
     * @param msg
     * @throws IOException
     */
    @Order(orderMsg = "cattack", status = {ChannelStatus.COMMONSCENE})
    public void attackCommonSceneFirst(Channel channel, String msg) throws IOException {
        User user = ChannelUtil.channelToUserMap.get(channel);
        String[] temp = msg.split("=");
        //输入的怪物是否存在
        Monster monster = getMonsterFirst(user, temp[1], Monster.TYPEOFCOMMONMONSTER);
        if (monster == null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NOFOUNDMONSTER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        //输入的键位是否存在
        if (!(temp.length == GrobalConfig.THREE && user.getUserskillrelationMap().containsKey(temp[GrobalConfig.TWO]))) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NOKEYSKILL);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }

        Userskillrelation userskillrelation = user.getUserskillrelationMap().get(temp[GrobalConfig.TWO]);
        UserSkill userSkill = UserSkillResourceLoad.skillMap.get(userskillrelation.getSkillid());

//      战斗前置检查
        if (preAttackCheck(channel, user, monster, userSkill, userskillrelation)) {
            return;
        }
//      激活第一次攻击需要的特殊buff
        firstAttackBuffStart(user);

        //红、蓝计算
        BigInteger attackDamage = attackCaculation(user, monster, userSkill);

        //输出语句拼接
        String resp = out(user, userSkill, monster, attackDamage.toString());

        if (monster.getValueOfLife().equals(GrobalConfig.MINVALUE)) {
//          初始化人物buff
            userService.initUserBuff(user);
            resp += System.getProperty("line.separator")
                    + "怪物已死亡";
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(resp);
            MessageUtil.sendMessage(channel, builder.build());
            //修改怪物状态
            monster.setStatus(GrobalConfig.DEAD);
            //爆装备
            rewardService.getGoods(channel, monster);
            //移除死掉的怪物
            SceneResourceLoad.sceneMap.get(user.getPos()).getMonsters().remove(monster);
            //生成新的怪物
            Scene scene = SceneResourceLoad.sceneMap.get(user.getPos());
            List<Monster> monsters = monsterFactory.getMonsterByArea(user.getPos());
            for (Monster monsterT : monsters) {
                scene.getMonsters().add(monsterT);
            }
        } else {
            //切换到攻击模式
            ChannelUtil.channelStatus.put(channel, ChannelStatus.ATTACK);
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(resp);
            MessageUtil.sendMessage(channel, builder.build());
            //记录当前攻击的目标
            user.getUserToMonsterMap().put(monster.getId(), monster);
            //提醒用户你已进入战斗模式
            ServerPacket.NormalResp.Builder builder2 = ServerPacket.NormalResp.newBuilder();
            builder2.setData(MessageConfig.ENTERFIGHT);
            MessageUtil.sendMessage(channel, builder2.build());
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
     * 第一次获取攻击目标加入map
     *
     * @param user
     * @param monsterName
     * @return
     */
    private Monster getMonsterFirst(User user, String monsterName, String monsterType) {
        if (monsterType.equals(Monster.TYPEOFCOMMONMONSTER)) {
            for (Monster monster : SceneResourceLoad.sceneMap.get(user.getPos()).getMonsters()) {
                if (monster.getName().equals(monsterName)) {
                    return monster;
                }
            }
            return null;
        } else if (monsterType.equals(Monster.TYPEOFBOSS)) {
            BossScene bossScene = BossSceneConfigResourceLoad.bossAreaMap.get(user.getTeamId());
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
     * 从map拿到正在攻击的目标
     *
     * @param user
     */
    private Monster getTargetMonsterFromMap(User user) {
        if (user.getUserToMonsterMap().size() == 0) {
            return null;
        }
        Monster monster = null;
        for (Map.Entry<Integer, Monster> entry : user.getUserToMonsterMap().entrySet()) {
            monster = entry.getValue();
        }
        return monster;
    }

    /**
     * boss场景的第一次攻击
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "battack", status = {ChannelStatus.BOSSSCENE, ChannelStatus.ATTACK})
    public void bossSceneFirstAttack(Channel channel, String msg) {
        String[] temp = msg.split("=");
        User user = ChannelUtil.channelToUserMap.get(channel);
        //      锁定怪物  输入的怪物是否存在
        Monster monster = getMonsterFirst(user, temp[1], Monster.TYPEOFBOSS);
        if (monster == null) {
            //提醒用户你已进入战斗模式
            ServerPacket.NormalResp.Builder builder2 = ServerPacket.NormalResp.newBuilder();
            builder2.setData(MessageConfig.NOFOUNDMONSTER);
            MessageUtil.sendMessage(channel, builder2.build());
            return;
        }
        //      键位校验
        if (!(temp.length == GrobalConfig.THREE && user.getUserskillrelationMap().containsKey(temp[GrobalConfig.TWO]))) {
            ServerPacket.NormalResp.Builder builder2 = ServerPacket.NormalResp.newBuilder();
            builder2.setData(MessageConfig.NOKEYSKILL);
            MessageUtil.sendMessage(channel, builder2.build());
            return;
        }
        Userskillrelation userskillrelation = user.getUserskillrelationMap().get(temp[2]);
        UserSkill userSkill = UserSkillResourceLoad.skillMap.get(userskillrelation.getSkillid());

        String resp = "";
        //      战斗前置检查
        if (preAttackCheck(channel, user, monster, userSkill, userskillrelation)) {
//          是否特殊技能直接打死
            resp = checkMonsterDead(channel, user, monster, resp);
            ServerPacket.NormalResp.Builder builder2 = ServerPacket.NormalResp.newBuilder();
            builder2.setData(resp);
            MessageUtil.sendMessage(channel, builder2.build());
            return;
        }
//      激活第一次攻击需要的特殊buff
        firstAttackBuffStart(user);
//      蓝量计算
        BigInteger attackDamage = attackCaculation(user, monster, userSkill);

        AttackUtil.addMonsterToUserMonsterList(user, monster);

        //      记录伤害作为仇恨值
        refreshUserDamageInBossScene(user, attackDamage);
        //      输出语句拼接
        resp = out(user, userSkill, monster, attackDamage.toString());
        //      怪物死亡校验,这里是为了解决有人一下子就把怪物打死的逻辑
        resp = checkMonsterDead(channel, user, monster, resp);
        ServerPacket.NormalResp.Builder builder2 = ServerPacket.NormalResp.newBuilder();
        builder2.setData(resp);
        MessageUtil.sendMessage(channel, builder2.build());
    }

    /**
     * 校验是否直接打死怪物
     *
     * @param channel
     * @param user
     * @param monster
     * @param resp
     * @return
     */
    private String checkMonsterDead(Channel channel, User user, Monster monster, String resp) {
        BossScene bossScene = BossSceneConfigResourceLoad.bossAreaMap.get(user.getTeamId());
        bossScene.setFight(true);
        if (monster.getValueOfLife().equals(GrobalConfig.MINVALUE)) {
            userService.initUserBuff(user);
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
                bossScene.setBossSceneEndTime(System.currentTimeMillis() + bossScene.getKeepTime() * 1000);
            } else {
                //              直接胜利不用倒计时副本
            }
        } else {
            //切换到攻击模式
            ChannelUtil.channelStatus.put(channel, ChannelStatus.ATTACK);
            //记录人物当前攻击的怪物
            AttackUtil.addMonsterToUserMonsterList(user, monster);
            bossScene.setBossSceneEndTime(System.currentTimeMillis() + bossScene.getKeepTime() * 1000);
        }
        return resp;
    }

    /**
     * 激活第一次战斗需要的buff
     *
     * @param user
     */
    private void firstAttackBuffStart(User user) {
        if (user.getRoleid() == GrobalConfig.FOUR) {
            user.getBuffMap().put(BuffConstant.BABYBUF, 7001);
        }
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
            Equipment equipment = EquipmentResourceLoad.equipmentMap.get(weaponequipmentbar.getWid());
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
