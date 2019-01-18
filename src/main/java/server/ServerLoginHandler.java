package server;


import com.google.protobuf.MessageLite;
import core.ServiceDistributor;
import core.packet.PacketProto;
import core.packet.ProtoBufEnum;
import core.packet.client_packet;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utils.ChannelUtil;

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
        if (ProtoBufEnum.protoIndexOfMessage((MessageLite) msg) == ProtoBufEnum.CLIENT_PACKET_NORMALREQ.getiValue()) {
            if (!ChannelUtil.channelToUserMap.containsKey(ctx.channel())) {
                String data = ((client_packet.client_packet_normalreq) msg).getData();
                serviceDistributor.distributeService(ctx.channel(), data);
            } else {
                ctx.fireChannelRead(msg);
            }
        }else {
//          无法处理该协议包，做处理，返回404啊什么的
        }
    }
}
