package event;

import config.MessageConfig;
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
            channel.writeAndFlush(DelimiterUtils.addDelimiter(MessageConfig.ERRORORDER));
        } else {
            if (!temp[1].equals(temp[2])) {
                channel.writeAndFlush(DelimiterUtils.addDelimiter(MessageConfig.DOUBLEPASSWORDERROR));
            } else {
                User user = new User();
                user.setUsername(temp[0]);
                user.setPassword(temp[1]);
                user.setStatus("1");
                user.setPos("0");
                userMapper.insert(user);
                channel.writeAndFlush(DelimiterUtils.addDelimiter(MessageConfig.REGISTERSUCCESS));
                NettyMemory.eventStatus.put(channel, EventStatus.LOGIN);
            }
        }
    }
}
