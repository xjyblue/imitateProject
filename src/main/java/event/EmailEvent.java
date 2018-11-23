package event;

import config.MessageConfig;
import email.Mail;
import io.netty.channel.Channel;
import memory.NettyMemory;
import org.springframework.stereotype.Component;
import pojo.User;
import utils.MessageUtil;

import java.util.Map;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/11/23 10:36
 */
@Component("emailEvent")
public class EmailEvent {

    public void email(Channel channel, String msg) {
        User user = NettyMemory.session2UserIds.get(channel);
        if(msg.equals("email")){
//            展示用户的email信息
            Map<String, Mail> emailMap = NettyMemory.userEmailMap.get(user.getUsername());
            if(emailMap.size()==0){
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.EMPTYEMAIL));
                return;
            }
        }
        if(msg.startsWith("email-send")){

        }
        if(msg.startsWith("")){

        }
    }
}
