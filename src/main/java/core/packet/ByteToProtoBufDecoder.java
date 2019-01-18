package core.packet;

import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @ClassName ByteToProtoBufDecoder
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/17 10:26
 * @Version 1.0
 **/
public class ByteToProtoBufDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
//      获取协议头
        int protoIndex = byteBuf.readInt();
//      根据协议头获得协议解析器
        Parser parser= ProtoBufEnum.parserOfProtoIndex(protoIndex);
//      根据协议头处理协议体
        try (ByteBufInputStream bufInputStream=new ByteBufInputStream(byteBuf)){
            MessageLite messageLite= (MessageLite) parser.parseFrom(bufInputStream);
            //将消息传递下去，或者在这里将消息发布出去
            ctx.fireChannelRead(messageLite);
        }
    }
}
