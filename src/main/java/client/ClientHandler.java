package client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import memory.NettyMemory;
import utils.DelimiterUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author xiaojianyu
 *
 */
public class ClientHandler extends ChannelHandlerAdapter {


	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	msg = DelimiterUtils.removeDelimiter(msg.toString());
    	String[] temp=((String) msg).split("#");
    	if(temp.length==3){
                ClientMemory.skillTime.put(temp[2],Long.parseLong(temp[1]));
                System.out.println("客户端收到：" +temp[0]);
        }else {
            System.out.println("客户端收到：" +msg.toString());
        }
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
