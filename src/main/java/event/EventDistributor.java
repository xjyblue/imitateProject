package event;


import config.MessageConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import memory.NettyMemory;
import utils.MessageUtil;

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
    @Autowired
    private ConnectEvent connectEvent;
    @Autowired
    private DeadEvent deadEvent;
    public void distributeEvent(ChannelHandlerContext ctx, String msg) {
        Channel ch = ctx.channel();
        String status = NettyMemory.eventStatus.get(ch);
        switch (status) {
            case EventStatus.COMING:
                connectEvent.connect(ctx.channel(),msg);
                break;
            case EventStatus.LOGIN:
                loginEvent.login(ctx.channel(), msg);
                break;
            case EventStatus.REGISTER:
                registerEvent.register(ctx.channel(), msg);
                break;
            case EventStatus.STOPAREA:
                stopAreaEvent.stopArea(ctx.channel(), msg);
                break;
            case EventStatus.SKILLMANAGER:
                skillEvent.skill(ctx.channel(), msg);
                break;
            case EventStatus.ATTACK:
                attackEvent.attack(ctx.channel(), msg);
                break;
            case EventStatus.BOSSAREA:
                bossEvent.attack(ctx.channel(), msg);
                break;
            case EventStatus.SHOPAREA:
                shopEvent.shop(ctx.channel(), msg);
                break;
            case EventStatus.DEADAREA:
                deadEvent.dead(ctx.channel(),msg);
                break;
        }
    }
}
