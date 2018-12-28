package server;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * 服务端主程序入口
 * @author server
 *
 */
public class ServerStart {

	public static void main(String[] args) throws InterruptedException, IOException {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:server.xml");
		ServerConfig serverConfig = (ServerConfig) context.getBean("serverConfig");
		serverConfig.serverStart();
	}

}
