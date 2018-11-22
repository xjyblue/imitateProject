package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import common.PacketProto;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

import static common.PacketProto.Packet.newBuilder;

/**
 * @author xiaojianyu
 */
public class Client {
    private int port;
    private String host;
    private Bootstrap bootstrap;
    private Channel channel;

    public Client(int port, String host) {
        this.host = host;
        this.port = port;
        start();
    }

    private void start() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class)
                // 保持连接
                .option(ChannelOption.SO_KEEPALIVE, true)
                // 有数据立即发送
                .option(ChannelOption.TCP_NODELAY, true)
//              .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(2048))
                // 绑定处理group
                .group(eventLoopGroup).remoteAddress(host, port)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        // 初始化编码器，解码器，处理器
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new ProtobufVarint32FrameDecoder());
                        pipeline.addLast(new ProtobufEncoder());
                        pipeline.addLast(new ProtobufDecoder(PacketProto.Packet.getDefaultInstance()));
//                       读空闲心跳，写空闲心跳，读或者写空闲心跳
                        pipeline.addLast(new IdleStateHandler(0, 5, 0));
                        socketChannel.pipeline().addLast(
                                new ClientHandler(Client.this));
                    }
                });
        // 进行连接
        doConnect();
    }

    /**
     * 抽取出该方法 (断线重连时使用)
     */
    protected void doConnect() {
        if (channel != null && channel.isActive()) {
            return;
        }
        ChannelFuture future = bootstrap.connect("127.0.0.1", 8081);
//      监听通道异步连接
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture futureListener) throws Exception {
                if (futureListener.isSuccess()) {
                    channel = futureListener.channel();
                    System.out.println("客户端连接成功");
                } else {
                    System.out.println("失败尝试5s后重连");
//                  EventLoop 始终由一个线程驱动
//                  一个 EventLoop 可以被指派来服务多个 Channel
//                  一个 Channel 只拥有一个 EventLoop
                    futureListener.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            doConnect();
                        }
                    }, 5, TimeUnit.SECONDS);
                }
            }
        });
    }


    public void sendMessage(Object msg) {
        if (channel != null) {
            PacketProto.Packet.Builder builder = newBuilder();
            builder.setPacketType(PacketProto.Packet.PacketType.DATA);
            builder.setData((String) msg);
            PacketProto.Packet packet = builder.build();
            channel.writeAndFlush(packet);
        }
    }
}