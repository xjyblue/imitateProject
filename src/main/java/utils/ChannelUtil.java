package utils;

import core.channel.ChannelUserInfo;
import io.netty.channel.Channel;
import core.context.ProjectContext;
import core.packet.PacketProto;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import pojo.User;

/**
 * @ClassName ChannelUtil
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class ChannelUtil {

    public static final AttributeKey<ChannelUserInfo> CHANNEL_TO_USER_KEY = AttributeKey.valueOf("channelToUserKey");

    /**
     * 给channel的attribute添加用户属性
     * @param channel
     * @param user
     */
    public static void setUserInfoToChannel(Channel channel, User user) {
        Attribute<ChannelUserInfo> attr = channel.attr(CHANNEL_TO_USER_KEY);
        ChannelUserInfo channelUserInfo = new ChannelUserInfo(user.getUsername(), user);
        attr.set(channelUserInfo);
    }

    /**
     * 从channel中获取用户信息
     * @param channel
     */
    public static User getUserInfoFromChannel(Channel channel) {
        Attribute<ChannelUserInfo> attr = channel.attr(CHANNEL_TO_USER_KEY);
        ChannelUserInfo channelUserInfo = attr.get();
        return channelUserInfo.getUser();
    }

    /**
     * 将消息添加到用户队列中
     *
     * @param channel
     * @param packet
     */
    public static void addPacketToUser(Channel channel, PacketProto.Packet packet) {
        User user = ProjectContext.channelToUserMap.get(channel);
        user.getPacketsQueue().add(packet);
    }


}
