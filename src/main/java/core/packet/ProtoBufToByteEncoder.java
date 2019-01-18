package core.packet;

import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
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

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessageLite messageLite, ByteBuf byteBuf) throws Exception {
        encode(messageLite, byteBuf);
    }

    protected void encode(MessageLite messageLite, ByteBuf byteBuf) throws IOException {
        //先获取消息对应的枚举编号,传进来的是messageLite无法获得协议编号,根据类型获取协议编号
        int protoIndex = ProtoBufEnum.protoIndexOfMessage(messageLite);
        if (protoIndex == -1) {
            throw new UnsupportedEncodingException("UnsupportedEncodingProtoBuf " + messageLite.getClass().getSimpleName());
        }
        //protoLength 表示有效内容的长度(不包括自身)
        int protoLength = 4 + messageLite.getSerializedSize();
        byteBuf.writeInt(protoLength);
        //写入协议头
        byteBuf.writeInt(protoIndex);
        try (ByteBufOutputStream byteBufOutputStream = new ByteBufOutputStream(byteBuf)) {
            messageLite.writeTo(byteBufOutputStream);
        }
    }
}
