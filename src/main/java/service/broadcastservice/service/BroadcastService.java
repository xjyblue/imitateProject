package service.broadcastservice.service;

import com.google.protobuf.GeneratedMessageV3;
import core.channel.ChannelStatus;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;
import pojo.User;
import service.teamservice.entity.Team;
import service.teamservice.entity.TeamCache;
import utils.ChannelUtil;
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

    public void sendMessageToAll(String teamId, GeneratedMessageV3 serverPacket) {
        Team team = TeamCache.teamMap.get(teamId);
        for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
            User user = entry.getValue();
            Channel channel = ChannelUtil.userToChannelMap.get(user);
            String userStatus = ChannelUtil.channelStatus.get(channel);
            if (userStatus.equals(ChannelStatus.BOSSSCENE) || userStatus.equals(ChannelStatus.DEADSCENE) || userStatus.equals(ChannelStatus.ATTACK)) {
                Channel channelTemp = ChannelUtil.userToChannelMap.get(entry.getValue());
                MessageUtil.sendMessage(channelTemp,serverPacket);
            }
        }
    }
}
