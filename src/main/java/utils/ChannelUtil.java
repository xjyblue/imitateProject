package utils;

import io.netty.channel.Channel;
import core.context.ProjectContext;
import core.packet.PacketProto;
import pojo.User;

/**
 * @ClassName ChannelUtil
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class ChannelUtil {
    /**
     * 将消息添加到用户队列中
     * @param channel
     * @param packet
     */
    public static void addPacketToUser(Channel channel, PacketProto.Packet packet) {
        User user = ProjectContext.session2UserIds.get(channel);
        user.getPacketsQueue().add(packet);
    }

}
