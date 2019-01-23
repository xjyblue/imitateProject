package server;

import core.config.GrobalConfig;
import core.packet.ByteToProtoBufDecoder;
import core.packet.ProtoBufToByteEncoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import mapper.UserMapper;
import org.springframework.stereotype.Component;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import org.springframework.beans.factory.annotation.Autowired;


import java.io.IOException;

/**
 * @ClassName ServerConfig
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/

@Component
@Slf4j
public class ServerConfig {
    @Autowired
    private ServerLoginHandler serverLoginHandler;
    @Autowired
    private ServerDistributeHandler serverDistributeHandler;
    @Autowired
    private ServerNetHandler serverNetHandler;

    /**
     * 程序初始方法入口注解，提示spring这个程序先执行这里
     *
     * @throws InterruptedException
     * @throws IOException
     */
    public void serverStart() throws InterruptedException, IOException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
//                            ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                            ch.pipeline().addLast(new ByteToProtoBufDecoder());
                            ch.pipeline().addLast(new ProtoBufToByteEncoder());
//                          写空闲,每隔3秒触发心跳包丢失统计
                            ch.pipeline().addLast(new IdleStateHandler(3, 0, 0));
//                          处理空闲客户端网络心跳包，网络波动处理在这里
                            ch.pipeline().addLast(serverNetHandler);
//                          这里会有并发问题，记得处理
                            ch.pipeline().addLast(serverLoginHandler);
//                          这里只负责派发任务给用户，用户自己去消费packet
                            ch.pipeline().addLast(serverDistributeHandler);
                        }
                    });
            log.info("开放8080端口");
            ChannelFuture f = b.bind(GrobalConfig.PORTNUM).sync();
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


}