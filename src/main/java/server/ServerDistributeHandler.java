package server;

import component.Scene;
import event.EventStatus;
import event.TeamEvent;
import io.netty.handler.timeout.IdleStateEvent;
import context.ProjectContext;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import event.EventDistributor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.util.ReferenceCountUtil;
import packet.PacketProto;
import packet.PacketType;
import pojo.User;
import utils.ChannelUtil;

import java.io.IOException;


@Sharable
@Service("serverDistributeHandler")
public class ServerDistributeHandler extends SimpleChannelInboundHandler<String> {


    //	Channel注册到EventLoop
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }


    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof PacketProto.Packet) {
                PacketProto.Packet packet = (PacketProto.Packet) msg;
                handleData(ctx, packet);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {

    }

    private void handleData(ChannelHandlerContext ctx, PacketProto.Packet packet) throws IOException {
        ChannelUtil.addPacketToUser(ctx.channel(), packet);
    }


}
