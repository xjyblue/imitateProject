package service.connectservice.service;

import config.MessageConfig;
import event.EventStatus;
import io.netty.channel.Channel;
import context.ProjectContext;
import order.Order;
import org.springframework.stereotype.Component;
import utils.MessageUtil;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/11/26 12:03
 */
@Component("connectEvent")
public class ConnectService {

    @Order(orderMsg = "d")
    public void connect(Channel channel, String msg) {
        ProjectContext.eventStatus.put(channel, EventStatus.LOGIN);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.LOGINMESSAGE));
    }

    @Order(orderMsg = "z")
    public void register(Channel channel, String msg) {
        ProjectContext.eventStatus.put(channel, EventStatus.REGISTER);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.REGISTERMESSAGE));
    }

}
