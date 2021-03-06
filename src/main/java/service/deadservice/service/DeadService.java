package service.deadservice.service;

import config.impl.excel.SceneResourceLoad;
import core.annotation.order.OrderRegion;
import core.config.OrderConfig;
import core.packet.ServerPacket;
import service.sceneservice.entity.Scene;
import core.config.GrobalConfig;
import core.config.MessageConfig;
import core.channel.ChannelStatus;
import io.netty.channel.Channel;
import mapper.UserMapper;
import core.annotation.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import utils.ChannelUtil;
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
@OrderRegion
public class DeadService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;


    /**
     * 复活
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.REBORN_ORDER, status = {ChannelStatus.DEADSCENE})
    public void reborn(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        if (!user.getPos().equals(GrobalConfig.STARTSCENE)) {
            Scene scene = SceneResourceLoad.sceneMap.get(user.getPos());
//          这句是为了解决普通场景复活和怪物副本复活的bug
            if (scene.getUserMap().containsKey(user.getUsername())) {
                scene.getUserMap().remove(user.getUsername());
            }
        }

        Scene sceneTarget = SceneResourceLoad.sceneMap.get(GrobalConfig.STARTSCENE);
        sceneTarget.getUserMap().put(user.getUsername(), user);

        user.setStatus(GrobalConfig.ALIVE);
        user.setPos(GrobalConfig.STARTSCENE);
        userService.recoverUser(user);
        userMapper.updateByPrimaryKeySelective(user);

        ChannelUtil.channelStatus.put(channel, ChannelStatus.COMMONSCENE);
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(MessageConfig.LIVE_IN_START);
        MessageUtil.sendMessage(channel, builder.build());

    }
}
