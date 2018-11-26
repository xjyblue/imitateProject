package xiaojianyu.controller;
import event.EventStatus;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import event.EventDistributor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.util.ReferenceCountUtil;
import memory.NettyMemory;
import packet.PacketProto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static packet.PacketProto.Packet.newBuilder;

@Sharable
@Service("nettyServerHandler")
public class NettyServerHandler extends SimpleChannelInboundHandler<String> {

	private static Logger logger = Logger.getLogger(NettyServerHandler.class);
	
	public static final ChannelGroup group = NettyMemory.group;

	public static final Map<Channel,Integer> heartCounts = new ConcurrentHashMap<>();

	@Autowired
	private EventDistributor eventDistributor;
	
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
	    Channel channel = ctx.channel();
	    group.add(channel);
		heartCounts.put(channel,0);
	}

//	Channel注册到EventLoop
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		super.channelRegistered(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("客户端与服务端连接断开");
		if(NettyMemory.session2UserIds.containsKey(ctx.channel()))
		{
			NettyMemory.session2UserIds.remove(ctx.channel());
		}
		
		if(NettyMemory.eventStatus.containsKey(ctx.channel())) {
			NettyMemory.eventStatus.remove(ctx.channel());
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Channel channel = ctx.channel();
		logger.info(channel.remoteAddress() + "客户端与服务端连接开始...");
		NettyMemory.eventStatus.put(channel, EventStatus.COMING);
		PacketProto.Packet.Builder builder = newBuilder();
		builder.setPacketType(PacketProto.Packet.PacketType.DATA);
		builder.setData("d:登录 z:注册");
		PacketProto.Packet packetResp = builder.build();
		ctx.writeAndFlush(packetResp);
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			if (msg instanceof PacketProto.Packet) {
				PacketProto.Packet packet = (PacketProto.Packet) msg;
				switch (packet.getPacketType()) {
					case HEARTBEAT:
						handleHeartbreat(ctx, packet);
						break;
					case DATA:
						handleData(ctx, packet);
						break;
					default:
						break;
				}
			}
		} finally {
			ReferenceCountUtil.release(msg);
		}
	}

	private void handleData(ChannelHandlerContext ctx, PacketProto.Packet packet) {
		for (Channel ch : group) {
			if (ch == ctx.channel()) {
				heartCounts.put(ch,0);
				eventDistributor.distributeEvent(ctx, packet.getData());
			}
		}
	}


	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			int counter = heartCounts.get(ctx.channel());
			// 空闲5s之后触发 (心跳包丢失)
			if (counter >= 3) {
				// 连续丢失3个心跳包 (断开连接)
				ctx.channel().close().sync();
				System.out.println("已与Client断开连接");
			} else {
				counter++;
				System.out.println(ctx.channel().remoteAddress()+"丢失了第 " + counter + " 个心跳包");
			}
			heartCounts.put(ctx.channel(),counter);
		}
	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {

	}

	/**
	 * 处理心跳包
	 */
	private void handleHeartbreat(ChannelHandlerContext ctx, PacketProto.Packet packet) {
		// 将心跳丢失计数器置为0
		// counter = 0;
		heartCounts.put(ctx.channel(),0);
		System.out.println("收到"+ctx.channel().remoteAddress()+"心跳包");
		ReferenceCountUtil.release(packet);
	}

}
