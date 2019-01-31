package server.nettySpringServer;

import mapper.TeamMapper;
import mapper.UnioninfoMapper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pojo.Unioninfo;
import service.teamservice.entity.Team;

import java.util.UUID;

/**
 * @ClassName Test
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/30 9:55
 * @Version 1.0
 **/
@RunWith(SpringJUnit4ClassRunner.class)

@ContextConfiguration(locations = {"classpath*:/server.xml"})
public class Test {
    @Autowired
    private UnioninfoMapper unioninfoMapper;
    @Autowired
    private TeamMapper teamMapper;

    @org.junit.Test

    public void test1() {
//		多打一句话
//		User user = new User();
//		user.setUsername("a");
//		user.setPos("1");
//		user.setPassword("123456");
//		user.setStatus("0");
//		userMapper.updateByPrimaryKey(user);

//        Unioninfo unioninfo = unioninfoMapper.selectUnionByUnionName("武术会");
//        System.out.println(unioninfo);
        Team team = new Team();
        team.setTeamId(UUID.randomUUID().toString());
        team.setTeamName("我也不知道这个是什么队伍");
        team.setLeaderId("a");
        teamMapper.insert(team);
    }
}
