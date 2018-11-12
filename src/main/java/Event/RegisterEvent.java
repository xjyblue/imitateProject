package Event;

import io.netty.channel.Channel;
import mapper.UserMapper;
import memory.NettyMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import utils.DelimiterUtils;

@Component("registerEvent")
public class RegisterEvent {
    @Autowired
    private UserMapper userMapper;

    public void register(Channel channel, String msg) {
        String []temp = msg.split("-");
        if (temp.length != 3) {
            channel.writeAndFlush(DelimiterUtils.addDelimiter("输入错误命令"));
        } else {
            if (!temp[1].equals(temp[2])) {
                channel.writeAndFlush(DelimiterUtils.addDelimiter("两次密码不一致"));
            } else {
                User user = new User();
                user.setUsername(temp[0]);
                user.setPassword(temp[1]);
                user.setStatus("1");
                user.setPos("0");
                userMapper.insert(user);
                channel.writeAndFlush(DelimiterUtils.addDelimiter("注册成功请输入：用户名-密码进行登录"));
                NettyMemory.eventStatus.put(channel, EventStatus.LOGIN);
            }
        }
    }
}
