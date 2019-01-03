package service.attackservice.service;

import service.caculationservice.service.AttackCaculationService;
import service.caculationservice.service.HpCaculationService;
import service.sceneservice.entity.Scene;
import service.sceneservice.entity.BossScene;
import core.component.good.Equipment;
import service.buffservice.entity.BuffConstant;
import core.config.MessageConfig;
import core.config.GrobalConfig;
import service.userbagservice.service.UserbagService;
import service.weaponservice.service.Weaponservice;
import utils.ReflectMethodUtil;
import service.buffservice.service.BuffService;
import core.ChannelStatus;
import core.factory.MonsterFactory;
import io.netty.channel.Channel;
import core.context.ProjectContext;
import order.Order;
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
 * 持续攻击的服务
 */
@Component
public class AttackService {
    @Autowired
    private AttackCaculationService attackCaculationService;
    @Autowired
    private RewardService rewardService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private BuffService buffService;
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

    @Order(orderMsg = "chat-")
    public void chatOne(Channel channel, String msg) {
        chatService.chatOne(channel, msg);
    }

    @Order(orderMsg = "chatAll")
    public void chatAll(Channel channel, String msg) {
        chatService.chatAll(channel, msg);
    }

    @Order(orderMsg = "qs")
    public void queryShop(Channel channel, String msg) {
        shopService.queryShopGood(channel, msg);
    }

    @Order(orderMsg = "bg")
    public void buyShopGood(Channel channel, String msg) {
        shopService.buyShopGood(channel, msg);
    }

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

    @Order(orderMsg = "qw")
    public void showUserWeapon(Channel channel, String msg) {
        weaponservice.queryEquipmentBar(channel, msg);
    }

    @Order(orderMsg = "fix")
    public void fixWeapon(Channel channel, String msg) {
        weaponservice.fixEquipment(channel, msg);
    }

    @Order(orderMsg = "wq")
    public void takeOffWeapon(Channel channel, String msg) {
        weaponservice.quitEquipment(channel, msg);
    }

    @Order(orderMsg = "ww")
    public void takeInWeapon(Channel channel, String msg) {
        weaponservice.takeEquipment(channel, msg);
    }

    @Order(orderMsg = "qb")
    public void showUserbag(Channel channel, String msg) {
        userbagService.refreshUserbagInfo(channel, msg);
    }

    @Order(orderMsg = "ub-")
    public void useUserbag(Channel channel, String msg) {
        userbagService.useUserbag(channel, msg);
    }

    @Order(orderMsg = "*")
    public void attack(Channel channel, String msg) throws IOException {
        attackKeySolve(channel, msg);
    }

    //  此处是再副本的战斗状态中 转移攻击目标
    @Order(orderMsg = "attack")
    public void changeTarget(Channel channel, String msg) throws IOException {
        User user = ProjectContext.session2UserIds.get(channel);
        if (msg == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        String temp[] = msg.split("-");
        if (temp.length != 3) {
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
                    if (ProjectContext.userToMonsterMap.containsKey(user)) {
                        ProjectContext.userToMonsterMap.get(user).put(monster.getId(), monster);
                    } else {
                        Map<Integer, Monster> map = new HashMap<>();
                        map.put(monster.getId(), monster);
                        ProjectContext.userToMonsterMap.put(user, map);
                    }
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

//  战斗完成后恢复血量，仅限副本可用
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


    //  持续攻击逻辑
    private void attackKeySolve(Channel channel, String msg) throws IOException {
        User user = ProjectContext.session2UserIds.get(channel);
        if (!ProjectContext.userskillrelationMap.get(user).containsKey(msg)) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOKEYSKILL));
            return;
        }


        UserSkill userSkill = ProjectContext.skillMap.get(ProjectContext.userskillrelationMap.get(user).get(msg).getSkillid());
        Userskillrelation userskillrelation = ProjectContext.userskillrelationMap.get(user).get(msg);
//      找到刚刚锁定攻击的怪物
        Monster monster = null;
        if (!ProjectContext.userToMonsterMap.containsKey(user)) {
            return;
        }
        for (Map.Entry<Integer, Monster> entry : ProjectContext.userToMonsterMap.get(user).entrySet()) {
            monster = entry.getValue();
        }

//      人物蓝量检查
        Integer userMp = Integer.parseInt(user.getMp());
        Integer skillMp = Integer.parseInt(userSkill.getSkillMp());
        if (userMp < skillMp) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNENOUGHMP));
            return;
        }

//      技能CD检查
        if (System.currentTimeMillis() < userskillrelation.getSkillcds()) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNSKILLCD));
            return;
        }
//      更新技能cd
        userskillrelation.setSkillcds(System.currentTimeMillis() + userSkill.getAttackCd());

//      判断用户是否被眩晕,解除控制
        if (user.getBuffMap().get(BuffConstant.SLEEPBUFF) != 5000) {
            if (userSkill.getBuffMap().containsKey(BuffConstant.RELIEVEBUFF)) {
                buffService.buffSolve(userskillrelation, userSkill, monster, user);
            } else {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.SLEEPMESSAGE));
                return;
            }
        }

//      技能buff处理
        if (buffService.buffSolve(userskillrelation, userSkill, monster, user) == 1) {
            return;
        }

//      攻击逻辑
        BigInteger attackDamage = new BigInteger(userSkill.getDamage());
        attackDamage = attackCaculationService.caculate(user, attackDamage);
        hpCaculationService.subMonsterHp(monster, attackDamage.toString());
//      蓝量计算
        userMp -= skillMp;
        user.setMp(userMp.toString());

        String resp = out(user);
        resp += System.getProperty("line.separator")
                + "[技能]:" + userSkill.getSkillName()
                + System.getProperty("line.separator")
                + "对[" + monster.getName()
                + "]造成了" + attackDamage + "点伤害"
                + System.getProperty("line.separator")
                + "[怪物剩余血量]:" + monster.getValueOfLife()
                + System.getProperty("line.separator")
                + "[消耗蓝量]:" + userSkill.getSkillMp()
                + System.getProperty("line.separator")
                + "[人物剩余蓝量]:" + user.getMp();

        if (monster.getValueOfLife().equals(GrobalConfig.MINVALUE)) {
//          通知玩家技能伤害情况
            channel.writeAndFlush(MessageUtil.turnToPacket(resp));
            monster.setStatus(GrobalConfig.DEAD);

//          boss战斗场景
            if (monster.getType().equals(Monster.TYPEOFBOSS)) {
                BossScene bossScene = ProjectContext.bossAreaMap.get(user.getTeamId());
//              更改用户攻击的boss
                AttackUtil.changeUserAttackMonster(user, bossScene, monster);
                AttackUtil.killBossMessageToAll(user, monster);
//              额外奖励最后一击的玩家
                if (monster.getPos().equals(bossScene.getFinalReward())) {
                    rewardService.extraBonus(user, channel);
                }
            }

//           普通战斗场景
            if (monster.getType().equals(Monster.TYPEOFCOMMONMONSTER)) {
//              移除死掉的怪物
                ProjectContext.sceneMap.get(user.getPos()).getMonsters().remove(monster);
//              新增怪物
                Scene scene = ProjectContext.sceneMap.get(user.getPos());
                List<Monster> monsters = monsterFactory.getMonsterByArea(user.getPos());
                for (Monster monsterT : monsters) {
                    scene.getMonsters().add(monsterT);
                }
            }
        } else {
//          通知玩家技能伤害情况
            channel.writeAndFlush(MessageUtil.turnToPacket(resp));
//          如果是在副本中战斗更新用户总战斗伤害的值，仇恨值
            if (user.getTeamId() != null && ProjectContext.bossAreaMap.get(user.getTeamId()) != null) {
                if (!ProjectContext.bossAreaMap.get(user.getTeamId()).getDamageAll().containsKey(user)) {
                    ProjectContext.bossAreaMap.get(user.getTeamId()).getDamageAll().put(user, attackDamage.toString());
                } else {
                    String newDamageValue = ProjectContext.bossAreaMap.get(user.getTeamId()).getDamageAll().get(user);
                    BigInteger newDamageValueI = new BigInteger(newDamageValue).add(attackDamage);
                    ProjectContext.bossAreaMap.get(user.getTeamId()).getDamageAll().put(user, newDamageValueI.toString());
                }
            }
            channel.writeAndFlush(MessageUtil.turnToPacket(resp));
        }
    }


    //  对普通场景的怪物进行第一次攻击
    public void attackCommonFirst(Channel channel, String msg) throws IOException {
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
        buffService.buffSolve(userskillrelation, userSkill, monster, user);

//      攻击伤害计算
        BigInteger attackDamage = new BigInteger(userSkill.getDamage());
        attackDamage = attackCaculationService.caculate(user, attackDamage);

//      怪物掉血，生命值计算逻辑
        hpCaculationService.subMonsterHp(monster, attackDamage.toString());
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
            rewardService.getGoods(channel, monster);
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
            ProjectContext.eventStatus.put(channel, ChannelStatus.ATTACK);
            channel.writeAndFlush(MessageUtil.turnToPacket(resp));
//          记录当前攻击的目标
            Map<Integer, Monster> monsterMap = new HashMap<>();
            monsterMap.put(monster.getId(), monster);
            ProjectContext.userToMonsterMap.put(user, monsterMap);
//          提醒用户你已进入战斗模式
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ENTERFIGHT));
        }
    }

    //  获取普通场景的怪物
    private Monster getMonster(User user, String monsterName) {
        for (Monster monster : ProjectContext.sceneMap.get(user.getPos()).getMonsters()) {
            if (monster.getName().equals(monsterName)) {
                return monster;
            }
        }
        return null;
    }

//  武器耐久度输出
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
