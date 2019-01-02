package service.emailservice.service;

import component.good.parent.PGood;
import config.MessageConfig;
import service.emailservice.entity.Mail;
import io.netty.channel.Channel;
import context.ProjectContext;
import order.Order;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userbag;
import utils.MessageUtil;

import java.util.Map;
import java.util.UUID;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/11/23 10:36
 */
@Component("emailEvent")
public class EmailService {

    @Order(orderMsg = "qemail")
    public void queryEmail(Channel channel,String msg){
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
                        + ",邮件附件为" + PGood.getGoodNameByUserbag(mailTemp.getUserbag())
                        + ",邮件内容为[" + mailTemp.getEmailText() + "]"));
            } else {
                channel.writeAndFlush(MessageUtil.turnToPacket("您有一封来自" + mailTemp.getFromUser() + "的邮件,邮件编号为" + mailTemp.getEmailId() + ",邮件内容为[" + mailTemp.getEmailText() + "]"));
            }
        }
    }

    @Order(orderMsg = "send=emailservice")
    public void sendEmail(Channel channel,String msg){
        User user = ProjectContext.session2UserIds.get(channel);
        String temp[] = msg.split("=");
        if (!ProjectContext.userEmailMap.containsKey(temp[2])) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOEMAILUSER));
            return;
        }
        if (temp.length == 4) {
            sendEmail(user, temp[2], temp[3], null);
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.SUCCESSSENDEMIAL));
            return;
        }
        if (temp.length == 5) {
            Userbag userbag = sendEmail(user, temp[2], temp[3], temp[4]);
            if (userbag == null) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOUSERBAGID));
                return;
            }
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.SUCCESSSENDEMIAL));
            channel.writeAndFlush("邮件携带了附件:" + userbag.getName());
            return;
        }
    }

    @Order(orderMsg = "receive=emailservice")
    public void receiveEmail(Channel channel,String msg){
        User user = ProjectContext.session2UserIds.get(channel);
        String temp[] = msg.split("=");
        if(temp.length!=3){
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        if(ProjectContext.userEmailMap.get(user.getUsername()).containsKey(temp[2])){
            Mail mail = ProjectContext.userEmailMap.get(user.getUsername()).get(temp[2]);
            if(mail.isIfUserBag()){
                mail.getUserbag().setName(user.getUsername());
                user.getUserBag().add(mail.getUserbag());
                ProjectContext.userEmailMap.get(user.getUsername()).remove(temp[2]);
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.RECEIVEEMAILSUCCESS));
                return;
            }else {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NORECEIVEEMAIL));
            }
        }else {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.RECEIVEEMAILFAIL));
            return;
        }
    }

    private Userbag getUserBagById(User user, String userBagId) {
        for (Userbag userbag : user.getUserBag()) {
            if (userbag.getId().equals(userBagId)) {
                return userbag;
            }
        }
        return null;
    }

    private Userbag sendEmail(User user, String toUser, String text, String userbagId) {
        Mail mail = new Mail();
        mail.setEmailId(UUID.randomUUID().toString());
        mail.setFromUser(user.getUsername());
        mail.setToUser(toUser);
        mail.setEmailText(text);
        Userbag userbagTemp = null;
        if (userbagId != null) {
            mail.setIfUserBag(true);
            userbagTemp = getUserBagById(user, userbagId);
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
