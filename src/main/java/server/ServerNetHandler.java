package server;

import com.google.protobuf.MessageLite;
import core.config.GrobalConfig;
import core.channel.ChannelStatus;
import core.packet.ProtoBufEnum;
import core.packet.ServerPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import io.netty.channel.ChannelHandler.Sharable;
import pojo.User;
import utils.ChannelUtil;
import utils.MessageUtil;
import utils.ProjectContextUtil;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @ClassName ServerNetHandler
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Sharable
@Slf4j
@Service
public class ServerNetHandler extends ChannelHandlerAdapter {

    public static final Map<Channel, Integer> HEART_COUNTS = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        HEART_COUNTS.put(ctx.channel(), 0);
//      由于客户端全部在一个窗口输入，故只分为心跳包和普通包，心跳包全部在这一层处理，普通包往下跑
        if (ProtoBufEnum.protoIndexOfMessage((MessageLite) msg) == ProtoBufEnum.CLIENT_PACKET_PINGREQ.getiValue()) {
//         心跳包不做回应
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//      渠道激活，对每个渠道发送登录消息
        Channel channel = ctx.channel();
        log.info("端口号为：{}的渠道开始与服务端连接", channel.remoteAddress());
        HEART_COUNTS.put(channel, 0);
//      进来的时候新添加channel
        ChannelUtil.group.add(channel);
//      有渠道连接进来的时候
        ChannelUtil.channelStatus.put(channel, ChannelStatus.COMING);
        for (Channel channel1 : ChannelUtil.group) {
            System.out.println(channel1.remoteAddress());
        }
        sendMessage(ctx);
    }

    private void sendMessage(ChannelHandlerContext ctx) {
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData("欢迎来到【星宇征服】,请按以下提示操作：dl:登录 zc:注册");
        MessageUtil.sendMessage(ctx.channel(), builder.build());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Channel channel = ctx.channel();
        log.info("端口号为{}的渠道与服务器断开连接", channel.remoteAddress());
        //      已登录的情况下断开
        if (ChannelUtil.channelToUserMap.containsKey(ctx.channel())) {
            User user = ChannelUtil.channelToUserMap.get(ctx.channel());
//          顶号关闭，更改渠道和用户的绑定和渠道的事件状态就OK
            if (user != null && user.isIfOccupy()) {
                ChannelUtil.channelStatus.remove(ctx.channel());
                ChannelUtil.channelToUserMap.remove(ctx.channel());
                user.setIfOccupy(false);
            } else if (user != null) {
                ProjectContextUtil.clearContextUserInfo(ctx, user);
            }
            HEART_COUNTS.remove(ctx.channel());
        } else {
//          未登录的情况下断开
            ChannelUtil.channelStatus.remove(ctx.channel());
            HEART_COUNTS.remove(ctx.channel());
        }
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            int counter = HEART_COUNTS.get(ctx.channel());
            // 空闲5s之后触发 (心跳包丢失)
            if (counter >= GrobalConfig.HEARTLOSE) {
                // 连续丢失3个心跳包 (断开连接)
                // 后期可以改成 30秒检查一次丢包  1分钟检查一次丢包 3分钟检查一次丢包 5分钟不上就是玩家上不来了清除渠道信息
                ctx.channel().close().sync();
                log.info("客户端已于服务端断开连接");
            } else {
                counter++;
                log.info("端口号为{}的渠道丢失了{}个心跳包", ctx.channel().remoteAddress(), counter);
            }
            HEART_COUNTS.put(ctx.channel(), counter);
        }
    }


    /**
     * 处理心跳包
     */
    private void handleHeartbreat(ChannelHandlerContext ctx, Object o) {
//      将心跳丢失计数器置为0
        HEART_COUNTS.put(ctx.channel(), 0);

//		收到心跳包，这里屏蔽掉表示一直有心跳，不然显示很乱
//		System.out.println("收到"+ctx.channel().remoteAddress()+"心跳包");

//		这个位置由于是个心跳的定时任务，可以更新所有buff的状态，但是控不到30ms以内，最好重写心跳handler才能控到30ms以内
//		实际开发中由服务器发送心跳给客户端，客户端和服务器的心跳不用同步的，服务端慢点，客户端可以快点。
//		由于此次是为了实现任务，采用重开一个线程去处理所有的buf而不是使用心跳这个线程来处理，实际开发buf处理应使用心跳
    }
}
