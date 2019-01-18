package client;

import com.google.protobuf.MessageLite;
import core.packet.ProtoBufEnum;
import core.packet.client_packet;
import core.packet.server_packet;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import io.netty.handler.timeout.IdleStateEvent;

import javax.swing.*;

/**
 * @ClassName ClientHandler
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/

public class ClientHandler extends ChannelHandlerAdapter {
    private ClientConfig clientConfig;
    private ClientStart clientStart;

    ClientHandler(ClientConfig clientConfig, ClientStart clientStart) {
        this.clientConfig = clientConfig;
        this.clientStart = clientStart;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

//      收到玩家消息的时候触发
        try {
            handleData(ctx, msg);
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }


    private void handleData(ChannelHandlerContext ctx, Object msg) {
        int respType = ProtoBufEnum.protoIndexOfMessage((MessageLite) msg);
        JTextArea jTextArea = null;
        String respData = null;
        if (respType == ProtoBufEnum.SERVER_PACKET_NORMALRESP.getiValue()) {
            server_packet.server_packet_normalresp resp = (server_packet.server_packet_normalresp) msg;
            jTextArea = clientStart.getjTextArea1();
            respData = resp.getData();
        }
//        if (packet.getType().equals(PacketType.NORMALMSG)) {
//            jTextArea = clientStart.getjTextArea1();
//        }
//        if (packet.getType().equals(PacketType.USERBUFMSG)) {
//            jTextArea = clientStart.getjTextArea2();
//        }
//        if (packet.getType().equals(PacketType.MONSTERBUFMSG)) {
//            jTextArea = clientStart.getjTextArea3();
//        }
//        if (packet.getType().equals(PacketType.ATTACKMSG)) {
//            jTextArea = clientStart.getjTextArea4();
//        }
//        if (packet.getType().equals(PacketType.TRADEMSG)) {
//            jTextArea = clientStart.getjTextArea5();
//            jTextArea.setText("");
//        }
//        if (packet.getType().equals(PacketType.USERINFO)) {
//            clientStart.setTitle("用户[" + packet.getData() + "]客户端");
//            return;
//        }
//        if (packet.getType().equals(PacketType.UNIONINFO)) {
//            jTextArea = clientStart.getjTextArea6();
//            jTextArea.setText("");
//        }
//        if (packet.getType().equals(PacketType.USERBAGMSG)) {
//            jTextArea = clientStart.getjTextArea8();
//            jTextArea.setText("");
//        }
//        if (packet.getType().equals(PacketType.FRIENDMSG)) {
//            jTextArea = clientStart.getjTextArea9();
//            jTextArea.setText("");
//        }
//        if (packet.getType().equals(PacketType.ACHIEVEMENT)) {
//            jTextArea = clientStart.getjTextArea7();
//            jTextArea.setText("");
//            jTextArea.setText(packet.getData());
//            jTextArea.setCaretPosition(jTextArea.getDocument().getLength());
//            return;
//        }
//        if(packet.getType().equals(PacketType.CHANGECHANNEL)){
//            clientConfig.setChannel(null);
//            jTextArea = clientStart.getjTextArea1();
//        }
        String resp = jTextArea.getText();
        resp += "客户端收到：" + respData + System.getProperty("line.separator");
        jTextArea.setText(resp);
        jTextArea.setCaretPosition(jTextArea.getDocument().getLength());
//
//        System.out.println("客户端收到：" + packet.getData());
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//      玩家网络掉线的时候触发---------客户端
        System.out.println("--- ServerConfig is inactive ---");
        super.channelInactive(ctx);
        clientConfig.doConnect();
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (null != ctx) {
            ctx.close();
        }
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            // 不管是读事件空闲还是写事件空闲都向服务器发送心跳包
            sendHeartbeatPacket(ctx);
        }
    }

    /**
     * 发送空闲心跳包
     */
    private void sendHeartbeatPacket(ChannelHandlerContext ctx) {
        if (clientStart.flag) {
            client_packet.client_packet_normalreq.Builder builder = client_packet.client_packet_normalreq.newBuilder();
            ctx.channel().writeAndFlush(builder.build());
        }
    }
}
