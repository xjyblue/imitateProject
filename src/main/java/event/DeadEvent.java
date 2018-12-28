package event;

import component.BossScene;
import component.Scene;
import config.DeadOrAliveConfig;
import config.MessageConfig;
import context.ProjectUtil;
import io.netty.channel.Channel;
import mapper.UserMapper;
import context.ProjectContext;
import order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import utils.LevelUtil;
import utils.MessageUtil;
import utils.UserUtil;

import java.lang.reflect.InvocationTargetException;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/11/26 12:11
 */
@Component("deadEvent")
public class DeadEvent {
    @Autowired
    private ChatEvent chatEvent;
    @Autowired
    private UserMapper userMapper;

    @Order(orderMsg = "chat-,chatAll")
    public void chatEvent(Channel channel, String msg) throws InvocationTargetException, IllegalAccessException {
        ProjectUtil.reflectAnnotation(chatEvent, channel, msg);
    }

    @Order(orderMsg = "y")
    public void reborn(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        if (!user.getPos().equals("0")) {
            Scene scene = ProjectContext.sceneMap.get(user.getPos());
//          这句是为了解决普通场景复活和怪物副本复活的bug
            if(scene.getUserMap().containsKey(user.getUsername())){
                scene.getUserMap().remove(user.getUsername());
            }
        }

        Scene sceneTarget = ProjectContext.sceneMap.get("0");
        sceneTarget.getUserMap().put(user.getUsername(), user);

        user.setStatus(DeadOrAliveConfig.ALIVE);
        user.setPos("0");
        UserUtil.recoverUser(user);
        userMapper.updateByPrimaryKeySelective(user);

        ProjectContext.eventStatus.put(channel, EventStatus.STOPAREA);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.LIVEINSTART));

    }
}
