package client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import utils.DelimiterUtils;

/**
 * 
 * @author xiaojianyu
 *
 */
public class ClientHandler extends ChannelHandlerAdapter {
	 
    @Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	msg = DelimiterUtils.removeDelimiter(msg.toString());
        System.out.println("客户端收到：" +msg.toString());
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    	
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if(null != cause) cause.printStackTrace();
        if(null != ctx) ctx.close();
    }
 
}
