package xiaojianyu.controller;
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
import utils.DelimiterUtils;
@Sharable
@Service("nettyServerHandler")
public class NettyServerHandler extends SimpleChannelInboundHandler<String> {

	private static Logger logger = Logger.getLogger(NettyServerHandler.class);
	
	public static final ChannelGroup group = NettyMemory.group;

	@Autowired
	private EventDistributor eventDistributor;
	
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
	    Channel channel = ctx.channel();
	    group.add(channel);
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
		ctx.writeAndFlush(DelimiterUtils.addDelimiter("d:登录 z:注册"));
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			Channel channel = ctx.channel();
			String msg_ = DelimiterUtils.removeDelimiter(msg.toString());
			logger.info(channel.remoteAddress() + "输入结果：" + msg.toString());
			System.out.println(group.size());
			for(Channel ch : group) {
				if(ch == channel) {
					eventDistributor.distributeEvent(ctx, msg_);
				}
			}
		} finally {
			ReferenceCountUtil.release(msg);
		}
	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {

	}

}
