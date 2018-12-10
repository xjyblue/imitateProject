package event;

import component.parent.Good;
import config.MessageConfig;
import email.Mail;
import io.netty.channel.Channel;
import memory.NettyMemory;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userbag;
import utils.MessageUtil;

import java.util.Map;
import java.util.UUID;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/11/23 10:36
 */
@Component("emailEvent")
public class EmailEvent {

    public void email(Channel channel, String msg) {
        User user = NettyMemory.session2UserIds.get(channel);
        if (msg.equals("email")) {
//            展示用户的email信息
            Map<String, Mail> emailMap = NettyMemory.userEmailMap.get(user.getUsername());
            if (emailMap.size() == 0) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.EMPTYEMAIL));
                return;
            }
            for (Map.Entry<String, Mail> entry : emailMap.entrySet()) {
                Mail mailTemp = entry.getValue();
                if (mailTemp.isIfUserBag()) {
                    channel.writeAndFlush(MessageUtil.turnToPacket("您有一封来自" + mailTemp.getFromUser() + "的邮件,邮件编号为" + mailTemp.getEmailId()
                            + ",邮件附件为" + Good.getGoodNameByUserbag(mailTemp.getUserbag())
                            + ",邮件内容为[" + mailTemp.getEmailText() + "]"));
                } else {
                    channel.writeAndFlush(MessageUtil.turnToPacket("您有一封来自" + mailTemp.getFromUser() + "的邮件,邮件编号为" + mailTemp.getEmailId() + ",邮件内容为[" + mailTemp.getEmailText() + "]"));
                }
            }
        }
        if (msg.startsWith("email=send")) {
            String temp[] = msg.split("=");
            if (!NettyMemory.userEmailMap.containsKey(temp[2])) {
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
        if (msg.startsWith("email=receive=")) {
            String temp[] = msg.split("=");
            if(temp.length!=3){
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
                return;
            }
            if(NettyMemory.userEmailMap.get(user.getUsername()).containsKey(temp[2])){
                Mail mail = NettyMemory.userEmailMap.get(user.getUsername()).get(temp[2]);
                if(mail.isIfUserBag()){
                    mail.getUserbag().setName(user.getUsername());
                    user.getUserBag().add(mail.getUserbag());
                    NettyMemory.userEmailMap.get(user.getUsername()).remove(temp[2]);
                    channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.RECEIVEEMAILSUCCESS));
                    return;
                }
            }else {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.RECEIVEEMAILFAIL));
                return;
            }
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
        NettyMemory.userEmailMap.get(toUser).put(mail.getEmailId(), mail);
        return userbagTemp;
    }

}
