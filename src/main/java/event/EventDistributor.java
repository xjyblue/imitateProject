package event;


import config.MessageConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import memory.NettyMemory;
import utils.DelimiterUtils;

/**
 * 具体事件分发器
 *
 * @author xiaojianyu
 */
@Component("eventDistributor")
public class EventDistributor {
    @Autowired
    private LoginEvent loginEvent;
    @Autowired
    private RegisterEvent registerEvent;
    @Autowired
    private StopAreaEvent stopAreaEvent;
    @Autowired
    private SkillEvent skillEvent;
    @Autowired
    private AttackEvent attackEvent;
    @Autowired
    private BossEvent bossEvent;
    @Autowired
    private ShopEvent shopEvent;
    public void distributeEvent(ChannelHandlerContext ctx, String msg) {
        Channel ch = ctx.channel();
        if (!NettyMemory.eventStatus.containsKey(ch)) {
            if (msg.equals("d")) {
                NettyMemory.eventStatus.put(ch, EventStatus.LOGIN);
                ctx.writeAndFlush(DelimiterUtils.addDelimiter(MessageConfig.LOGINMESSAGE));
            }
            if (msg.equals("z")) {
                NettyMemory.eventStatus.put(ch, EventStatus.REGISTER);
                ctx.writeAndFlush(DelimiterUtils.addDelimiter(MessageConfig.REGISTERMESSAGE));
            }
        } else {
            String status = NettyMemory.eventStatus.get(ch);
            switch (status) {
                case EventStatus.LOGIN:
                    loginEvent.login(ctx.channel(),msg);
                    break;
                case EventStatus.REGISTER:
                    registerEvent.register(ctx.channel(),msg);
                    break;
                case EventStatus.STOPAREA:
                    stopAreaEvent.stopArea(ctx.channel(),msg);
                    break;
                case EventStatus.SKILLMANAGER:
                    skillEvent.skill(ctx.channel(),msg);
                    break;
                case EventStatus.ATTACK:
                    attackEvent.attack(ctx.channel(),msg);
                    break;
                case EventStatus.BOSSAREA:
                    bossEvent.attack(ctx.channel(),msg);
                    break;
                case EventStatus.SHOPAREA:
                    shopEvent.shop(ctx.channel(),msg);
                    break;
            }
        }
    }
}
