package test;

import com.google.common.collect.Maps;
import mapper.ApplyunioninfoMapper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import mapper.UserMapper;
import pojo.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

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

		System.out.println(ResultCode.BACKPACK_UPDATE);
//		User user1 = userMapper.getUser("z","zz");
//		System.out.println(user1.getPos());

//		int count = applyunioninfoMapper.selectByUserIdAndUnionId("1","1");
//		System.out.println(count);
	}

}
