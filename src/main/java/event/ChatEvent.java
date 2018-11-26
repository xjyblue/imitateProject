package event;

import config.MessageConfig;
import io.netty.channel.Channel;
import memory.NettyMemory;
import org.springframework.stereotype.Component;
import pojo.User;
import utils.MessageUtil;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/11/23 9:30
 */
@Component("chatEvent")
public class ChatEvent {

    public void chat(Channel channel, String msg) {
        User user = NettyMemory.session2UserIds.get(channel);
        if(msg.startsWith("chatAll")){
            String temp[] = msg.split("-");
            if(temp.length!=2){
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
                return;
            }
            for(Channel channelTemp : NettyMemory.group){
                if(NettyMemory.session2UserIds.get(channelTemp)==user){
                    channelTemp.writeAndFlush(MessageUtil.turnToPacket("你发送了全服喇叭，消息为>>>>>"+temp[1]+"<<<<<"));
                }else {
                    channelTemp.writeAndFlush(MessageUtil.turnToPacket("您收到来自"+user.getUsername()+"的全服大喇叭:"+temp[1]));
                }
            }
            return;
        }else if(msg.startsWith("chat-")){
            String temp[] = msg.split("-");
            if(temp.length!=3){
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
                return;
            }
            for(Channel channelTemp : NettyMemory.group){
                User userTemp = NettyMemory.session2UserIds.get(channelTemp);
                if(userTemp.getUsername().equals(temp[1])){
                    channelTemp.writeAndFlush(MessageUtil.turnToPacket("您收到来自"+user.getUsername()+"的私聊大喇叭:"+temp[2]));
                    return;
                }
            }
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOONLINEUSER));
            //若用户未登陆，要保存该用户的聊天信息，一般保存在数据库，这里我保存在内存中
        }else {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
        }
    }
}
