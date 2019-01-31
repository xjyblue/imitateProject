package service.emailservice.service;

import config.impl.db.EmailDbLoad;
import core.channel.ChannelStatus;
import core.annotation.Order;
import core.annotation.Region;
import core.config.GrobalConfig;
import core.config.MessageConfig;
import core.packet.ServerPacket;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import service.caculationservice.service.MoneyCaculationService;
import service.caculationservice.service.UserbagCaculationService;
import service.emailservice.entity.Mail;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userbag;
import service.userbagservice.service.UserbagService;
import utils.ChannelUtil;
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
        User user = ChannelUtil.channelToUserMap.get(channel);
        Map<String, Mail> emailMap = EmailDbLoad.userEmailMap.get(user.getUsername());
        if (emailMap.size() == 0) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.EMPTYEMAIL);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        for (Map.Entry<String, Mail> entry : emailMap.entrySet()) {
            Mail mailTemp = entry.getValue();
            if (mailTemp.isIfUserBag() && mailTemp.getUserbag() != null) {
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData("您有一封来自" + mailTemp.getFromUser() + "的邮件,邮件编号为" + mailTemp.getEmailId()
                        + ",邮件附件为" + userbagService.getGoodNameByUserbag(mailTemp.getUserbag())
                        + ",邮件内容为[" + mailTemp.getEmailText() + "]");
                MessageUtil.sendMessage(channel, builder.build());
            } else {
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData("您有一封来自" + mailTemp.getFromUser() + "的邮件,邮件编号为" + mailTemp.getEmailId() + ",邮件内容为[" + mailTemp.getEmailText() + "]");
                MessageUtil.sendMessage(channel, builder.build());
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
        User user = ChannelUtil.channelToUserMap.get(channel);
        String[] temp = msg.split("=");
        if (!EmailDbLoad.userEmailMap.containsKey(temp[GrobalConfig.ONE])) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NOEMAILUSER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        if (temp.length == GrobalConfig.THREE) {
            send(user.getUsername(), temp[GrobalConfig.ONE], temp[GrobalConfig.TWO], null, null);
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.SUCCESSSENDEMIAL);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        if (temp.length == GrobalConfig.FIVE) {
            Userbag userbag = userbagService.getUserbagByUserbagId(user, temp[GrobalConfig.THREE]);
            if (userbag == null) {
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData(MessageConfig.NOUSERBAGID);
                MessageUtil.sendMessage(channel, builder.build());
                return;
            }
            if (!userbagService.checkUserbagNum(userbag, temp[GrobalConfig.FOUR])) {
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData(MessageConfig.NOENOUGHCHANGEGOOD);
                MessageUtil.sendMessage(channel, builder.build());
                return;
            }
            Userbag userbagNew = new Userbag();
            BeanUtils.copyProperties(userbag, userbagNew);
            userbagNew.setNum(Integer.parseInt(temp[4]));
            userbagCaculationService.removeUserbagFromUser(user, userbag, Integer.parseInt(temp[4]));
            send(user.getUsername(), temp[GrobalConfig.ONE], temp[GrobalConfig.TWO], userbagNew, null);
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.SUCCESSSENDEMIAL);
            MessageUtil.sendMessage(channel, builder.build());
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
        User user = ChannelUtil.channelToUserMap.get(channel);
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.TWO) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERRORORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        if (!EmailDbLoad.userEmailMap.get(user.getUsername()).containsKey(temp[GrobalConfig.ONE])) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.RECEIVEEMAILFAIL);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        Mail mail = EmailDbLoad.userEmailMap.get(user.getUsername()).get(temp[GrobalConfig.ONE]);
        if (mail.isIfUserBag()) {
            userbagCaculationService.addUserBagForUser(user, mail.getUserbag());
//          接收完附件邮件还在
            EmailDbLoad.userEmailMap.get(user.getUsername()).get(temp[GrobalConfig.ONE]).setUserbag(null);
        }
        if (mail.getMoney() != null) {
            moneyCaculationService.addMoneyToUser(user, mail.getMoney().toString());
//          接收完金币邮件还在
            EmailDbLoad.userEmailMap.get(user.getUsername()).get(temp[GrobalConfig.ONE]).setMoney(null);
        }
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(MessageConfig.RECEIVEEMAILSUCCESS);
        MessageUtil.sendMessage(channel, builder.build());
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
        EmailDbLoad.userEmailMap.get(toUser).put(mail.getEmailId(), mail);
    }
}
