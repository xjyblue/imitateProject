package service.bossservice.service;

import service.attackservice.service.AttackService;
import service.sceneservice.entity.BossScene;
import core.component.monster.Monster;
import service.sceneservice.entity.Scene;
import core.config.MessageConfig;
import core.config.GrobalConfig;
import service.userbagservice.service.UserbagService;
import service.weaponservice.service.Weaponservice;
import core.ChannelStatus;
import service.shopservice.service.ShopService;
import io.netty.channel.Channel;
import core.context.ProjectContext;
import core.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import service.chatservice.service.ChatService;
import service.teamservice.entity.Team;
import service.levelservice.service.LevelService;
import utils.MessageUtil;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName BossService
 * @Description boss副本服务
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
public class BossService {
    @Autowired
    private ShopService shopService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private LevelService levelService;
    @Autowired
    private UserbagService userbagService;
    @Autowired
    private Weaponservice weaponservice;
    @Autowired
    private AttackService attackService;

    /**
     * 进入boss副本
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "ef")
    public void enterBossArea(Channel channel, String msg) {
        String[] temp = msg.split("-");
        if (temp.length != GrobalConfig.TWO) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        User user = ProjectContext.session2UserIds.get(channel);
//      10级以下无法进入副本
        if (levelService.getLevelByExperience(user.getExperience()) < GrobalConfig.MIN_ENTER_BOSSSCENE) {
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
            HashMap<String, User> teamUserMap = new HashMap<>(64);
            teamUserMap.put(user.getUsername(), user);
            team.setUserMap(teamUserMap);
            ProjectContext.teamMap.put(user.getTeamId(), team);
        } else {
//      进入副本队员死亡检查
            team = ProjectContext.teamMap.get(user.getTeamId());
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

    /**
     * 单人交流
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "chat-")
    public void chatOne(Channel channel, String msg) {
        chatService.chatOne(channel, msg);
    }

    /**
     * 集体交流
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "chatAll")
    public void chatAll(Channel channel, String msg) {
        chatService.chatAll(channel, msg);
    }

    /**
     * 购买物品
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "bg")
    public void buyShopGood(Channel channel, String msg) {
        shopService.buyShopGood(channel, msg);
    }

    /**
     * 展示商城
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "qs")
    public void queryShopGood(Channel channel, String msg) {
        shopService.queryShopGood(channel, msg);
    }

    /**
     * 展示装备栏
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "qw")
    public void showUserWeapon(Channel channel, String msg) {
        weaponservice.queryEquipmentBar(channel, msg);
    }

    /**
     * 修复武器
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "fix")
    public void fixWeapon(Channel channel, String msg) {
        weaponservice.fixEquipment(channel, msg);
    }

    /**
     * 卸下武器
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "wq")
    public void takeOffWeapon(Channel channel, String msg) {
        weaponservice.quitEquipment(channel, msg);
    }

    /**
     * 穿戴武器
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "ww")
    public void takeInWeapon(Channel channel, String msg) {
        weaponservice.takeEquipment(channel, msg);
    }

    /**
     * 展示背包
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "qb")
    public void showUserbag(Channel channel, String msg) {
        userbagService.refreshUserbagInfo(channel, msg);
    }

    /**
     * 使用道具
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "ub-")
    public void useUserbag(Channel channel, String msg) {
        userbagService.useUserbag(channel, msg);
    }

    /**
     * 退出副本
     * @param channel
     * @param msg
     */
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

    /**
     * 第一次攻击
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "attack")
    public void attack(Channel channel, String msg) {
        attackService.bossSceneFirstAttack(channel, msg);
    }

    /**
     * 检查是否有人活着
     *
     * @param team
     * @return
     */
    private boolean checkAllManAlive(Team team) {
        for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
            if (entry.getValue().getStatus().equals(GrobalConfig.DEAD)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 第一次进入副本改变队伍中所有用户的状态和提示信息
     *
     * @param team
     * @param bossScene
     */
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

}