package server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import utils.SpringContextUtil;

import java.io.IOException;

/**
 * @ClassName ServerStart
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class ServerStart {
    /**
     * 服务端主层序入口
     * @param args
     * @throws InterruptedException
     * @throws IOException
     */
	public static void main(String[] args) throws InterruptedException, IOException {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:server.xml");
		ServerConfig serverConfig = (ServerConfig) context.getBean("serverConfig");
		serverConfig.serverStart();
	}

}
