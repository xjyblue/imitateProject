package Event;

import io.netty.channel.Channel;
import mapper.UserMapper;
import mapper.UserskillrelationMapper;
import memory.NettyMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userskillrelation;
import pojo.UserskillrelationExample;
import utils.DelimiterUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            User user2 = userMapper.selectByPrimaryKey(temp[0]);
            if (user2 == null || (!user2.getPassword().equals(temp[1]))) {
                channel.writeAndFlush(DelimiterUtils.addDelimiter("账户密码出错"));
            } else {
 //                             初始化玩家的技能start
                UserskillrelationExample userskillrelationExample = new UserskillrelationExample();
                UserskillrelationExample.Criteria criteria = userskillrelationExample.createCriteria();
                criteria.andUsernameEqualTo(user2.getUsername());
                List<Userskillrelation> userskillrelations = userskillrelationMapper.selectByExample(userskillrelationExample);
                Map<String, Userskillrelation> userskillrelationMap = new HashMap<>();
                for (Userskillrelation userskillrelation : userskillrelations) {
                    userskillrelationMap.put(userskillrelation.getKeypos(), userskillrelation);
                }
                NettyMemory.userskillrelationMap.put(channel, userskillrelationMap);
//                              初始化玩家的技能end
                NettyMemory.session2UserIds.put(channel, user2);

                channel.writeAndFlush(DelimiterUtils.addDelimiter("登录成功，你已进入" + NettyMemory.areaMap.get(user2.getPos()).getName()));
                NettyMemory.eventStatus.put(channel, EventStatus.STOPAREA);
            }
        }
    }
}
