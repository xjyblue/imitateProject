package event;


import context.ProjectContext;
import context.ProjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
    public void distributeEvent(Channel ch, String msg) throws IOException, InvocationTargetException, IllegalAccessException {
        String status = ProjectContext.eventStatus.get(ch);
        switch (status) {
            case EventStatus.COMING:
                ProjectUtil.reflectAnnotation(connectEvent,ch,msg);
                break;
            case EventStatus.LOGIN:
                ProjectUtil.reflectAnnotation(loginEvent,ch,msg);
                break;
            case EventStatus.REGISTER:
                ProjectUtil.reflectAnnotation(registerEvent,ch,msg);
                break;
            case EventStatus.STOPAREA:
                ProjectUtil.reflectAnnotation(stopAreaEvent,ch,msg);
                break;
            case EventStatus.SKILLMANAGER:
                ProjectUtil.reflectAnnotation(skillEvent,ch,msg);
                break;
            case EventStatus.ATTACK:
                ProjectUtil.reflectAnnotation(attackEvent,ch,msg);
                break;
            case EventStatus.BOSSAREA:
                ProjectUtil.reflectAnnotation(bossEvent,ch,msg);
                break;
            case EventStatus.TRADE:
                ProjectUtil.reflectAnnotation(transactionEvent,ch,msg);
                break;
            case EventStatus.DEADAREA:
                ProjectUtil.reflectAnnotation(deadEvent,ch,msg);
                break;
            case EventStatus.LABOURUNION:
                ProjectUtil.reflectAnnotation(labourUnionEvent,ch,msg);
                break;
            case EventStatus.FRIEND:
                ProjectUtil.reflectAnnotation(friendEvent,ch,msg);
                break;
        }
    }

}
