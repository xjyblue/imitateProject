package service.pkservice.service;

import service.achievementservice.service.AchievementExecutor;
import service.caculationservice.service.AttackCaculationService;
import service.caculationservice.service.HpCaculationService;
import config.MessageConfig;
import config.GrobalConfig;
import event.EventStatus;
import io.netty.channel.Channel;
import context.ProjectContext;
import order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userskillrelation;
import service.skillservice.entity.UserSkill;
import service.skillservice.util.UserSkillUtil;
import utils.MessageUtil;
import service.userservice.service.UserService;

import java.math.BigInteger;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/11/23 16:22
 */
@Component("pkEvent")
public class PKEvent {
    @Autowired
    private AttackCaculationService attackCaculationService;
    @Autowired
    private AchievementExecutor achievementExecutor;
    @Autowired
    private HpCaculationService hpCaculationService;
    @Autowired
    private UserService userService;
    @Order(orderMsg = "pkservice")
    public void pkOthers(Channel channel, String msg) {
        String temp[] = msg.split("-");
        User user = ProjectContext.session2UserIds.get(channel);
        if (temp.length != 3) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        User userTarget = userService.getUserByName(temp[1]);
        Channel channelTarget = ProjectContext.userToChannelMap.get(userTarget);
//       解决夸场景pk禁止
        if(!user.getPos().equals(userTarget.getPos())){
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOSUPPORTREMOTEPK));
            return;
        }
//       解决起始之地不允许pk
        if(user.getPos().equals(GrobalConfig.STARTSCENE)){
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.RESURRECTIONNOPK));
            return;
        }
        if (userTarget == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOFOUNDPKPERSON));
            return;
        }
        if (user.getUsername().equals(temp[1])) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOPKSELF));
            return;
        }
        UserSkill userSkill = UserSkillUtil.getUserSkillByKey(channel, temp[2]);
        if(userSkill == null){
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOKEYSKILL));
            return;
        }
        BigInteger attackDamage = attackCaculationService.caculate(user, new BigInteger(userSkill.getDamage()));
//      人物蓝量校验
        BigInteger userSkillMp = new BigInteger(userSkill.getSkillMp());
        BigInteger userMp = new BigInteger(user.getMp());
        BigInteger minHp = new BigInteger(GrobalConfig.MINVALUE);
        if (userSkillMp.compareTo(userMp) > 0) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNENOUGHMP));
            return;
        }
//      人物cd校验
        Userskillrelation userskillrelation = ProjectContext.userskillrelationMap.get(user).get(temp[2]);
        if (System.currentTimeMillis() < userskillrelation.getSkillcds() + userSkill.getAttackCd()) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNSKILLCD));
            return;
        }
        userskillrelation.setSkillcds(System.currentTimeMillis());
        hpCaculationService.subUserHp(userTarget,attackDamage.toString());
//      人物死亡处理
        if (new BigInteger(userTarget.getHp()).compareTo(minHp)<=0){
            userTarget.setHp(GrobalConfig.MINVALUE);
            userTarget.setStatus(GrobalConfig.DEAD);
            String resp = "你受到来自：" + user.getUsername() + "的" + userSkill.getSkillName() + "的攻击，伤害为["
                    + attackDamage.toString() + "]你的剩余血量为：" + userTarget.getHp() + ",你已死亡";
            channelTarget.writeAndFlush(MessageUtil.turnToPacket(resp));
            resp = "你对" + userTarget.getUsername() + "使用" + userSkill.getSkillName() + "进行攻击，造成伤害[" + attackDamage.toString() + "]" +
                    "，" + userTarget.getUsername() + "的剩余血量为：" + userTarget.getHp()+"，你已杀死"+userTarget.getUsername();
            channel.writeAndFlush(MessageUtil.turnToPacket(resp));
            channelTarget.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.SELECTLIVEWAY));
//          死亡后处理

//          pk触发pk胜利成就
            achievementExecutor.executeFirstPKWin(user);

            ProjectContext.eventStatus.put(channelTarget, EventStatus.DEADAREA);
            return;
        }
        String resp = "你受到来自：" + user.getUsername() + "的" + userSkill.getSkillName() + "的攻击，伤害为["
                + attackDamage.toString() + "]你的剩余血量为：" + userTarget.getHp();
        channelTarget.writeAndFlush(MessageUtil.turnToPacket(resp));
        resp = "你对" + userTarget.getUsername() + "使用" + userSkill.getSkillName() + "进行攻击，造成伤害[" + attackDamage.toString() + "]" +
                "，" + userTarget.getUsername() + "的剩余血量为：" + userTarget.getHp();
        channel.writeAndFlush(MessageUtil.turnToPacket(resp));
    }

}
