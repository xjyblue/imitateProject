package service.connectservice.service;

import core.annotation.Region;
import core.config.MessageConfig;
import core.channel.ChannelStatus;
import io.netty.channel.Channel;
import core.annotation.Order;
import org.springframework.stereotype.Component;
import utils.ChannelUtil;
import utils.MessageUtil;

/**
 * @ClassName ConnectService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
@Region
public class ConnectService {
    /**
     * 登录
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "dl", status = {ChannelStatus.COMING})
    public void connect(Channel channel, String msg) {
        ChannelUtil.channelStatus.put(channel, ChannelStatus.LOGIN);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.LOGINMESSAGE));
    }

    /**
     * 注册
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "zc", status = {ChannelStatus.COMING})
    public void register(Channel channel, String msg) {
        ChannelUtil.channelStatus.put(channel, ChannelStatus.REGISTER);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.REGISTERMESSAGE));
    }

}
