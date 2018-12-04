package client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import packet.PacketProto;
import io.netty.handler.timeout.IdleStateEvent;
import packet.PacketType;

import javax.swing.*;

import static packet.PacketProto.Packet.newBuilder;

/**
 * @author xiaojianyu
 */
public class ClientHandler extends ChannelHandlerAdapter {
    private Client client;
    private ClientStart clientStart;

    ClientHandler(Client client, ClientStart clientStart) {
        this.client = client;
        this.clientStart = clientStart;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

//      收到玩家消息的时候触发
        try {
            if (msg instanceof PacketProto.Packet) {
                PacketProto.Packet packet = (PacketProto.Packet) msg;
                switch (packet.getPacketType()) {
                    case HEARTBEAT:
                        handleHeartbreat(ctx, msg);
                        break;
                    case DATA:
                        handleData(ctx, msg);
                        break;
                    default:
                        break;
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void handleData(ChannelHandlerContext ctx, Object msg) {
        PacketProto.Packet packet = (PacketProto.Packet) msg;
        JTextArea jTextArea = null;
        if(packet.getType().equals(PacketType.NORMALMSG)){
           jTextArea = clientStart.getjTextArea1();
        }
        if(packet.getType().equals(PacketType.USERBUFMSG)){
            jTextArea = clientStart.getjTextArea2();
        }
        if(packet.getType().equals(PacketType.MONSTERBUFMSG)){
            jTextArea = clientStart.getjTextArea3();
        }
        if(packet.getType().equals(PacketType.ATTACKMSG)){
            jTextArea = clientStart.getjTextArea4();
        }
        if(packet.getType().equals(PacketType.TRADEMSG)){
            jTextArea = clientStart.getjTextArea5();
            jTextArea.setText("");
        }
        String resp = jTextArea.getText();
        resp += "客户端收到：" + packet.getData()+System.getProperty("line.separator");
        jTextArea.setText(resp);

        jTextArea.setCaretPosition(jTextArea.getDocument().getLength());

        System.out.println("客户端收到：" + packet.getData());
    }

    private void handleHeartbreat(ChannelHandlerContext ctx, Object msg) {
        PacketProto.Packet packet = (PacketProto.Packet) msg;
//      这一句话不打印，只是用来测试能收到服务端的持续心跳
//        System.out.println("客户端收到心跳包");
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
        if(clientStart.flag){
            PacketProto.Packet.Builder builder = newBuilder();
            builder.setPacketType(PacketProto.Packet.PacketType.HEARTBEAT);
            PacketProto.Packet packet = builder.build();
            ctx.writeAndFlush(packet);
        }
    }
}
