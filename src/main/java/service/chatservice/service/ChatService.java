package service.chatservice.service;

import core.channel.ChannelStatus;
import core.annotation.Order;
import core.annotation.Region;
import core.config.GrobalConfig;
import core.config.MessageConfig;
import core.packet.ServerPacket;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;
import pojo.User;
import service.teamservice.entity.TeamCache;
import utils.ChannelUtil;
import utils.MessageUtil;

import java.util.Map;

/**
 * @ClassName ChatService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
@Region
public class ChatService {
    /**
     * 全服大喇叭
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "chatAll", status = {ChannelStatus.COMMONSCENE, ChannelStatus.ATTACK, ChannelStatus.BOSSSCENE, ChannelStatus.DEADSCENE})
    public void chatAll(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.TWO) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERRORORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
//      广播一次全服大喇叭
        for (Map.Entry<Channel, User> entry : ChannelUtil.channelToUserMap.entrySet()) {
            Channel channelTemp = entry.getKey();
            if (entry.getValue() == user) {
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData("您发送了>>>>>>全服大喇叭：" + temp[1] + "<<<<<<");
                MessageUtil.sendMessage(channel, builder.build());
            } else {
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData("您收到来自" + user.getUsername() + "的全服大喇叭:" + temp[1]);
                MessageUtil.sendMessage(channelTemp, builder.build());
            }
        }
    }

    /**
     * 单人聊天
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "chatOne", status = {ChannelStatus.COMMONSCENE, ChannelStatus.ATTACK, ChannelStatus.BOSSSCENE, ChannelStatus.DEADSCENE})
    public void chatOne(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.THREE) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERRORORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }

        for (Map.Entry<Channel, User> entry : ChannelUtil.channelToUserMap.entrySet()) {
            Channel channelTemp = entry.getKey();
            if (entry.getValue().getUsername().equals(temp[1])) {
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData("您收到来自" + user.getUsername() + "的私聊大喇叭:" + temp[2]);
                MessageUtil.sendMessage(channelTemp, builder.build());
                return;
            }
        }

        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(MessageConfig.NOONLINEUSER);
        MessageUtil.sendMessage(channel, builder.build());
    }


    @Order(orderMsg = "chatTeam", status = {ChannelStatus.COMMONSCENE, ChannelStatus.ATTACK, ChannelStatus.BOSSSCENE, ChannelStatus.DEADSCENE})
    public void chatTeam(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.TWO) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERRORORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        if (user.getTeamId() == null) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NO_TEAM_NO_TALK);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        for (Map.Entry<String, User> entry : TeamCache.teamMap.get(user.getTeamId()).getUserMap().entrySet()) {
            Channel channelT = ChannelUtil.userToChannelMap.get(entry.getValue());
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            if (channelT == channel) {
                builder.setData("你发送了组队聊天消息内容为：" + temp[1]);
                MessageUtil.sendMessage(channelT, builder.build());
            } else {
                builder.setData(user.getUsername() + "发送了组队聊天消息内容为：" + temp[1]);
                MessageUtil.sendMessage(channelT, builder.build());
            }
        }
    }
}
