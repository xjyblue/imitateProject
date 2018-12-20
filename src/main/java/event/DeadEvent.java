package event;

import component.BossScene;
import component.Scene;
import config.MessageConfig;
import io.netty.channel.Channel;
import mapper.UserMapper;
import context.ProjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import utils.LevelUtil;
import utils.MessageUtil;

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

    public void dead(Channel channel, String msg) {
        if (msg.startsWith("chat")) {
            chatEvent.chat(channel, msg);
            return;
        }
        if (msg.equals("y")) {
            User user = ProjectContext.session2UserIds.get(channel);


//          人物复活
            Scene scene = ProjectContext.sceneMap.get(user.getPos());
            scene.getUserMap().remove(user.getUsername());
            Scene sceneTarget = ProjectContext.sceneMap.get("0");
            sceneTarget.getUserMap().put(user.getUsername(), user);


            user.setStatus("1");
            user.setHp(LevelUtil.getMaxHp(user));
            user.setPos("0");
            userMapper.updateByPrimaryKeySelective(user);


            ProjectContext.eventStatus.put(channel, EventStatus.STOPAREA);
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.LIVEINSTART));
            if (user.getTeamId() != null && ProjectContext.bossAreaMap.containsKey(user.getTeamId())) {
                BossScene bossScene = ProjectContext.bossAreaMap.get(user.getTeamId());
                bossScene.getDamageAll().put(user, "0");
            }
            return;
        }
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.DEADNOOPERATE));
    }

    private boolean bossAreaContaisUser(Channel channel) {
        return ProjectContext.bossAreaMap.containsKey(ProjectContext.session2UserIds.get(channel).getTeamId());
    }
}
