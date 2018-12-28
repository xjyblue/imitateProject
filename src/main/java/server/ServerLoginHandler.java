package server;


import config.BuffConfig;
import event.EventDistributor;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import context.ProjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import packet.PacketProto;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/12/18 9:35
 */
@Sharable
@Service("serverLoginHandler")
public class ServerLoginHandler extends ChannelHandlerAdapter {
    @Autowired
    private EventDistributor eventDistributor;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(!ProjectContext.session2UserIds.containsKey(ctx.channel())){
//          没登录走这里
            if (msg instanceof PacketProto.Packet) {
                PacketProto.Packet packet = (PacketProto.Packet) msg;
                eventDistributor.distributeEvent(ctx.channel(),packet.getData());
            }
        }else {
            ctx.fireChannelRead(msg);
        }
    }
}
