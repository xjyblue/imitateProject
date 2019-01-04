package server;

import org.springframework.stereotype.Service;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import core.packet.PacketProto;
import utils.ChannelUtil;

import java.io.IOException;

/**
 * @ClassName ServerDistributeHandler
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Sharable
@Service("serverDistributeHandler")
public class ServerDistributeHandler extends SimpleChannelInboundHandler<String> {


    /**
     * Channel注册到EventLoop
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }


    @Override
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
