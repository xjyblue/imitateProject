package xiaojianyu.main;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import xiaojianyu.controller.NettyServer;
import xiaojianyu.controller.NettyServerHandler;

import java.io.IOException;

/**
 * 服务端主程序入口
 * @author xiaojianyu
 *
 */
public class NettyStart {
	
	private static Logger logger = Logger.getLogger(NettyStart.class);
	
	public static void main(String[] args) throws InterruptedException, IOException {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:server.xml");
		NettyServer nettyServer = (NettyServer) context.getBean("nettyServer");
		nettyServer.serverStart();
	}
}
