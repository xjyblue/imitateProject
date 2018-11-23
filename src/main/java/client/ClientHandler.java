package client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import common.PacketProto;
import io.netty.handler.timeout.IdleStateEvent;

import static common.PacketProto.Packet.newBuilder;

/**
 * @author xiaojianyu
 */
public class ClientHandler extends ChannelHandlerAdapter {
    private Client client;
    ClientHandler(Client client) {
        this.client = client;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//      收到玩家消息的时候触发
        if (msg instanceof PacketProto.Packet) {
            PacketProto.Packet packet = (PacketProto.Packet) msg;
            System.out.println("客户端收到：" + packet.getData());
        }
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//      玩家网络掉线的时候触发---------客户端
        System.out.println("--- Server is inactive ---");
        super.channelInactive(ctx);
        client.doConnect();
    }



    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        if (null != cause) cause.printStackTrace();
        if (null != ctx) ctx.close();
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            // 不管是读事件空闲还是写事件空闲都向服务器发送心跳包
            sendHeartbeatPacket(ctx);
        }
    }

    /**
     * 发送心跳包
     */
    private void sendHeartbeatPacket(ChannelHandlerContext ctx) {
//        System.out.println("开始发送心跳包");
        PacketProto.Packet.Builder builder = newBuilder();
        builder.setPacketType(PacketProto.Packet.PacketType.HEARTBEAT);
        PacketProto.Packet packet = builder.build();
        ctx.writeAndFlush(packet);
    }
}
