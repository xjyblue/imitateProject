package core;


import core.context.ProjectContext;
import service.labourunionservice.service.LabourUnionService;
import service.sceneservice.service.CommonSceneService;
import service.teamservice.service.TeamService;
import utils.ReflectMethodUtil;
import service.attackservice.service.AttackService;
import service.bossservice.service.BossService;
import service.connectservice.service.ConnectService;
import service.deadservice.service.DeadService;
import service.friendservice.service.FriendService;
import service.loginservice.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import service.registerservice.service.RegisterService;
import service.skillservice.service.SkillService;
import service.transactionservice.service.TransactionService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @ClassName ServiceDistributor
 * @Description 服务分发器
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/

@Component
public class ServiceDistributor {
    @Autowired
    private LoginService loginService;
    @Autowired
    private RegisterService registerService;
    @Autowired
    private CommonSceneService commonSceneService;
    @Autowired
    private SkillService skillService;
    @Autowired
    private AttackService attackService;
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
    @Autowired
    private TeamService teamService;
    public void distributeEvent(Channel ch, String msg) throws IOException, InvocationTargetException, IllegalAccessException {
        String status = ProjectContext.eventStatus.get(ch);
        switch (status) {
            case ChannelStatus.COMING:
                ReflectMethodUtil.reflectAnnotation(connectService,ch,msg);
                break;
            case ChannelStatus.LOGIN:
                ReflectMethodUtil.reflectAnnotation(loginService,ch,msg);
                break;
            case ChannelStatus.REGISTER:
                ReflectMethodUtil.reflectAnnotation(registerService,ch,msg);
                break;
            case ChannelStatus.COMMONSCENE:
                ReflectMethodUtil.reflectAnnotation(commonSceneService,ch,msg);
                break;
            case ChannelStatus.SKILLVIEW:
                ReflectMethodUtil.reflectAnnotation(skillService,ch,msg);
                break;
            case ChannelStatus.ATTACK:
                ReflectMethodUtil.reflectAnnotation(attackService,ch,msg);
                break;
            case ChannelStatus.BOSSSCENE:
                ReflectMethodUtil.reflectAnnotation(bossService,ch,msg);
                break;
            case ChannelStatus.TRADE:
                ReflectMethodUtil.reflectAnnotation(transactionService,ch,msg);
                break;
            case ChannelStatus.DEADSCENE:
                ReflectMethodUtil.reflectAnnotation(deadService,ch,msg);
                break;
            case ChannelStatus.LABOURUNION:
                ReflectMethodUtil.reflectAnnotation(labourUnionService,ch,msg);
                break;
            case ChannelStatus.FRIEND:
                ReflectMethodUtil.reflectAnnotation(friendService,ch,msg);
                break;
            case ChannelStatus.TEAM:
                ReflectMethodUtil.reflectAnnotation(teamService,ch,msg);
                break;
             default:

        }
    }

}
