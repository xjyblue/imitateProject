package service.connectservice.service;

import core.annotation.order.OrderRegion;
import core.config.MessageConfig;
import core.channel.ChannelStatus;
import core.config.OrderConfig;
import core.packet.ServerPacket;
import io.netty.channel.Channel;
import core.annotation.order.Order;
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
@OrderRegion
public class ConnectService {
    /**
     * 登录
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.SELECT_LOGIN_ORDER, status = {ChannelStatus.COMING})
    public void connect(Channel channel, String msg) {
        ChannelUtil.channelStatus.put(channel, ChannelStatus.LOGIN);
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(MessageConfig.LOGIN_MESSAGE);
        MessageUtil.sendMessage(channel, builder.build());
    }

    /**
     * 注册
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.SELECT_REGISTER_ORDER, status = {ChannelStatus.COMING})
    public void register(Channel channel, String msg) {
        ChannelUtil.channelStatus.put(channel, ChannelStatus.REGISTER);
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(MessageConfig.REGISTER_MESSAGE);
        MessageUtil.sendMessage(channel, builder.build());
    }

}
