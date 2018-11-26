package event;

import component.BossArea;
import config.MessageConfig;
import io.netty.channel.Channel;
import memory.NettyMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import utils.MessageUtil;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/11/26 12:11
 */
@Component("deadEvent")
public class DeadEvent {
    @Autowired
    private ChatEvent chatEvent;

    public void dead(Channel channel, String msg) {
        if(msg.startsWith("chat")){
            chatEvent.chat(channel,msg);
            return;
        }
        if(msg.equals("y")){
            User user = NettyMemory.session2UserIds.get(channel);
            user.setStatus("1");
            user.setHp("10000");
            user.setPos("0");
            NettyMemory.eventStatus.put(channel,EventStatus.STOPAREA);
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.LIVEINSTART));
            if(user.getTeamId()!=null&&NettyMemory.bossAreaMap.containsKey(user.getTeamId())){
                BossArea bossArea = NettyMemory.bossAreaMap.get(user.getTeamId());
                bossArea.getDamageAll().put(user,"0");
            }
            return;
        }
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.DEADNOOPERATE));
    }

    private boolean bossAreaContaisUser(Channel channel) {
       return NettyMemory.bossAreaMap.containsKey(NettyMemory.session2UserIds.get(channel).getTeamId());
    }
}
