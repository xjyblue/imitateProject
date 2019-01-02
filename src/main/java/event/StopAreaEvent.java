package event;

import service.attackservice.service.AttackService;
import service.achievementservice.entity.Achievement;
import service.achievementservice.service.AchievementExecutor;
import service.caculationservice.service.MoneyCaculationService;
import service.caculationservice.service.UserbagCaculationService;
import component.scene.Scene;
import component.good.Equipment;
import service.npcservice.entity.NPC;
import component.good.parent.PGood;
import config.MessageConfig;
import config.GrobalConfig;
import service.npcservice.service.NpcService;
import service.userbagservice.service.UserbagService;
import service.weaponservice.service.Weaponservice;
import utils.ReflectMethodUtil;
import io.netty.channel.Channel;
import service.bossservice.service.BossService;
import service.chatservice.service.ChatService;
import service.emailservice.service.EmailService;
import service.friendservice.service.FriendService;
import service.labourUnionservice.service.LabourUnionService;
import service.levelservice.service.LevelService;
import mapper.UserMapper;
import mapper.UserbagMapper;
import context.ProjectContext;
import order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.pkservice.service.PKEvent;
import pojo.*;
import service.shopservice.service.ShopService;
import service.teamservice.service.TeamService;
import service.transactionservice.service.TransactionService;
import service.userservice.service.UserService;
import utils.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Component
public class StopAreaEvent {
    @Autowired
    private UserMapper userMapper;
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
    private PKEvent pkEvent;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private LabourUnionService labourUnionService;
    @Autowired
    private AchievementExecutor achievementExecutor;
    @Autowired
    private FriendService friendService;
    @Autowired
    private UserbagMapper userbagMapper;
    @Autowired
    private UserbagCaculationService userbagCaculationService;
    @Autowired
    private MoneyCaculationService moneyCaculationService;
    @Autowired
    private LevelService levelService;
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

    @Order(orderMsg = "aoi")
    public void aoiMethod(Channel channel,String msg){
        userService.aoiMethod(channel,msg);
    }

    @Order(orderMsg = "iftrade,ytrade,ntrade")
    public void tradeMethod(Channel channel, String msg) throws InvocationTargetException, IllegalAccessException {
        ReflectMethodUtil.reflectAnnotation(transactionService, channel, msg);
    }

    @Order(orderMsg = "pkservice")
    public void pkEvent(Channel channel, String msg) throws InvocationTargetException, IllegalAccessException {
        ReflectMethodUtil.reflectAnnotation(pkEvent, channel, msg);
    }

    @Order(orderMsg = "qemail,send=emailservice,receive=emailservice")
    public void emailEvent(Channel channel, String msg) throws InvocationTargetException, IllegalAccessException {
        ReflectMethodUtil.reflectAnnotation(emailService, channel, msg);
    }

    @Order(orderMsg = "chat-,chatAll")
    public void chatEvent(Channel channel, String msg) throws InvocationTargetException, IllegalAccessException {
        ReflectMethodUtil.reflectAnnotation(chatService, channel, msg);
    }

    @Order(orderMsg = "bg,qs")
    public void shopEvent(Channel channel, String msg) throws InvocationTargetException, IllegalAccessException {
        ReflectMethodUtil.reflectAnnotation(shopService, channel, msg);
    }

    @Order(orderMsg = "pe")
    public void friendEvent(Channel channel, String msg) throws InvocationTargetException, IllegalAccessException {
        ReflectMethodUtil.reflectAnnotation(friendService, channel, msg);
    }

    @Order(orderMsg = "g")
    public void labourUnionEvent(Channel channel, String msg) throws InvocationTargetException, IllegalAccessException {
        ReflectMethodUtil.reflectAnnotation(labourUnionService, channel, msg);
    }

    @Order(orderMsg = "ef")
    public void enterBossArea(Channel channel, String msg) throws InvocationTargetException, IllegalAccessException {
        ReflectMethodUtil.reflectAnnotation(bossService, channel, msg);
    }

    @Order(orderMsg = "t")
    public void teamEvent(Channel channel, String msg) throws InvocationTargetException, IllegalAccessException {
        ReflectMethodUtil.reflectAnnotation(teamService, channel, msg);
    }

    @Order(orderMsg = "npctalk")
    public void talkMethod(Channel channel, String msg) {
        npcService.talkMethod(channel,msg);
    }

    @Order(orderMsg = "attack-")
    public void attackFirst(Channel channel, String msg) throws IOException {
        attackService.attackCommonFirst(channel,msg);
    }

    @Order(orderMsg = "iu")
    public void upStartLevel(Channel channel, String msg) {
        upEquipmentStartlevel(channel, msg);
    }

    @Order(orderMsg = "npcGet")
    public void getEquipFromNpc(Channel channel, String msg) {
        npcService.getEquipFromNpc(channel,msg);
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
        if (levelService.getLevelByExperience(user.getExperience()) < Integer.parseInt(sceneTarget.getNeedLevel())) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOLEVELTOMOVE));
            return;
        }

//      起始之地有特殊功效，能让人物的血量和蓝量回满
        if (sceneTarget.getName().equals("起始之地")) {
            userService.recoverUser(user);
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

        moneyCaculationService.removeMoneyToUser(user, "100000");

        userbag.setStartlevel(userbag.getStartlevel() + 1);
        userbagMapper.updateByPrimaryKeySelective(userbag);
        channel.writeAndFlush(MessageUtil.turnToPacket("升星成功，升星花费100000金币,当前装备星级" + userbag.getStartlevel()));
        UserbagUtil.refreshUserbagInfo(channel);
    }

}
