package service.deadservice.service;

import service.sceneservice.entity.Scene;
import core.config.GrobalConfig;
import core.config.MessageConfig;
import core.ChannelStatus;
import io.netty.channel.Channel;
import mapper.UserMapper;
import core.context.ProjectContext;
import core.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import service.chatservice.service.ChatService;
import utils.MessageUtil;
import service.userservice.service.UserService;

/**
 * @ClassName DeadService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
public class DeadService {
    @Autowired
    private ChatService chatService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;

    /**
     * 单人聊天
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "chat-")
    public void chatOne(Channel channel, String msg) {
        chatService.chatOne(channel, msg);
    }

    /**
     * 集体聊天
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "chatAll")
    public void chatAll(Channel channel,String msg){
        chatService.chatAll(channel,msg);
    }

    /**
     * 复活
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "y")
    public void reborn(Channel channel, String msg) {
        User user = ProjectContext.channelToUserMap.get(channel);
        if (!user.getPos().equals(GrobalConfig.STARTSCENE)) {
            Scene scene = ProjectContext.sceneMap.get(user.getPos());
//          这句是为了解决普通场景复活和怪物副本复活的bug
            if (scene.getUserMap().containsKey(user.getUsername())) {
                scene.getUserMap().remove(user.getUsername());
            }
        }

        Scene sceneTarget = ProjectContext.sceneMap.get(GrobalConfig.STARTSCENE);
        sceneTarget.getUserMap().put(user.getUsername(), user);

        user.setStatus(GrobalConfig.ALIVE);
        user.setPos(GrobalConfig.STARTSCENE);
        userService.recoverUser(user);
        userMapper.updateByPrimaryKeySelective(user);

        ProjectContext.channelStatus.put(channel, ChannelStatus.COMMONSCENE);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.LIVEINSTART));

    }
}
