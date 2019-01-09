package service.connectservice.service;

import core.config.MessageConfig;
import core.ChannelStatus;
import io.netty.channel.Channel;
import core.context.ProjectContext;
import core.order.Order;
import org.springframework.stereotype.Component;
import utils.MessageUtil;

/**
 * @ClassName ConnectService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
public class ConnectService {
    /**
     * 登录
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "d")
    public void connect(Channel channel, String msg) {
        ProjectContext.channelStatus.put(channel, ChannelStatus.LOGIN);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.LOGINMESSAGE));
    }

    /**
     * 注册
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "z")
    public void register(Channel channel, String msg) {
        ProjectContext.channelStatus.put(channel, ChannelStatus.REGISTER);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.REGISTERMESSAGE));
    }

}
