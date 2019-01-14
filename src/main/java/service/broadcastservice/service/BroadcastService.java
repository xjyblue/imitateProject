package service.broadcastservice.service;

import core.channel.ChannelStatus;
import core.context.ProjectContext;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;
import pojo.User;
import service.teamservice.entity.Team;
import utils.MessageUtil;

import java.util.Map;

/**
 * @ClassName BroadcastService
 * @Description 广播服务
 * @Author xiaojianyu
 * @Date 2019/1/7 12:11
 * @Version 1.0
 **/
@Component
public class BroadcastService {

    public void sendMessageToAll(String msg, String teamId, String type) {
        Team team = ProjectContext.teamMap.get(teamId);
        for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
            User user = entry.getValue();
            Channel channel = ProjectContext.userToChannelMap.get(user);
            String userStatus = ProjectContext.channelStatus.get(channel);
            if (userStatus.equals(ChannelStatus.BOSSSCENE) || userStatus.equals(ChannelStatus.DEADSCENE) || userStatus.equals(ChannelStatus.ATTACK)) {
                Channel channelTemp = ProjectContext.userToChannelMap.get(entry.getValue());
                if (type == null) {
                    channelTemp.writeAndFlush(MessageUtil.turnToPacket(msg));
                } else {
                    channelTemp.writeAndFlush(MessageUtil.turnToPacket(msg, type));
                }
            }
        }
    }
}
