package service.pkservice.service;

import core.annotation.Order;
import core.annotation.Region;
import core.packet.ServerPacket;
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
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERRORORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        User userTarget = userService.getUserByNameFromSession(temp[1]);
        Channel channelTarget = ChannelUtil.userToChannelMap.get(userTarget);
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
//       解决鞭尸问题
        if(userTarget.getStatus().equals(GrobalConfig.DEAD)){
            builder.setData(MessageConfig.NOPKDEADMAN);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
//       解决夸场景pk禁止
        if (userTarget != null && user != null && !user.getPos().equals(userTarget.getPos())) {
            builder.setData(MessageConfig.NOSUPPORTREMOTEPK);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
//       解决起始之地不允许pk
        if (user != null && user.getPos().equals(GrobalConfig.STARTSCENE)) {
            builder.setData(MessageConfig.RESURRECTIONNOPK);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        if (userTarget == null) {
            builder.setData(MessageConfig.NOFOUNDPKPERSON);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        if (user.getUsername().equals(temp[1])) {
            builder.setData(MessageConfig.NOPKSELF);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        UserSkill userSkill = skillService.getUserSkillByKey(channel, temp[2]);
        if (userSkill == null) {
            builder.setData(MessageConfig.NOKEYSKILL);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        Integer attackDamage = attackDamageCaculationService.caculate(user, userSkill.getDamage());
//      人物蓝量校验
        Integer userSkillMp = Integer.parseInt(userSkill.getSkillMp());
        Integer userMp = Integer.parseInt(user.getMp());
        if (userSkillMp > userMp) {
            builder.setData(MessageConfig.UNENOUGHMP);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
//      人物cd校验
        Userskillrelation userskillrelation = user.getUserskillrelationMap().get(temp[2]);
        if (System.currentTimeMillis() < userskillrelation.getSkillcds() + userSkill.getAttackCd()) {
            builder.setData(MessageConfig.UNSKILLCD);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        userskillrelation.setSkillcds(System.currentTimeMillis());
        hpCaculationService.subUserHp(userTarget, attackDamage.toString());
//      人物死亡处理
        if (Integer.parseInt(userTarget.getHp()) <= GrobalConfig.ZERO) {
            userTarget.setHp(GrobalConfig.MINVALUE);
            userTarget.setStatus(GrobalConfig.DEAD);
            String resp = "你受到来自：" + user.getUsername() + "的" + userSkill.getSkillName() + "的攻击，伤害为["
                    + attackDamage.toString() + "]你的剩余血量为：" + userTarget.getHp() + ",你已死亡";

            builder.setData(resp);
            MessageUtil.sendMessage(channelTarget, builder.build());

            resp = "你对" + userTarget.getUsername() + "使用" + userSkill.getSkillName() + "进行攻击，造成伤害[" + attackDamage.toString() + "]" +
                    "，" + userTarget.getUsername() + "的剩余血量为：" + userTarget.getHp() + "，你已杀死" + userTarget.getUsername();

            builder.setData(resp);
            MessageUtil.sendMessage(channel, builder.build());

            builder.setData(MessageConfig.SELECTLIVEWAY);
            MessageUtil.sendMessage(channelTarget, builder.build());
//          死亡后处理

//          pk触发pk胜利成就
            achievementService.executeFirstPKWin(user);

            ChannelUtil.channelStatus.put(channelTarget, ChannelStatus.DEADSCENE);
            return;
        }

        String resp = "你受到来自：" + user.getUsername() + "的" + userSkill.getSkillName() + "的攻击，伤害为["
                + attackDamage.toString() + "]你的剩余血量为：" + userTarget.getHp();
        builder.setData(resp);
        MessageUtil.sendMessage(channelTarget, builder.build());


        resp = "你对" + userTarget.getUsername() + "使用" + userSkill.getSkillName() + "进行攻击，造成伤害[" + attackDamage.toString() + "]" +
                "，" + userTarget.getUsername() + "的剩余血量为：" + userTarget.getHp();
        builder.setData(resp);
        MessageUtil.sendMessage(channel, builder.build());
    }

}
