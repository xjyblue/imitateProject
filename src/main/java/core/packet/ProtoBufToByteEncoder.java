package core.packet;

import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @ClassName ProtoBufToByteEncoder
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/17 11:07
 * @Version 1.0
 **/
public class ProtoBufToByteEncoder extends MessageToByteEncoder<MessageLite> {

    private static final int REDUCE_LENGTH = 4;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessageLite messageLite, ByteBuf byteBuf) throws Exception {
        encode(messageLite, byteBuf);
    }

    protected void encode(MessageLite messageLite, ByteBuf byteBuf) throws IOException {
        try {
            //protoLength 表示有效内容的长度(不包括自身)
            int protoLength = REDUCE_LENGTH + messageLite.getSerializedSize();
            //先获取消息对应的枚举编号,传进来的是messageLite无法获得协议编号,根据类型获取协议编号
            int protoIndex = ProtoBufEnum.protoIndexOfMessage(messageLite);
            if (protoIndex == -1) {
                throw new UnsupportedEncodingException("UnsupportedEncodingProtoBuf " + messageLite.getClass().getSimpleName());
            }
            //写入长度
            byteBuf.writeInt(protoLength);
            //写入协议头
            byteBuf.writeInt(protoIndex);
//      写入数据
            byteBuf.writeBytes(messageLite.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
