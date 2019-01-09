package server;


import core.ServiceDistributor;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import core.context.ProjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import core.packet.PacketProto;

/**
 * @ClassName ServerLoginHandler
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/

@Sharable
@Service("serverLoginHandler")
public class ServerLoginHandler extends ChannelHandlerAdapter {
    @Autowired
    private ServiceDistributor serviceDistributor;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(!ProjectContext.channelToUserMap.containsKey(ctx.channel())){
//          没登录走这里
            if (msg instanceof PacketProto.Packet) {
                PacketProto.Packet packet = (PacketProto.Packet) msg;
                serviceDistributor.distributeEvent(ctx.channel(),packet.getData());
            }
        }else {
            ctx.fireChannelRead(msg);
        }
    }
}
