package Event;

import io.netty.channel.Channel;
import mapper.UserMapper;
import mapper.UserbagMapper;
import mapper.UserskillrelationMapper;
import memory.NettyMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.*;
import utils.DelimiterUtils;

import java.util.*;

@Component("loginEvent")
public class LoginEvent {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserskillrelationMapper userskillrelationMapper;

    public void login(Channel channel, String msg) {
        String temp[] = msg.split("-");
        if (temp.length != 2) {
            channel.writeAndFlush(DelimiterUtils.addDelimiter("输入错误命令"));
        } else {
            User user = userMapper.getUser(temp[0],temp[1]);
            if (user == null) {
                channel.writeAndFlush(DelimiterUtils.addDelimiter("账户密码出错"));
            } else {
 //             初始化玩家的技能start
                UserskillrelationExample userskillrelationExample = new UserskillrelationExample();
                UserskillrelationExample.Criteria criteria = userskillrelationExample.createCriteria();
                criteria.andUsernameEqualTo(user.getUsername());
                List<Userskillrelation> userskillrelations = userskillrelationMapper.selectByExample(userskillrelationExample);
                Map<String, Userskillrelation> userskillrelationMap = new HashMap<>();
                for (Userskillrelation userskillrelation : userskillrelations) {
                    userskillrelationMap.put(userskillrelation.getKeypos(), userskillrelation);
                }
                NettyMemory.userskillrelationMap.put(channel, userskillrelationMap);
//                初始化玩家的各种buffer
                //TODO:这里可以改成数据库初始化玩家buffer
                Map<String,Integer> map = new HashMap<>();
                map.put("mpBuff",1000);
                map.put("poisoningBuff",2000);
                map.put("defenseBuff",3000);
                user.setBufferMap(map);
//              初始化每个用户buff的终止时间
                Map<String,Long> mapSecond = new HashMap<>();
                mapSecond.put("mpBuff",1000l);
                mapSecond.put("poisoningBuff",2000l);
                mapSecond.put("defenseBuff",3000l);
                if(!NettyMemory.buffEndTime.containsKey(user)){
                    NettyMemory.buffEndTime.put(user,mapSecond);
                }
//                初始化玩家的技能end
                NettyMemory.session2UserIds.put(channel, user);

                channel.writeAndFlush(DelimiterUtils.addDelimiter("登录成功，你已进入" + NettyMemory.areaMap.get(user.getPos()).getName()));
                NettyMemory.eventStatus.put(channel, EventStatus.STOPAREA);
            }
        }
    }
}
