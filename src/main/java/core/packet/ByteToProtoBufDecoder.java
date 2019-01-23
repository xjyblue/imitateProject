package core.packet;

import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.EmptyByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @ClassName ByteToProtoBufDecoder
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/17 10:26
 * @Version 1.0
 **/
@Slf4j
public class ByteToProtoBufDecoder extends ByteToMessageDecoder {

    private static final int REDUCE_LENGTH = 4;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        try {
            if (byteBuf.readableBytes() < REDUCE_LENGTH) {
                return;
            }
//          配合下面使用，标记位
            byteBuf.markReaderIndex();
//          读取长度
            int length = byteBuf.readInt();
//          读到的消息体长度如果小于我们传送过来的消息长度，则resetReaderIndex. 这个配合markReaderIndex使用的。把readIndex重置到mark的地方
            if (byteBuf.readableBytes() < length) {
                byteBuf.resetReaderIndex();
                return;
            }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
