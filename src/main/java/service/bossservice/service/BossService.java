package service.bossservice.service;

import service.caculationservice.service.AttackCaculationService;
import service.caculationservice.service.HpCaculationService;
import service.sceneservice.entity.BossScene;
import core.component.good.Equipment;
import core.component.monster.Monster;
import service.sceneservice.entity.Scene;
import core.config.MessageConfig;
import core.config.GrobalConfig;
import service.userbagservice.service.UserbagService;
import service.weaponservice.service.Weaponservice;
import service.buffservice.service.BuffService;
import core.ChannelStatus;
import service.shopservice.service.ShopService;
import io.netty.channel.Channel;
import core.context.ProjectContext;
import order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userskillrelation;
import pojo.Weaponequipmentbar;
import service.chatservice.service.ChatService;
import service.skillservice.entity.UserSkill;
import service.teamservice.entity.Team;
import service.attackservice.util.AttackUtil;
import service.levelservice.service.LevelService;
import utils.MessageUtil;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/11/19 16:04
 */
@Component
public class BossService {
    @Autowired
    private AttackCaculationService attackCaculationService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private BuffService buffService;
    @Autowired
    private HpCaculationService hpCaculationService;
    @Autowired
    private LevelService levelService;
    @Autowired
    private UserbagService userbagService;
    @Autowired
    private Weaponservice weaponservice;
    @Order(orderMsg = "ef")
    public void enterBossArea(Channel channel, String msg) {
        String temp[] = msg.split("-");
        if (temp.length != 2) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        User user = getUser(channel);
//      10级以下无法进入副本
        if (levelService.getLevelByExperience(user.getExperience()) < 10) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOLEVELTOMOVE));
            return;
        }
        Team team = null;

//      处理用户死亡后重连副本逻辑
        if (user.getTeamId() != null && ProjectContext.bossAreaMap.containsKey(user.getTeamId())) {
            ProjectContext.eventStatus.put(channel, ChannelStatus.BOSSSCENE);
//          进入副本，用户场景线程转移
            BossScene bossScene = ProjectContext.bossAreaMap.get(user.getTeamId());
            bossScene.getUserMap().put(user.getUsername(), user);

            Scene scene = ProjectContext.sceneMap.get(user.getPos());
            scene.getUserMap().remove(user.getUsername());
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.REBRORNANDCONNECTBOSSAREA));
            return;
        }

//      处理用户一个人加入副本的逻辑
        if (user.getTeamId() == null) {
            team = new Team();
            team.setTeamId(UUID.randomUUID().toString());
            team.setLeader(user);
            user.setTeamId(team.getTeamId());
            HashMap<String, User> teamUserMap = new HashMap<>();
            teamUserMap.put(user.getUsername(), user);
            team.setUserMap(teamUserMap);
            ProjectContext.teamMap.put(user.getTeamId(), team);
        } else {
//      进入副本队员死亡检查
            team = getTeam(user);
            if (checkAllManAlive(team)) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.SOMEBODYDEAD));
                return;
            }
        }
//      进入副本队长检查
        if (!team.getLeader().getUsername().equals(user.getUsername())) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.YOUARENOLEADER));
            return;
        }
//      生成新副本
        BossScene bossScene = new BossScene(user.getTeamId(), temp[1]);
        ProjectContext.bossAreaMap.put(team.getTeamId(), bossScene);
//      开启副本场景帧频线程
        Future future = ProjectContext.bossAreaThreadPool.scheduleAtFixedRate(bossScene, 0, 30, TimeUnit.MILLISECONDS);
        ProjectContext.futureMap.put(bossScene.getTeamId(), future);
        bossScene.setFutureMap(ProjectContext.futureMap);

//      改变用户渠道状态
        changeChannelStatus(team, bossScene);
    }

    @Order(orderMsg = "chat-")
    public void chatOne(Channel channel, String msg){
       chatService.chatOne(channel,msg);
    }

    @Order(orderMsg = "chatAll")
    public void chatAll(Channel channel,String msg){
        chatService.chatAll(channel,msg);
    }

    @Order(orderMsg = "bg")
    public void buyShopGood(Channel channel, String msg) {
        shopService.buyShopGood(channel,msg);
    }

    @Order(orderMsg = "qs")
    public void queryShopGood(Channel channel,String msg){
        shopService.queryShopGood(channel,msg);
    }

    @Order(orderMsg = "qw")
    public void showUserWeapon(Channel channel,String msg){
        weaponservice.queryEquipmentBar(channel,msg);
    }

    @Order(orderMsg = "fix")
    public void fixWeapon(Channel channel,String msg){
        weaponservice.fixEquipment(channel,msg);
    }

    @Order(orderMsg = "wq")
    public void takeOffWeapon(Channel channel,String msg){
        weaponservice.quitEquipment(channel,msg);
    }

    @Order(orderMsg = "ww")
    public void takeInWeapon(Channel channel,String msg){
        weaponservice.takeEquipment(channel,msg);
    }

    @Order(orderMsg = "qb")
    public void showUserbag(Channel channel,String msg){
        userbagService.refreshUserbagInfo(channel,msg);
    }

    @Order(orderMsg = "ub-")
    public void useUserbag(Channel channel,String msg){
        userbagService.useUserbag(channel,msg);
    }

    @Order(orderMsg = "qf")
    public void backBossArea(Channel channel, String msg) {

//      退出副本，回收资源
        User user = ProjectContext.session2UserIds.get(channel);
//      线程终止
        BossScene bossScene = ProjectContext.bossAreaMap.get(user.getTeamId());
        Future future = bossScene.getFutureMap().remove(user.getTeamId());
        future.cancel(true);
//      场景还原
        bossScene.getUserMap().remove(user.getUsername());
        Scene sceneTarget = ProjectContext.sceneMap.get(user.getPos());
        sceneTarget.getUserMap().put(user.getUsername(), user);
//      上下文回收
        ProjectContext.bossAreaMap.remove(user.getTeamId());
//      提示
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.OUTBOSSAREA));
//      渠道状态更新
        ProjectContext.eventStatus.put(channel, ChannelStatus.COMMONSCENE);
    }

    @Order(orderMsg = "attack")
    public void attack(Channel channel, String msg) {
        String temp[] = msg.split("-");
        User user = ProjectContext.session2UserIds.get(channel);
//      键位校验
        if (!(temp.length == 3 && ProjectContext.userskillrelationMap.get(user).containsKey(temp[2]))) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOKEYSKILL));
            return;
        }
//      锁定怪物  输入的怪物是否存在
        Monster monster = null;
        for (Map.Entry<String, Monster> entry : getMonsterMap(user).entrySet()) {
            if (entry.getValue().getName().equals(temp[1]) && !entry.getValue().getStatus().equals(GrobalConfig.DEAD)) {
                monster = entry.getValue();
            }
        }
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

//      技能冷却校验
        if (System.currentTimeMillis() < userskillrelation.getSkillcds()) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNSKILLCD));
            return;
        }
//      刷新技能时间
        userskillrelation.setSkillcds(System.currentTimeMillis() + userSkill.getAttackCd());

//      蓝量计算
        userMp -= skillMp;
        user.setMp(userMp + "");

//      技能buff处理
        buffService.buffSolve(userskillrelation, userSkill, monster, user);

        AttackUtil.addMonsterToUserMonsterList(user, monster);

//      判断攻击完怪物是否死亡，生命值计算逻辑
        BigInteger attackDamage = new BigInteger(userSkill.getDamage());
//      攻击逻辑计算
        attackDamage = attackCaculationService.caculate(user, attackDamage);
//      怪物掉血，生命值计算逻辑
        hpCaculationService.subMonsterHp(monster, attackDamage.toString());

//      记录伤害作为仇恨值
        if (!ProjectContext.bossAreaMap.get(user.getTeamId()).getDamageAll().containsKey(user)) {
            ProjectContext.bossAreaMap.get(user.getTeamId()).getDamageAll().put(user, attackDamage.toString());
        } else {
            String newDamageValue = ProjectContext.bossAreaMap.get(user.getTeamId()).getDamageAll().get(user);
            BigInteger newDamageValueI = new BigInteger(newDamageValue).add(attackDamage);
            ProjectContext.bossAreaMap.get(user.getTeamId()).getDamageAll().put(user, newDamageValueI.toString());
        }

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

        BossScene bossScene = ProjectContext.bossAreaMap.get(user.getTeamId());
        bossScene.setFight(true);
        if (monster.getValueOfLife().equals(GrobalConfig.MINVALUE)) {
            resp += "怪物已死亡";
//          修改怪物状态
            monster.setStatus(GrobalConfig.DEAD);
//          更改用户攻击的boss
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

    private boolean checkAllManAlive(Team team) {
        for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
            if (entry.getValue().getStatus().equals(GrobalConfig.DEAD)) {
                return true;
            }
        }
        return false;
    }

    private void changeChannelStatus(Team team, BossScene bossScene) {
        for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
//          移除之前场景线程的用户
            User user = entry.getValue();
            Scene sceneOld = ProjectContext.sceneMap.get(user.getPos());
            sceneOld.getUserMap().remove(user.getUsername());
//          新场景添加用户
            bossScene.getUserMap().put(user.getUsername(), user);

//          更新渠道的状态
            Channel channel = ProjectContext.userToChannelMap.get(entry.getValue());
            ProjectContext.eventStatus.put(channel, ChannelStatus.BOSSSCENE);

            String resp = "进入" + bossScene.getBossName() + "副本,出现boss有：";
            for (Map.Entry<String, Monster> entryMonster : bossScene.getMonsters().get(bossScene.getSequence().get(0)).entrySet()) {
                resp += entryMonster.getValue().getName() + " ";
            }
            channel.writeAndFlush(MessageUtil.turnToPacket(resp));
        }
    }

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

    private String out(User user) {
        String resp = "";
        for (Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()) {
            Equipment equipment = ProjectContext.equipmentMap.get(weaponequipmentbar.getWid());
            resp += System.getProperty("line.separator")
                    + equipment.getName() + "剩余耐久为:" + weaponequipmentbar.getDurability()
            ;
        }
        return resp;
    }

    private Map<String, Monster> getMonsterMap(User user) {
        BossScene bossScene = ProjectContext.bossAreaMap.get(user.getTeamId());
        return bossScene.getMonsters().get(bossScene.getSequence().get(0));
    }

    private Team getTeam(User user) {
        if (!ProjectContext.teamMap.containsKey(user.getTeamId())) return null;
        return ProjectContext.teamMap.get(user.getTeamId());
    }

    private User getUser(Channel channel) {
        return ProjectContext.session2UserIds.get(channel);
    }

}
