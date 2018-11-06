package test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import mapper.UserMapper;
import pojo.User;

@RunWith(SpringJUnit4ClassRunner.class)

@ContextConfiguration(locations = { "classpath*:server.xml" })
public class Test {
	@Autowired
	private UserMapper userMapper;

	@org.junit.Test

	public void test1() {
		User user = new User();
		user.setUsername("a");
		user.setPos("1");
		user.setPassword("123456");
		user.setStatus("0");
		userMapper.updateByPrimaryKey(user);
	}
}
