package utils;

import component.Equipment;
import io.netty.channel.Channel;
import context.ProjectContext;
import level.Level;
import mapper.UserMapper;
import pojo.User;
import pojo.Weaponequipmentbar;

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

    public static void recoverUser(User user) {
        int userHp = Integer.parseInt(LevelUtil.getMaxHp(user));
        int userMp = Integer.parseInt(LevelUtil.getMaxMp(user));
        user.setHp(userHp + "");
        user.setMp(userMp + "");
//      更新用户血量和mp
        UserMapper userMapper = SpringContextUtil.getBean("userMapper");
        userMapper.updateByPrimaryKeySelective(user);
    }

}
