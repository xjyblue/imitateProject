package service.emailservice.service;

import core.channel.ChannelStatus;
import core.annotation.Order;
import core.annotation.Region;
import core.config.GrobalConfig;
import core.config.MessageConfig;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import service.caculationservice.service.MoneyCaculationService;
import service.caculationservice.service.UserbagCaculationService;
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
@Region
public class EmailService {
    @Autowired
    private UserbagService userbagService;
    @Autowired
    private UserbagCaculationService userbagCaculationService;
    @Autowired
    private MoneyCaculationService moneyCaculationService;

    /**
     * 展示所有邮件
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "qmail", status = {ChannelStatus.COMMONSCENE})
    public void queryEmail(Channel channel, String msg) {
//      展示用户的email信息
        User user = ProjectContext.channelToUserMap.get(channel);
        Map<String, Mail> emailMap = ProjectContext.userEmailMap.get(user.getUsername());
        if (emailMap.size() == 0) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.EMPTYEMAIL));
            return;
        }
        for (Map.Entry<String, Mail> entry : emailMap.entrySet()) {
            Mail mailTemp = entry.getValue();
            if (mailTemp.isIfUserBag()) {
                channel.writeAndFlush(MessageUtil.turnToPacket("您有一封来自" + mailTemp.getFromUser() + "的邮件,邮件编号为" + mailTemp.getEmailId()
                        + ",邮件附件为" + userbagService.getGoodNameByUserbag(mailTemp.getUserbag())
                        + ",邮件内容为[" + mailTemp.getEmailText() + "]"));
            } else {
                channel.writeAndFlush(MessageUtil.turnToPacket("您有一封来自" + mailTemp.getFromUser() + "的邮件,邮件编号为" + mailTemp.getEmailId() + ",邮件内容为[" + mailTemp.getEmailText() + "]"));
            }
        }
    }

    /**
     * 发送邮件
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "sendmail", status = {ChannelStatus.COMMONSCENE})
    public void sendEmail(Channel channel, String msg) {
        User user = ProjectContext.channelToUserMap.get(channel);
        String[] temp = msg.split("=");
        if (!ProjectContext.userEmailMap.containsKey(temp[GrobalConfig.ONE])) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOEMAILUSER));
            return;
        }
        if (temp.length == GrobalConfig.THREE) {
            send(user.getUsername(), temp[GrobalConfig.ONE], temp[GrobalConfig.TWO], null, null);
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.SUCCESSSENDEMIAL));
            return;
        }
        if (temp.length == GrobalConfig.FIVE) {
            Userbag userbag = userbagService.getUserbagByUserbagId(user, temp[GrobalConfig.THREE]);
            if (userbag == null) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOUSERBAGID));
                return;
            }
            if (!userbagService.checkUserbagNum(userbag, temp[GrobalConfig.FOUR])) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOENOUGHCHANGEGOOD));
                return;
            }
            Userbag userbagNew = new Userbag();
            BeanUtils.copyProperties(userbag, userbagNew);
            userbagNew.setNum(Integer.parseInt(temp[4]));
            userbagCaculationService.removeUserbagFromUser(user, userbag, Integer.parseInt(temp[4]));
            send(user.getUsername(), temp[GrobalConfig.ONE], temp[GrobalConfig.TWO], userbagNew, null);
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.SUCCESSSENDEMIAL));
            channel.writeAndFlush("邮件携带了附件:" + userbag.getName());
            return;
        }
    }

    /**
     * 接收邮件
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "receivemail", status = {ChannelStatus.COMMONSCENE})
    public void receiveEmail(Channel channel, String msg) {
        User user = ProjectContext.channelToUserMap.get(channel);
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.TWO) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        if (!ProjectContext.userEmailMap.get(user.getUsername()).containsKey(temp[GrobalConfig.ONE])) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.RECEIVEEMAILFAIL));
            return;
        }
        Mail mail = ProjectContext.userEmailMap.get(user.getUsername()).get(temp[GrobalConfig.ONE]);
        if (mail.isIfUserBag()) {
            userbagCaculationService.addUserBagForUser(user, mail.getUserbag());
        }
        if (mail.getMoney() != null) {
            moneyCaculationService.addMoneyToUser(user, mail.getMoney().toString());
        }
        ProjectContext.userEmailMap.get(user.getUsername()).remove(temp[GrobalConfig.ONE]);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.RECEIVEEMAILSUCCESS));
    }

    /**
     * 系统邮件
     */
    public void systemSendMail(String toUser, String text, Userbag userbag, Integer money) {
        send(GrobalConfig.SYSTEM_EMAIL, toUser, text, userbag, money);
    }

    /**
     * 具体发邮件逻辑
     *
     * @param
     * @param toUser
     * @param text
     * @return
     */
    private void send(String username, String toUser, String text, Userbag userbag, Integer money) {
        Mail mail = new Mail();
        mail.setEmailId(UUID.randomUUID().toString());
        mail.setFromUser(username);
        mail.setToUser(toUser);
        mail.setEmailText(text);
        if (userbag != null) {
            mail.setIfUserBag(true);
            mail.setUserbag(userbag);
        }
        if (money != null) {
            mail.setMoney(money);
        }
//      存到全局中
        ProjectContext.userEmailMap.get(toUser).put(mail.getEmailId(), mail);
    }
}
