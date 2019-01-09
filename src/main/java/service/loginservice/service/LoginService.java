package service.loginservice.service;

import core.config.GrobalConfig;
import service.sceneservice.entity.Scene;
import service.buffservice.entity.BuffConstant;
import core.config.MessageConfig;
import core.ServiceDistributor;
import core.ChannelStatus;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import mapper.UserMapper;
import mapper.UserskillrelationMapper;
import core.context.ProjectContext;
import core.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import core.packet.PacketType;
import pojo.*;
import core.component.role.Role;
import service.skillservice.entity.UserSkill;
import service.buffservice.service.UserBuffService;
import service.achievementservice.util.AchievementUtil;
import utils.MessageUtil;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName LoginService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
@Slf4j
public class LoginService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserskillrelationMapper userskillrelationMapper;
    @Autowired
    private ServiceDistributor serviceDistributor;
    @Autowired
    private UserBuffService userBuffService;

    private Lock lock = new ReentrantLock();

    /**
     * 登录逻辑
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "*")
    public void login(Channel channel, String msg) {
        String[] temp = msg.split("-");
        log.info("用户登录开始----------账户{},用户密码{}", temp[0], temp[1]);
        if (temp.length != GrobalConfig.TWO) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        User user = userMapper.getUser(temp[0], temp[1]);
        if (user == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORPASSWORD));
            return;
        }

//      登录逻辑
        try {
            lock.lock();
//          解决玩家顶号问题
            if (replaceUserChannel(user.getUsername(), channel)) {
                return;
            }
            Role role = ProjectContext.roleMap.get(user.getRoleid());
//          初始化玩家的技能
            initUserSkill(user, channel, role);
//          初始化玩家buff
            initUserBuffBegin(user);
            user.setIfOccupy(false);
//          将玩家放入场景队列中
            Scene scene = ProjectContext.sceneMap.get(user.getPos());
            scene.getUserMap().put(user.getUsername(), user);


            ProjectContext.channelToUserMap.put(channel, user);
            ProjectContext.userToChannelMap.put(user, channel);
//          展示成就信息
            AchievementUtil.refreshAchievementInfo(user);

            channel.writeAndFlush(MessageUtil.turnToPacket("登录成功"));
            ProjectContext.channelStatus.put(channel, ChannelStatus.COMMONSCENE);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 初始化人物技能
     *
     * @param user
     * @param channel
     * @param role
     */
    private void initUserSkill(User user, Channel channel, Role role) {
        UserskillrelationExample userskillrelationExample = new UserskillrelationExample();
        UserskillrelationExample.Criteria criteria = userskillrelationExample.createCriteria();
        criteria.andUsernameEqualTo(user.getUsername());
        List<Userskillrelation> userskillrelations = userskillrelationMapper.selectByExample(userskillrelationExample);
        Map<String, Userskillrelation> userskillrelationMap = new HashMap<>(64);
        String skillLook = "";
        for (Userskillrelation userskillrelation : userskillrelations) {
            UserSkill userSkill = ProjectContext.skillMap.get(userskillrelation.getSkillid());
            userskillrelationMap.put(userskillrelation.getKeypos(), userskillrelation);
            skillLook += "[键位-" + userskillrelation.getKeypos() + "-技能名称-" + userSkill.getSkillName() + "-技能伤害-" + userSkill.getDamage() + "技能cd" + userSkill.getAttackCd() + "] ";
        }
        ProjectContext.userskillrelationMap.put(user, userskillrelationMap);
        channel.writeAndFlush(MessageUtil.turnToPacket("   " + user.getUsername() + "    职业为:" + role.getName() + "] " + skillLook, PacketType.USERINFO));
    }

    /**
     * 初始人物buff
     *
     * @param user
     */
    private void initUserBuffBegin(User user) {
        //          初始化玩家的各种buffer
        Map<String, Integer> map = new HashMap<>(64);
        map.put(BuffConstant.MPBUFF, 1000);
        map.put(BuffConstant.POISONINGBUFF, 2000);
        map.put(BuffConstant.DEFENSEBUFF, 3000);
        map.put(BuffConstant.SLEEPBUFF, 5000);
        map.put(BuffConstant.TREATMENTBUFF, 6000);
        map.put(BuffConstant.ALLPERSON, 4000);
        map.put(BuffConstant.BABYBUF, 7000);
        user.setBuffMap(map);
//          初始化每个用户buff的终止时间
        Map<String, Long> mapSecond = new HashMap<>(64);
        mapSecond.put(BuffConstant.MPBUFF, 1000L);
        mapSecond.put(BuffConstant.POISONINGBUFF, 2000L);
        mapSecond.put(BuffConstant.DEFENSEBUFF, 3000L);
        mapSecond.put(BuffConstant.SLEEPBUFF, 1000L);
        mapSecond.put(BuffConstant.TREATMENTBUFF, 1000L);
        mapSecond.put(BuffConstant.ALLPERSON, 1000L);
        mapSecond.put(BuffConstant.BABYBUF, 1000L);
        ProjectContext.userBuffEndTime.put(user, mapSecond);

//          这里注入事件处理器是为了让玩家自己心跳去消费命令，执行任务
        user.setServiceDistributor(serviceDistributor);
//          注入buff处理器，让用户去刷新自己的buff
        user.setUserBuffService(userBuffService);
        user.setBuffRefreshTime(0L);
        user.setIfOccupy(true);
    }

    /**
     * 顶号处理
     *
     * @param username
     * @param channel
     * @return
     */
    private boolean replaceUserChannel(String username, Channel channel) {
        try {
            lock.lock();
            Channel channelTarget = null;
//          找到之前的渠道，把之前渠道的信息全部转移到新的渠道
            for (Map.Entry<Channel, User> entry : ProjectContext.channelToUserMap.entrySet()) {
                if (entry.getValue().getUsername().equals(username)) {
                    channelTarget = entry.getKey();
                }
            }

//          进行顶号处理,挂掉旧的渠道
            if (channelTarget != null) {
//              切换渠道处理渠道消息
                User user = ProjectContext.channelToUserMap.get(channelTarget);
                user.setIfOccupy(true);
                ProjectContext.channelToUserMap.put(channel, user);
                ProjectContext.userToChannelMap.put(user, channel);
                ProjectContext.channelStatus.put(channel, ProjectContext.channelStatus.get(channelTarget));
                channel.writeAndFlush(MessageUtil.turnToPacket("登录成功"));
                channelTarget.writeAndFlush(MessageUtil.turnToPacket("不好意思,有人在别处登录你的游戏号，请选择重新登录或者修改密码", PacketType.CHANGECHANNEL));
                channelTarget.close();
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }


}
