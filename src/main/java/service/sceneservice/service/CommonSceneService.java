package service.sceneservice.service;

import service.attackservice.service.AttackService;
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
import service.labourUnionservice.service.LabourUnionService;
import order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.pkservice.service.PKService;
import service.shopservice.service.ShopService;
import service.teamservice.service.TeamService;
import service.transactionservice.service.TransactionService;
import service.userservice.service.UserService;

import java.io.IOException;

/**
 * 进入游戏世界的第一次。。。
 */

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
    private PKService pkService;
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

    @Order(orderMsg = "aoi")
    public void aoiMethod(Channel channel, String msg) {
        userService.aoiMethod(channel, msg);
    }

    @Order(orderMsg = "iftrade")
    public void applyTrade(Channel channel, String msg) {
        transactionService.createTrade(channel, msg);
    }

    @Order(orderMsg = "ytrade")
    public void agreeTrade(Channel channel, String msg) {
        transactionService.agreeTrade(channel, msg);
    }

    @Order(orderMsg = "ntrade")
    public void diagreeTrade(Channel channel, String msg) {
        transactionService.cancelTrade(channel, msg);
    }

    @Order(orderMsg = "pk-")
    public void pkEvent(Channel channel, String msg) {
        pkService.pkOthers(channel, msg);
    }

    @Order(orderMsg = "qemail")
    public void queryEmail(Channel channel, String msg) {
        emailService.queryEmail(channel, msg);
    }

    @Order(orderMsg = "send=email")
    public void sendEmail(Channel channel, String msg) {
        emailService.sendEmail(channel, msg);
    }

    @Order(orderMsg = "receive=email")
    public void receiveEmail(Channel channel, String msg) {
        emailService.receiveEmail(channel, msg);
    }

    @Order(orderMsg = "chat-")
    public void chatEvent(Channel channel, String msg) {
        chatService.chatOne(channel, msg);
    }

    @Order(orderMsg = "chatAll")
    public void chatAll(Channel channel, String msg) {
        chatService.chatAll(channel, msg);
    }

    @Order(orderMsg = "bg")
    public void buyShopGood(Channel channel, String msg) {
        shopService.buyShopGood(channel, msg);
    }

    @Order(orderMsg = "qs")
    public void showShopGood(Channel channel, String msg) {
        shopService.queryShopGood(channel, msg);
    }

    @Order(orderMsg = "pe")
    public void enterFriendView(Channel channel, String msg) {
        friendService.enterFriendView(channel, msg);
    }

    @Order(orderMsg = "g")
    public void enterLabourUnion(Channel channel, String msg) {
        labourUnionService.enterUnionView(channel, msg);
    }

    @Order(orderMsg = "ef")
    public void enterBossArea(Channel channel, String msg) {
        bossService.enterBossArea(channel, msg);
    }

    @Order(orderMsg = "te")
    public void teamEvent(Channel channel, String msg){
        teamService.enterTeamView(channel,msg);
    }

    @Order(orderMsg = "npctalk")
    public void talkMethod(Channel channel, String msg) {
        npcService.talkMethod(channel, msg);
    }

    @Order(orderMsg = "attack-")
    public void attackFirst(Channel channel, String msg) throws IOException {
        attackService.attackCommonFirst(channel, msg);
    }

    @Order(orderMsg = "iu")
    public void upStartLevel(Channel channel, String msg) {
        weaponStartService.upEquipmentStartlevel(channel, msg);
    }

    @Order(orderMsg = "npcGet")
    public void getEquipFromNpc(Channel channel, String msg) {
        npcService.getEquipFromNpc(channel, msg);
    }

    @Order(orderMsg = "viewSkill")
    public void skillCheckOut(Channel channel, String msg) {
        skillService.enterSkillView(channel, msg);
    }

    @Order(orderMsg = "move")
    public void moveScene(Channel channel, String msg) {
        sceneService.moveScene(channel, msg);
    }


}
