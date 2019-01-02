package event;


import context.ProjectContext;
import utils.ReflectMethodUtil;
import service.attackservice.service.AttackService;
import service.bossservice.service.BossService;
import service.connectservice.service.ConnectService;
import service.deadservice.service.DeadService;
import service.friendservice.service.FriendService;
import service.labourUnionservice.service.LabourUnionService;
import service.loginservice.service.loginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import service.registerservice.service.RegisterService;
import service.shopservice.service.ShopService;
import service.skillservice.service.SkillService;
import service.transactionservice.service.TransactionService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * 具体事件分发器
 * @author server
 */
@Component("eventDistributor")
public class EventDistributor {
    @Autowired
    private loginService loginService;
    @Autowired
    private RegisterService registerService;
    @Autowired
    private StopAreaEvent stopAreaEvent;
    @Autowired
    private SkillService skillService;
    @Autowired
    private AttackService attackEvent;
    @Autowired
    private BossService bossService;
    @Autowired
    private ConnectService connectService;
    @Autowired
    private DeadService deadService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private LabourUnionService labourUnionService;
    @Autowired
    private FriendService friendService;
    public void distributeEvent(Channel ch, String msg) throws IOException, InvocationTargetException, IllegalAccessException {
        String status = ProjectContext.eventStatus.get(ch);
        switch (status) {
            case EventStatus.COMING:
                ReflectMethodUtil.reflectAnnotation(connectService,ch,msg);
                break;
            case EventStatus.LOGIN:
                ReflectMethodUtil.reflectAnnotation(loginService,ch,msg);
                break;
            case EventStatus.REGISTER:
                ReflectMethodUtil.reflectAnnotation(registerService,ch,msg);
                break;
            case EventStatus.STOPAREA:
                ReflectMethodUtil.reflectAnnotation(stopAreaEvent,ch,msg);
                break;
            case EventStatus.SKILLMANAGER:
                ReflectMethodUtil.reflectAnnotation(skillService,ch,msg);
                break;
            case EventStatus.ATTACK:
                ReflectMethodUtil.reflectAnnotation(attackEvent,ch,msg);
                break;
            case EventStatus.BOSSAREA:
                ReflectMethodUtil.reflectAnnotation(bossService,ch,msg);
                break;
            case EventStatus.TRADE:
                ReflectMethodUtil.reflectAnnotation(transactionService,ch,msg);
                break;
            case EventStatus.DEADAREA:
                ReflectMethodUtil.reflectAnnotation(deadService,ch,msg);
                break;
            case EventStatus.LABOURUNION:
                ReflectMethodUtil.reflectAnnotation(labourUnionService,ch,msg);
                break;
            case EventStatus.FRIEND:
                ReflectMethodUtil.reflectAnnotation(friendService,ch,msg);
                break;
        }
    }

}
