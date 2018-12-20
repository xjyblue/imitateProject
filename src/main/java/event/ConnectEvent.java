package event;

import config.MessageConfig;
import io.netty.channel.Channel;
import context.ProjectContext;
import org.springframework.stereotype.Component;
import utils.MessageUtil;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/11/26 12:03
 */
@Component("connectEvent")
public class ConnectEvent {
    public void connect(Channel channel, String msg) {
        if (msg.equals("d")) {
            ProjectContext.eventStatus.put(channel, EventStatus.LOGIN);
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.LOGINMESSAGE));
        }
        if (msg.equals("z")) {
            ProjectContext.eventStatus.put(channel, EventStatus.REGISTER);
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.REGISTERMESSAGE));
        }
    }
}
