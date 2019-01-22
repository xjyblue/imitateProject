package client;

import com.google.protobuf.MessageLite;
import core.packet.ClientPacket;
import core.packet.ProtoBufEnum;
import core.packet.ServerPacket;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import io.netty.handler.timeout.IdleStateEvent;
import utils.MessageUtil;

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
        try {
            int respType = ProtoBufEnum.protoIndexOfMessage((MessageLite) msg);
            JTextArea jTextArea = null;
            String respData = null;
            if (respType == ProtoBufEnum.SERVER_PACKET_NORMALRESP.getiValue()) {
                jTextArea = clientStart.getjTextArea1();
                ServerPacket.NormalResp normalResp = (ServerPacket.NormalResp) msg;
                respData = normalResp.getData();
            }
            if (respType == ProtoBufEnum.SERVER_PACKET_USERBUFRESP.getiValue()) {
                jTextArea = clientStart.getjTextArea2();
                ServerPacket.UserbufResp userbufResp = (ServerPacket.UserbufResp) msg;
                respData = userbufResp.getData();
            }
            if (respType == ProtoBufEnum.SERVER_PACKET_MONSTERBUFRESP.getiValue()) {
                jTextArea = clientStart.getjTextArea3();
                ServerPacket.MonsterbufResp monsterbufResp = (ServerPacket.MonsterbufResp) msg;
                respData = monsterbufResp.getData();
            }
            if (respType == ProtoBufEnum.SERVER_PACKET_ATTACKRESP.getiValue()) {
                jTextArea = clientStart.getjTextArea4();
                ServerPacket.AttackResp attackResp = (ServerPacket.AttackResp) msg;
                respData = attackResp.getData();
            }
            if (respType == ProtoBufEnum.SERVER_PACKET_TRADERESP.getiValue()) {
                jTextArea = clientStart.getjTextArea5();
                jTextArea.setText("");
                ServerPacket.TradeResp tradeResp = (ServerPacket.TradeResp) msg;
                respData = tradeResp.getData();
                jTextArea.setCaretPosition(jTextArea.getDocument().getLength());
            }
            if (respType == ProtoBufEnum.SERVER_PACKET_USERINFORESP.getiValue()) {
                ServerPacket.UserinfoResp userinfoResp = (ServerPacket.UserinfoResp) msg;
                clientStart.setTitle("用户[" + userinfoResp.getData() + "]客户端");
                return;
            }
            if (respType == ProtoBufEnum.SERVER_PACKET_UNIONRESP.getiValue()) {
                jTextArea = clientStart.getjTextArea6();
                jTextArea.setText("");
                ServerPacket.UnionResp unionResp = (ServerPacket.UnionResp) msg;
                respData = unionResp.getData();
            }
            if (respType == ProtoBufEnum.SERVER_PACKET_USERBAGRESP.getiValue()) {
                ServerPacket.UserbagResp userbagResp = (ServerPacket.UserbagResp) msg;
                respData = userbagResp.getData();
                jTextArea = clientStart.getjTextArea8();
                jTextArea.setText("");
            }
            if (respType == ProtoBufEnum.SERVER_PACKET_FRIENDRESP.getiValue()) {
                jTextArea = clientStart.getjTextArea9();
                jTextArea.setText("");
                ServerPacket.FriendResp friendResp = (ServerPacket.FriendResp) msg;
                respData = friendResp.getData();
            }
            if (respType == ProtoBufEnum.SERVER_PACKET_ACHIEVEMENTRESP.getiValue()) {
                jTextArea = clientStart.getjTextArea7();
                jTextArea.setText("");
                ServerPacket.AchievementResp normalResp = (ServerPacket.AchievementResp) msg;
                respData = normalResp.getData();
                jTextArea.setCaretPosition(jTextArea.getDocument().getLength());
            }
            if (respType == ProtoBufEnum.SERVER_PACKET_CHANGECHANNELRESP.getiValue()) {
                clientConfig.setChannel(null);
                jTextArea = clientStart.getjTextArea1();
            }
            String resp = jTextArea.getText();
            resp += "客户端收到：" + respData + System.getProperty("line.separator");
            jTextArea.setText(resp);
            jTextArea.setCaretPosition(jTextArea.getDocument().getLength());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
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
            ClientPacket.PingReq.Builder builder = ClientPacket.PingReq.newBuilder();
            MessageUtil.sendMessage(ctx.channel(), builder.build());
        }
    }
}
