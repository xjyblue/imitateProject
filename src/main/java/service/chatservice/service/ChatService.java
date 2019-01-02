package service.chatservice.service;

import config.MessageConfig;
import io.netty.channel.Channel;
import context.ProjectContext;
import order.Order;
import org.springframework.stereotype.Component;
import pojo.User;
import utils.MessageUtil;

import java.util.Map;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/11/23 9:30
 */
@Component
public class ChatService {

    @Order(orderMsg = "chatAll")
    public void chatAll(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        String temp[] = msg.split("-");
        if (temp.length != 2) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
//      广播一次全服大喇叭
        for (Map.Entry<Channel, User> entry : ProjectContext.session2UserIds.entrySet()) {
            Channel channelTemp = entry.getKey();
            if (entry.getValue() == user) {
                channelTemp.writeAndFlush(MessageUtil.turnToPacket("你发送了全服喇叭，消息为>>>>>" + temp[1] + "<<<<<"));
            } else {
                channelTemp.writeAndFlush(MessageUtil.turnToPacket("您收到来自" + user.getUsername() + "的全服大喇叭:" + temp[1]));
            }
        }
    }

    @Order(orderMsg = "chat-")
    public void chatOne(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        String temp[] = msg.split("-");
        if (temp.length != 3) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }

        for (Map.Entry<Channel, User> entry : ProjectContext.session2UserIds.entrySet()) {
            Channel channelTemp = entry.getKey();
            if(entry.getValue().getUsername().equals(temp[1])){
                channelTemp.writeAndFlush(MessageUtil.turnToPacket("您收到来自" + user.getUsername() + "的私聊大喇叭:" + temp[2]));
                return;
            }
        }

        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOONLINEUSER));
    }

}
