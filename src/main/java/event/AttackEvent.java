package event;

import caculation.AttackCaculation;
import caculation.HpCaculation;
import component.Scene;
import component.BossScene;
import component.Equipment;
import config.BuffConfig;
import config.MessageConfig;
import config.DeadOrAliveConfig;
import context.ProjectUtil;
import factory.MonsterFactory;
import io.netty.channel.Channel;
import context.ProjectContext;
import order.Order;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userskillrelation;
import pojo.Weaponequipmentbar;
import skill.UserSkill;
import team.Team;
import utils.AttackUtil;
import utils.MessageUtil;
import component.Monster;
import utils.UserUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Component("attackEvent")
public class AttackEvent {
    @Autowired
    private CommonEvent commonEvent;
    @Autowired
    private AttackCaculation attackCaculation;
    @Autowired
    private OutfitEquipmentEvent outfitEquipmentEvent;
    @Autowired
    private ShopEvent shopEvent;
    @Autowired
    private ChatEvent chatEvent;
    @Autowired
    private BuffEvent buffEvent;
    @Autowired
    private MonsterFactory monsterFactory;
    @Autowired
    private HpCaculation hpCaculation;
    @Autowired
    private BossEvent bossEvent;

    @Order(orderMsg = "chat-,chatAll")
    public void chatEvent(Channel channel, String msg) throws InvocationTargetException, IllegalAccessException {
        ProjectUtil.reflectAnnotation(chatEvent, channel, msg);
    }

    @Order(orderMsg = "bg,qs")
    public void shopEvent(Channel channel, String msg) throws InvocationTargetException, IllegalAccessException {
        ProjectUtil.reflectAnnotation(shopEvent, channel, msg);
    }

    @Order(orderMsg = "qf")
    public void quitFight(Channel channel, String msg) throws InvocationTargetException, IllegalAccessException {
        User user = ProjectContext.session2UserIds.get(channel);
//      boss的战斗中推出
        if(user.getTeamId()!=null&&ProjectContext.bossAreaMap.containsKey(user.getTeamId())){
            ProjectUtil.reflectAnnotation(bossEvent,channel,msg);
        }else {
//          普通战斗中退出
            ProjectContext.userToMonsterMap.remove(ProjectContext.session2UserIds.get(channel));
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.RETREATFIGHT));
            ProjectContext.eventStatus.put(channel, EventStatus.STOPAREA);
            return;
        }
    }

    @Order(orderMsg = "qb,ub-,qw,fix,wq,ww")
    public void commonMethod(Channel channel, String msg) throws InvocationTargetException, IllegalAccessException {
        ProjectUtil.reflectAnnotation(commonEvent, channel, msg);
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
//                   让副本继续处于战斗状态
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
        UserUtil.recoverUser(user);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.RECOVRESUCCESS));
        channel.writeAndFlush(MessageUtil.turnToPacket(user.getUsername() + "的血量为：" + user.getHp() + "蓝量为：" + user.getMp()));
    }


    //  战斗效果
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
        if (user.getBuffMap().get(BuffConfig.SLEEPBUFF) != 5000) {
            if (userSkill.getBuffMap().containsKey(BuffConfig.RELIEVEBUFF)) {
                buffEvent.buffSolve(userskillrelation, userSkill, monster, user);
            } else {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.SLEEPMESSAGE));
                return;
            }
        }

//      技能buff处理
        if (buffEvent.buffSolve(userskillrelation, userSkill, monster, user) == 1) {
            return;
        }

//      攻击逻辑
        BigInteger attackDamage = new BigInteger(userSkill.getDamage());
        attackDamage = attackCaculation.caculate(user, attackDamage);
        hpCaculation.subMonsterHp(monster, attackDamage.toString());
//      蓝量计算
        userMp -= skillMp;
        user.setMp(userMp + "");

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

        if (monster.getValueOfLife().equals("0")) {
//          通知玩家技能伤害情况
            channel.writeAndFlush(MessageUtil.turnToPacket(resp));
            monster.setStatus(DeadOrAliveConfig.DEAD);

//          boss战斗场景
            if (monster.getType().equals(Monster.TYPEOFBOSS)) {
                BossScene bossScene = ProjectContext.bossAreaMap.get(user.getTeamId());
//              更改用户攻击的boss
                AttackUtil.changeUserAttackMonster(user, bossScene, monster);
                AttackUtil.killBossMessageToAll(user, monster);
//              额外奖励最后一击的玩家
                if (monster.getPos().equals("A3")) {
                    outfitEquipmentEvent.extraBonus(user, channel);
                }
            }

//           普通战斗场景
            if (monster.getType().equals(Monster.TYPEOFCOMMONMONSTER)) {
//              移除死掉的怪物
                ProjectContext.sceneMap.get(user.getPos()).getMonsters().remove(monster);
//              成新的怪物
                Scene scene = ProjectContext.sceneMap.get(user.getPos());
                scene.getMonsters().add(monsterFactory.getMonsterByArea(user.getPos()));
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
