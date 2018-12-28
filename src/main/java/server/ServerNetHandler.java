package server;

import component.Scene;
import config.BuffConfig;
import context.ProjectContext;
import event.EventStatus;
import event.TeamEvent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.netty.channel.ChannelHandler.Sharable;
import packet.PacketProto;
import packet.PacketType;
import pojo.User;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static packet.PacketProto.Packet.newBuilder;

/**
 * Description ：nettySpringServer  再加一层处理网络，防止网络波动
 * Created by xiaojianyu on 2018/12/21 14:29
 */
@Sharable
@Service("serverNetHandler")
public class ServerNetHandler extends ChannelHandlerAdapter {

    public static final Map<Channel, Integer> heartCounts = new ConcurrentHashMap<>();
    @Autowired
    private TeamEvent teamEvent;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        heartCounts.put(ctx.channel(),0);
        PacketProto.Packet packet = (PacketProto.Packet) msg;
        if (packet.getPacketType().equals(PacketProto.Packet.PacketType.HEARTBEAT)) {
//          客户端心跳包处理
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//      渠道激活，对每个渠道发送登录消息
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() + "客户端与服务端连接开始...");
        heartCounts.put(channel, 0);

//      有渠道连接进来的时候
        ProjectContext.eventStatus.put(channel, EventStatus.COMING);
        PacketProto.Packet.Builder builder = newBuilder();
        builder.setPacketType(PacketProto.Packet.PacketType.DATA);
        builder.setData("欢迎来到【星宇征服】,请按以下提示操作：d:登录 z:注册");
        builder.setType(PacketType.NORMALMSG);
        PacketProto.Packet packetResp = builder.build();
        ctx.writeAndFlush(packetResp);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端与服务端连接断开----inactive");
//      已登录的情况下断开
        if (ProjectContext.session2UserIds.containsKey(ctx.channel())) {
            User user = ProjectContext.session2UserIds.get(ctx.channel());
//          顶号关闭，更改渠道和用户的绑定和渠道的事件状态就OK
            if (user.isOccupied()) {
                ProjectContext.eventStatus.remove(ctx.channel());
                ProjectContext.session2UserIds.remove(ctx.channel());
                user.setOccupied(false);
            } else {
//              这个状态是为了解决线程安全问题，volatile可见保证
                user.setIfOnline(false);
                if (user.getTeamId() != null) {
//              处理一下用户的team对用户的处理
                    teamEvent.handleUserOffline(user);
                }
                Scene scene = ProjectContext.sceneMap.get(user.getPos());
                scene.getUserMap().remove(user.getUsername());
//              移除玩家的所有buff终止时间
                if (user != null && ProjectContext.userBuffEndTime.containsKey(user)) {
                    ProjectContext.userBuffEndTime.remove(user);
                }
//          移除怪物的buff终止时间
                if (user != null && ProjectContext.userToMonsterMap.containsKey(user)) {
                    ProjectContext.userToMonsterMap.remove(user);
                }
                if (ProjectContext.session2UserIds.containsKey(ctx.channel())) {
                    ProjectContext.session2UserIds.remove(ctx.channel());
                }
                if (ProjectContext.eventStatus.containsKey(ctx.channel())) {
                    ProjectContext.eventStatus.remove(ctx.channel());
                }
                if (user != null && ProjectContext.userToChannelMap.containsKey(user)) {
                    ProjectContext.userToChannelMap.remove(user);
                }
                if (ProjectContext.userskillrelationMap.containsKey(user)) {
                    ProjectContext.userskillrelationMap.remove(user);
                }
            }
            heartCounts.remove(ctx.channel());
        } else {
//          未登录的情况下断开
            ProjectContext.eventStatus.remove(ctx.channel());
            heartCounts.remove(ctx.channel());
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            int counter = heartCounts.get(ctx.channel());
            // 空闲5s之后触发 (心跳包丢失)
            if (counter >= 15) {
                // 连续丢失3个心跳包 (断开连接)
                // 可以改成 30秒检查一次丢包  1分钟检查一次丢包 3分钟检查一次丢包 5分钟不上就是玩家上不来了清除渠道信息
                ctx.channel().close().sync();
                System.out.println("已与Client断开连接");
            } else {
                counter++;
                System.out.println(ctx.channel().remoteAddress() + "丢失了第 " + counter + " 个心跳包");
            }
            heartCounts.put(ctx.channel(), counter);
        }
    }


    /**
     * 处理心跳包
     */
    private void handleHeartbreat(ChannelHandlerContext ctx, PacketProto.Packet packet) {
//      将心跳丢失计数器置为0
        heartCounts.put(ctx.channel(), 0);

//		收到心跳包，这里屏蔽掉表示一直有心跳，不然显示很乱
//		System.out.println("收到"+ctx.channel().remoteAddress()+"心跳包");

//		这个位置由于是个心跳的定时任务，可以更新所有buff的状态，但是控不到30ms以内，最好重写心跳handler才能控到30ms以内
//		实际开发中由服务器发送心跳给客户端，客户端和服务器的心跳不用同步的，服务端慢点，客户端可以快点。
//		由于此次是为了实现任务，采用重开一个线程去处理所有的buf而不是使用心跳这个线程来处理，实际开发buf处理应使用心跳
    }
}
