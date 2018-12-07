package test;

import level.Level;
import mapper.ApplyunioninfoMapper;
import memory.NettyMemory;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import mapper.UserMapper;
import pojo.User;
import xiaojianyu.controller.NettyServer;

@RunWith(SpringJUnit4ClassRunner.class)

@ContextConfiguration(locations = { "classpath*:/server.xml" })
public class Test {
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private ApplyunioninfoMapper applyunioninfoMapper;
	@org.junit.Test

	public void test1() {
//		多打一句话
//		User user = new User();
//		user.setUsername("a");
//		user.setPos("1");
//		user.setPassword("123456");
//		user.setStatus("0");
//		userMapper.updateByPrimaryKey(user);

//		User user1 = userMapper.getUser("z","zz");
//		System.out.println(user1.getPos());

		int count = applyunioninfoMapper.selectByUserIdAndUnionId("1","1");
		System.out.println(count);
	}
}
