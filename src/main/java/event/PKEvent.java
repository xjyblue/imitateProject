package event;

import achievement.AchievementExecutor;
import caculation.AttackCaculation;
import caculation.HpCaculation;
import config.MessageConfig;
import config.DeadOrAliveConfig;
import io.netty.channel.Channel;
import context.ProjectContext;
import order.Order;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userskillrelation;
import skill.UserSkill;
import utils.UserSkillUtil;
import utils.MessageUtil;
import utils.UserUtil;

import java.math.BigInteger;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/11/23 16:22
 */
@Component("pkEvent")
public class PKEvent {
    @Autowired
    private AttackCaculation attackCaculation;
    @Autowired
    private AchievementExecutor achievementExecutor;
    @Autowired
    private HpCaculation hpCaculation;
    @Order(orderMsg = "pk")
    public void pkOthers(Channel channel, String msg) {
        String temp[] = msg.split("-");
        User user = ProjectContext.session2UserIds.get(channel);
        if (temp.length != 3) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        User userTarget = UserUtil.getUserByName(temp[1]);
        Channel channelTarget = ProjectContext.userToChannelMap.get(userTarget);
//       解决夸场景pk禁止
        if(!user.getPos().equals(userTarget.getPos())){
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOSUPPORTREMOTEPK));
            return;
        }
//       解决起始之地不允许pk
        if(user.getPos().equals("0")){
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
        BigInteger attackDamage = attackCaculation.caculate(user, new BigInteger(userSkill.getDamage()));
//      人物蓝量校验
        BigInteger userSkillMp = new BigInteger(userSkill.getSkillMp());
        BigInteger userMp = new BigInteger(user.getMp());
        BigInteger minHp = new BigInteger("0");
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
        hpCaculation.reduceUserHp(userTarget,attackDamage.toString());
//      人物死亡处理
        if (new BigInteger(userTarget.getHp()).compareTo(minHp)<=0){
            userTarget.setHp("0");
            userTarget.setStatus(DeadOrAliveConfig.DEAD);
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

            ProjectContext.eventStatus.put(channelTarget,EventStatus.DEADAREA);
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
