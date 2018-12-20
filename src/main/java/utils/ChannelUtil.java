package utils;

import io.netty.channel.Channel;
import context.ProjectContext;
import packet.PacketProto;
import pojo.User;

/**
 * Description ：nettySpringServer 后面可扩展更多的功能
 * Created by server on 2018/12/18 11:48
 */
public class ChannelUtil {

    public static void addPacketToUser(Channel channel, PacketProto.Packet packet) {
        User user = ProjectContext.session2UserIds.get(channel);
        user.getPacketsQueue().add(packet);
    }

}
