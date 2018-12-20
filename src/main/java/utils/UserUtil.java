package utils;

import io.netty.channel.Channel;
import context.ProjectContext;
import pojo.User;

import java.util.Map;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/12/17 14:57
 */
public class UserUtil {

    public static User getUserByName(String s) {
        for (Map.Entry<Channel, User> entry : ProjectContext.session2UserIds.entrySet()) {
            if (entry.getValue().getUsername().equals(s)) {
                return entry.getValue();
            }
        }
        return null;
    }

}
