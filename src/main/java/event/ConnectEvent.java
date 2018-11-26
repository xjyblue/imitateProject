package event;

import config.MessageConfig;
import io.netty.channel.Channel;
import memory.NettyMemory;
import org.springframework.stereotype.Component;
import utils.MessageUtil;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/11/26 12:03
 */
@Component("connectEvent")
public class ConnectEvent {
    public void connect(Channel channel, String msg) {
        if (msg.equals("d")) {
            NettyMemory.eventStatus.put(channel, EventStatus.LOGIN);
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.LOGINMESSAGE));
        }
        if (msg.equals("z")) {
            NettyMemory.eventStatus.put(channel, EventStatus.REGISTER);
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.REGISTERMESSAGE));
        }
    }
}
