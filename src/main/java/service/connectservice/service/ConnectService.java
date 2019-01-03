package service.connectservice.service;

import core.config.MessageConfig;
import core.ChannelStatus;
import io.netty.channel.Channel;
import core.context.ProjectContext;
import order.Order;
import org.springframework.stereotype.Component;
import utils.MessageUtil;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/11/26 12:03
 */
@Component
public class ConnectService {

    @Order(orderMsg = "d")
    public void connect(Channel channel, String msg) {
        ProjectContext.eventStatus.put(channel, ChannelStatus.LOGIN);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.LOGINMESSAGE));
    }

    @Order(orderMsg = "z")
    public void register(Channel channel, String msg) {
        ProjectContext.eventStatus.put(channel, ChannelStatus.REGISTER);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.REGISTERMESSAGE));
    }

}
