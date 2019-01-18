package utils;

import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import pojo.User;

import java.util.Map;

/**
 * @ClassName ChannelUtil
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class ChannelUtil {
    /**
     * 全局渠道
     */
    public static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    /**
     * 信道所处事件的装填
     */
    public final static Map<Channel, String> channelStatus = Maps.newConcurrentMap();
    /**
     * 根据用户拿去对应的渠道
     */
    public final static Map<User, Channel> userToChannelMap = Maps.newConcurrentMap();
    /**
     * 缓存通信上下文环境对应的登录用户
     */
    public final static Map<Channel, User> channelToUserMap = Maps.newConcurrentMap();



    /**
     * 将消息添加到用户队列中
     *
     * @param channel
     */
    public static void addPacketToUser(Channel channel, Object o) {
        User user = channelToUserMap.get(channel);
        user.getPacketsQueue().add(o);
    }


}
