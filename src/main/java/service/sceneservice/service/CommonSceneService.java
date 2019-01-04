package service.sceneservice.service;

import service.attackservice.service.AttackService;
import service.labourunionservice.service.LabourUnionService;
import service.npcservice.service.NpcService;
import service.skillservice.service.SkillService;
import service.userbagservice.service.UserbagService;
import service.weaponservice.service.Weaponservice;
import service.weaponstartservice.service.WeaponStartService;
import io.netty.channel.Channel;
import service.bossservice.service.BossService;
import service.chatservice.service.ChatService;
import service.emailservice.service.EmailService;
import service.friendservice.service.FriendService;
import core.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.pkservice.service.PkService;
import service.shopservice.service.ShopService;
import service.teamservice.service.TeamService;
import service.transactionservice.service.TransactionService;
import service.userservice.service.UserService;

import java.io.IOException;

/**
 * @ClassName CommonSceneService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
public class CommonSceneService {
    @Autowired
    private TeamService teamService;
    @Autowired
    private BossService bossService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PkService pkService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private LabourUnionService labourUnionService;
    @Autowired
    private FriendService friendService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserbagService userbagService;
    @Autowired
    private Weaponservice weaponservice;
    @Autowired
    private AttackService attackService;
    @Autowired
    private NpcService npcService;
    @Autowired
    private SceneService sceneService;
    @Autowired
    private WeaponStartService weaponStartService;
    @Autowired
    private SkillService skillService;

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
     * 修复装备
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "fix")
    public void fixWeapon(Channel channel, String msg) {
        weaponservice.fixEquipment(channel, msg);
    }

    /**
     * 卸下装备
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "wq")
    public void takeOffWeapon(Channel channel, String msg) {
        weaponservice.quitEquipment(channel, msg);
    }

    /**
     * 穿戴装备
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
     * 使用道具
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "ub-")
    public void useUserbag(Channel channel, String msg) {
        userbagService.useUserbag(channel, msg);
    }

    /**
     * aoi
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "aoi")
    public void aoiMethod(Channel channel, String msg) {
        userService.aoiMethod(channel, msg);
    }

    /**
     * 发起交易请求
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "iftrade")
    public void applyTrade(Channel channel, String msg) {
        transactionService.createTrade(channel, msg);
    }

    /**
     * 同意交易请求
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "ytrade")
    public void agreeTrade(Channel channel, String msg) {
        transactionService.agreeTrade(channel, msg);
    }

    /**
     * 不同意交易请求
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "ntrade")
    public void diagreeTrade(Channel channel, String msg) {
        transactionService.cancelTrade(channel, msg);
    }

    /**
     * pk
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "pk-")
    public void pkEvent(Channel channel, String msg) {
        pkService.pkOthers(channel, msg);
    }

    /**
     * 展示所有邮件信息
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "qemail")
    public void queryEmail(Channel channel, String msg) {
        emailService.queryEmail(channel, msg);
    }

    /**
     * 发送邮件
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "send=email")
    public void sendEmail(Channel channel, String msg) {
        emailService.sendEmail(channel, msg);
    }

    /**
     * 接收邮件
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "receive=email")
    public void receiveEmail(Channel channel, String msg) {
        emailService.receiveEmail(channel, msg);
    }

    /**
     * 单人聊天
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "chat-")
    public void chatEvent(Channel channel, String msg) {
        chatService.chatOne(channel, msg);
    }

    /**
     * 多人聊天
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "chatAll")
    public void chatAll(Channel channel, String msg) {
        chatService.chatAll(channel, msg);
    }

    /**
     * 购买物品
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "bg")
    public void buyShopGood(Channel channel, String msg) {
        shopService.buyShopGood(channel, msg);
    }

    /**
     * 展示商城
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "qs")
    public void showShopGood(Channel channel, String msg) {
        shopService.queryShopGood(channel, msg);
    }

    /**
     * 进入好友模块
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "pe")
    public void enterFriendView(Channel channel, String msg) {
        friendService.enterFriendView(channel, msg);
    }

    /**
     * 进入工会管理
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "g")
    public void enterLabourUnion(Channel channel, String msg) {
        labourUnionService.enterUnionView(channel, msg);
    }

    /**
     * 进入副本
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "ef")
    public void enterBossArea(Channel channel, String msg) {
        bossService.enterBossArea(channel, msg);
    }

    /**
     * 进入队伍管理模块
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "te")
    public void teamEvent(Channel channel, String msg) {
        teamService.enterTeamView(channel, msg);
    }

    /**
     * 和npc交流
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "npctalk")
    public void talkMethod(Channel channel, String msg) {
        npcService.talkMethod(channel, msg);
    }

    /**
     * 普通场景第一次怪物攻击
     *
     * @param channel
     * @param msg
     * @throws IOException
     */
    @Order(orderMsg = "attack-")
    public void attackFirst(Channel channel, String msg) throws IOException {
        attackService.attackCommonSceneFirst(channel, msg);
    }

    /**
     * 装备升星
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "iu")
    public void upStartLevel(Channel channel, String msg) {
        weaponStartService.upEquipmentStartlevel(channel, msg);
    }

    /**
     * npc换装备
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "npcGet")
    public void getEquipFromNpc(Channel channel, String msg) {
        npcService.getEquipFromNpc(channel, msg);
    }

    /**
     * 进入技能管理界面
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "viewSkill")
    public void skillCheckOut(Channel channel, String msg) {
        skillService.enterSkillView(channel, msg);
    }

    /**
     * 移动场景
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "move")
    public void moveScene(Channel channel, String msg) {
        sceneService.moveScene(channel, msg);
    }


}
