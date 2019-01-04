package service.emailservice.service;

import core.component.good.parent.BaseGood;
import core.config.GrobalConfig;
import core.config.MessageConfig;
import org.springframework.beans.factory.annotation.Autowired;
import service.emailservice.entity.Mail;
import io.netty.channel.Channel;
import core.context.ProjectContext;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userbag;
import service.userbagservice.service.UserbagService;
import utils.MessageUtil;

import java.util.Map;
import java.util.UUID;

/**
 * @ClassName EmailService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
public class EmailService {
    @Autowired
    private UserbagService userbagService;

    /**
     * 展示所有邮件
     * @param channel
     * @param msg
     */
    public void queryEmail(Channel channel, String msg) {
//      展示用户的email信息
        User user = ProjectContext.session2UserIds.get(channel);
        Map<String, Mail> emailMap = ProjectContext.userEmailMap.get(user.getUsername());
        if (emailMap.size() == 0) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.EMPTYEMAIL));
            return;
        }
        for (Map.Entry<String, Mail> entry : emailMap.entrySet()) {
            Mail mailTemp = entry.getValue();
            if (mailTemp.isIfUserBag()) {
                channel.writeAndFlush(MessageUtil.turnToPacket("您有一封来自" + mailTemp.getFromUser() + "的邮件,邮件编号为" + mailTemp.getEmailId()
                        + ",邮件附件为" + BaseGood.getGoodNameByUserbag(mailTemp.getUserbag())
                        + ",邮件内容为[" + mailTemp.getEmailText() + "]"));
            } else {
                channel.writeAndFlush(MessageUtil.turnToPacket("您有一封来自" + mailTemp.getFromUser() + "的邮件,邮件编号为" + mailTemp.getEmailId() + ",邮件内容为[" + mailTemp.getEmailText() + "]"));
            }
        }
    }

    /**
     * 发送邮件
     * @param channel
     * @param msg
     */
    public void sendEmail(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        String[] temp = msg.split("=");
        if (!ProjectContext.userEmailMap.containsKey(temp[GrobalConfig.TWO])) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOEMAILUSER));
            return;
        }
        if (temp.length == GrobalConfig.FOUR) {
            sendEmail(user, temp[GrobalConfig.TWO], temp[GrobalConfig.THREE], null);
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.SUCCESSSENDEMIAL));
            return;
        }
        if (temp.length == GrobalConfig.FIVE) {
            Userbag userbag = sendEmail(user, temp[GrobalConfig.TWO], temp[GrobalConfig.THREE], temp[GrobalConfig.FOUR]);
            if (userbag == null) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOUSERBAGID));
                return;
            }
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.SUCCESSSENDEMIAL));
            channel.writeAndFlush("邮件携带了附件:" + userbag.getName());
            return;
        }
    }

    /**
     * 接收邮件
     * @param channel
     * @param msg
     */
    public void receiveEmail(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.THREE) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        if (ProjectContext.userEmailMap.get(user.getUsername()).containsKey(temp[GrobalConfig.TWO])) {
            Mail mail = ProjectContext.userEmailMap.get(user.getUsername()).get(temp[GrobalConfig.TWO]);
            if (mail.isIfUserBag()) {
                mail.getUserbag().setName(user.getUsername());
                user.getUserBag().add(mail.getUserbag());
                ProjectContext.userEmailMap.get(user.getUsername()).remove(temp[GrobalConfig.TWO]);
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.RECEIVEEMAILSUCCESS));
                return;
            } else {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NORECEIVEEMAIL));
            }
        } else {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.RECEIVEEMAILFAIL));
            return;
        }
    }

    /**
     * 具体发邮件逻辑
     * @param user
     * @param toUser
     * @param text
     * @param userbagId
     * @return
     */
    private Userbag sendEmail(User user, String toUser, String text, String userbagId) {
        Mail mail = new Mail();
        mail.setEmailId(UUID.randomUUID().toString());
        mail.setFromUser(user.getUsername());
        mail.setToUser(toUser);
        mail.setEmailText(text);
        Userbag userbagTemp = null;
        if (userbagId != null) {
            mail.setIfUserBag(true);
            userbagTemp = userbagService.getUserbagByUserbagId(user, userbagId);
            if (userbagTemp == null) {
                return userbagTemp;
            }
            mail.setUserbag(userbagTemp);
            user.getUserBag().remove(userbagTemp);
        }
//      存到全局中
        ProjectContext.userEmailMap.get(toUser).put(mail.getEmailId(), mail);
        return userbagTemp;
    }

}
