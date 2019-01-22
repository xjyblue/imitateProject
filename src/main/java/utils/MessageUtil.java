package utils;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.util.ReferenceCountUtil;

/**
 * @ClassName MessageUtil
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class MessageUtil {

    public static void sendMessage(Channel channel, MessageLite messageLite) {
        channel.writeAndFlush(messageLite);
    }


}
