package core.packet;

import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.EmptyByteBuf;
import io.netty.buffer.Unpooled;
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

    private static final int REDUCE_LENGTH = 4;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
       try {
           if(byteBuf.isReadable()){
//          读取长度
               int length = byteBuf.readInt();
//          获取协议头
               int protoIndex = byteBuf.readInt();
//          去除协议头后的长度
               byte[] data = new byte[length - REDUCE_LENGTH];

               byteBuf.readBytes(data);

//      根据协议头获得协议解析器
               Parser parser = ProtoBufEnum.parserOfProtoIndex(protoIndex);
//      根据协议头处理协议体
               MessageLite messageLite = null;
               messageLite = (MessageLite) parser.parsePartialFrom(data);
               out.add(messageLite);
           }
       }catch (Exception e){
           e.printStackTrace();
       }
    }
}
