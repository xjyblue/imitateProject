package event;


import context.ProjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;

import java.io.IOException;

/**
 * 具体事件分发器
 *
 * @author server
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
    @Autowired
    private TransactionEvent transactionEvent;
    @Autowired
    private LabourUnionEvent labourUnionEvent;
    @Autowired
    private FriendEvent friendEvent;
    public void distributeEvent(Channel ch, String msg) throws IOException {
        String status = ProjectContext.eventStatus.get(ch);
        switch (status) {
            case EventStatus.COMING:
                connectEvent.connect(ch,msg);
                break;
            case EventStatus.LOGIN:
                loginEvent.login(ch, msg);
                break;
            case EventStatus.REGISTER:
                registerEvent.register(ch, msg);
                break;
            case EventStatus.STOPAREA:
                stopAreaEvent.stopArea(ch, msg);
                break;
            case EventStatus.SKILLMANAGER:
                skillEvent.skill(ch, msg);
                break;
            case EventStatus.ATTACK:
                attackEvent.attack(ch, msg);
                break;
            case EventStatus.BOSSAREA:
                bossEvent.attack(ch, msg);
                break;
            case EventStatus.SHOPAREA:
                shopEvent.shop(ch, msg);
                break;
            case EventStatus.TRADE:
                transactionEvent.tradeing(ch,msg);
                break;
            case EventStatus.DEADAREA:
                deadEvent.dead(ch,msg);
                break;
            case EventStatus.LABOURUNION:
                labourUnionEvent.solve(ch,msg);
                break;
            case EventStatus.FRIEND:
                friendEvent.solve(ch,msg);
                break;
        }
    }
}
