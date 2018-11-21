package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import common.PacketProto;
import io.netty.handler.timeout.IdleStateHandler;

import static common.PacketProto.Packet.newBuilder;

/**
 * @author xiaojianyu
 */
public class Client {
    private int port;
    private String host;
    private SocketChannel socketChannel;
    private static Bootstrap bootstrap;
    private static Channel ch;
    public Client(int port, String host) {
        this.host = host;
        this.port = port;
        start();
    }

    private void start() {

        Thread thread = new Thread(new Runnable() {
            public void run() {
                EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
                bootstrap = new Bootstrap();
                bootstrap.channel(NioSocketChannel.class)
                        // 保持连接
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        // 有数据立即发送
                        .option(ChannelOption.TCP_NODELAY, true)
//                        .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(2048))
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
                                pipeline.addLast(new IdleStateHandler(0, 5, 0));
                                socketChannel.pipeline().addLast(
                                        new ClientHandler());
                            }
                        });
                // 进行连接
                ChannelFuture future;
                try {
                    future = bootstrap.connect(host, port).sync();
                    ch = future.channel();
                    // 判断是否连接成功
                    if (future.isSuccess()) {
                        // 得到管道，便于通信
                        socketChannel = (SocketChannel) future.channel();
                        System.out.println("客户端开启成功..");
                    } else {
                        System.out.println("客户端开启失败...");
                    }
                    // 等待客户端链路关闭，就是由于这里会将线程阻塞，导致无法发送信息，所以我这里开了线程
                    future.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    //优雅地退出，释放相关资源
                    eventLoopGroup.shutdownGracefully();
                }
            }
        });
        thread.start();
    }

    /**
     * 抽取出该方法 (断线重连时使用)
     */
    public static void doConnect() throws InterruptedException {
        ch = bootstrap.connect("127.0.0.1", 8081).sync().channel();
    }

    public void sendMessage(Object msg) {
        if (socketChannel != null) {
            PacketProto.Packet.Builder builder = newBuilder();
            builder.setPacketType(PacketProto.Packet.PacketType.DATA);
            builder.setData((String) msg);
            PacketProto.Packet packet = builder.build();
            socketChannel.writeAndFlush(packet);
        }
    }
}