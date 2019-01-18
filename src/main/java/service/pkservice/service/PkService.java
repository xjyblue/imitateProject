package service.pkservice.service;

import core.annotation.Order;
import core.annotation.Region;
import service.achievementservice.service.AchievementService;
import service.caculationservice.service.AttackDamageCaculationService;
import service.caculationservice.service.HpCaculationService;
import core.config.MessageConfig;
import core.config.GrobalConfig;
import core.channel.ChannelStatus;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userskillrelation;
import service.skillservice.entity.UserSkill;
import service.skillservice.service.SkillService;
import utils.ChannelUtil;
import utils.MessageUtil;
import service.userservice.service.UserService;

import java.math.BigInteger;

/**
 * @ClassName PkService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
@Region
public class PkService {
    @Autowired
    private AttackDamageCaculationService attackDamageCaculationService;
    @Autowired
    private AchievementService achievementService;
    @Autowired
    private HpCaculationService hpCaculationService;
    @Autowired
    private UserService userService;
    @Autowired
    private SkillService skillService;

    /**
     * 和他人PK
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "pk", status = {ChannelStatus.COMMONSCENE})
    public void pkOthers(Channel channel, String msg) {
        String[] temp = msg.split("=");
        User user = ChannelUtil.channelToUserMap.get(channel);
        if (temp.length != GrobalConfig.THREE) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        User userTarget = userService.getUserByNameFromSession(temp[1]);
        Channel channelTarget = ChannelUtil.userToChannelMap.get(userTarget);
//       解决夸场景pk禁止
        if (userTarget != null && user != null && !user.getPos().equals(userTarget.getPos())) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOSUPPORTREMOTEPK));
            return;
        }
//       解决起始之地不允许pk
        if (user != null && user.getPos().equals(GrobalConfig.STARTSCENE)) {
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
        UserSkill userSkill = skillService.getUserSkillByKey(channel, temp[2]);
        if (userSkill == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOKEYSKILL));
            return;
        }
        BigInteger attackDamage = attackDamageCaculationService.caculate(user, userSkill.getDamage());
//      人物蓝量校验
        BigInteger userSkillMp = new BigInteger(userSkill.getSkillMp());
        BigInteger userMp = new BigInteger(user.getMp());
        BigInteger minHp = new BigInteger(GrobalConfig.MINVALUE);
        if (userSkillMp.compareTo(userMp) > 0) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNENOUGHMP));
            return;
        }
//      人物cd校验
        Userskillrelation userskillrelation = user.getUserskillrelationMap().get(temp[2]);
        if (System.currentTimeMillis() < userskillrelation.getSkillcds() + userSkill.getAttackCd()) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNSKILLCD));
            return;
        }
        userskillrelation.setSkillcds(System.currentTimeMillis());
        hpCaculationService.subUserHp(userTarget, attackDamage.toString());
//      人物死亡处理
        if (new BigInteger(userTarget.getHp()).compareTo(minHp) <= 0) {
            userTarget.setHp(GrobalConfig.MINVALUE);
            userTarget.setStatus(GrobalConfig.DEAD);
            String resp = "你受到来自：" + user.getUsername() + "的" + userSkill.getSkillName() + "的攻击，伤害为["
                    + attackDamage.toString() + "]你的剩余血量为：" + userTarget.getHp() + ",你已死亡";
            channelTarget.writeAndFlush(MessageUtil.turnToPacket(resp));
            resp = "你对" + userTarget.getUsername() + "使用" + userSkill.getSkillName() + "进行攻击，造成伤害[" + attackDamage.toString() + "]" +
                    "，" + userTarget.getUsername() + "的剩余血量为：" + userTarget.getHp() + "，你已杀死" + userTarget.getUsername();
            channel.writeAndFlush(MessageUtil.turnToPacket(resp));
            channelTarget.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.SELECTLIVEWAY));
//          死亡后处理

//          pk触发pk胜利成就
            achievementService.executeFirstPKWin(user);

            ChannelUtil.channelStatus.put(channelTarget, ChannelStatus.DEADSCENE);
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
