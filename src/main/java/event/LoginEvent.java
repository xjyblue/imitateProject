package event;

import achievement.Achievement;
import component.Scene;
import config.BuffConfig;
import config.MessageConfig;
import io.netty.channel.Channel;
import mapper.AchievementprocessMapper;
import mapper.UserMapper;
import mapper.UserskillrelationMapper;
import context.ProjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import packet.PacketType;
import pojo.*;
import role.Role;
import skill.UserSkill;
import buff.BuffTask;
import utils.AchievementUtil;
import utils.LevelUtil;
import utils.MessageUtil;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component("loginEvent")
public class LoginEvent {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserskillrelationMapper userskillrelationMapper;
    @Autowired
    private AchievementprocessMapper achievementprocessMapper;
    @Autowired
    private EventDistributor eventDistributor;
    @Autowired
    private BuffTask buffTask;

    private Lock lock = new ReentrantLock();

    public void login(Channel channel, String msg) {
        String temp[] = msg.split("-");
        if (temp.length != 2) {
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
//             初始化玩家的技能start
            UserskillrelationExample userskillrelationExample = new UserskillrelationExample();
            UserskillrelationExample.Criteria criteria = userskillrelationExample.createCriteria();
            criteria.andUsernameEqualTo(user.getUsername());
            List<Userskillrelation> userskillrelations = userskillrelationMapper.selectByExample(userskillrelationExample);
            Map<String, Userskillrelation> userskillrelationMap = new HashMap<>();
            String skillLook = "";
            for (Userskillrelation userskillrelation : userskillrelations) {
                UserSkill userSkill = ProjectContext.skillMap.get(userskillrelation.getSkillid());
                userskillrelationMap.put(userskillrelation.getKeypos(), userskillrelation);
                skillLook += "[键位-" + userskillrelation.getKeypos() + "-技能名称-" + userSkill.getSkillName() + "-技能伤害-" + userSkill.getDamage() + "技能cd" + userSkill.getAttackCd() + "] ";
            }

            ProjectContext.userskillrelationMap.put(user, userskillrelationMap);

//              初始化玩家的各种buffer
            Map<String, Integer> map = new HashMap<>();
            map.put(BuffConfig.MPBUFF, 1000);
            map.put(BuffConfig.POISONINGBUFF, 2000);
            map.put(BuffConfig.DEFENSEBUFF, 3000);
            map.put(BuffConfig.SLEEPBUFF, 5000);
            map.put(BuffConfig.TREATMENTBUFF, 6000);
            map.put(BuffConfig.ALLPERSON, 4000);
            map.put(BuffConfig.BABYBUF, 7000);
            user.setBuffMap(map);
//              初始化每个用户buff的终止时间
            Map<String, Long> mapSecond = new HashMap<>();
            mapSecond.put(BuffConfig.MPBUFF, 1000l);
            mapSecond.put(BuffConfig.POISONINGBUFF, 2000l);
            mapSecond.put(BuffConfig.DEFENSEBUFF, 3000l);
            mapSecond.put(BuffConfig.SLEEPBUFF, 1000l);
            mapSecond.put(BuffConfig.TREATMENTBUFF, 1000l);
            mapSecond.put(BuffConfig.ALLPERSON, 1000l);
            mapSecond.put(BuffConfig.BABYBUF, 1000l);

            if (!ProjectContext.userBuffEndTime.containsKey(user)) {
                ProjectContext.userBuffEndTime.put(user, mapSecond);
            }

//          这里注入事件处理器是为了让玩家自己心跳去消费命令，执行任务
            user.setEventDistributor(eventDistributor);
//          注入buff处理器，让用户去刷新自己的buff
            user.setBuffTask(buffTask);
            user.setBuffRefreshTime(0l);
            user.setIfOnline(true);

//          将玩家放入场景队列中
            Scene scene = ProjectContext.sceneMap.get(user.getPos());
            scene.getUserMap().put(user.getUsername(), user);


            ProjectContext.session2UserIds.put(channel, user);
            ProjectContext.userToChannelMap.put(user, channel);
//          展示成就信息
            AchievementUtil.refreshAchievementInfo(user);

            channel.writeAndFlush(MessageUtil.turnToPacket("登录成功，你已进入" + ProjectContext.sceneMap.get(user.getPos()).getName()));
            Role role = ProjectContext.roleMap.get(user.getRoleid());
            channel.writeAndFlush(MessageUtil.turnToPacket("   " + user.getUsername() + "    职业为:" + role.getName() + "] " + skillLook, PacketType.USERINFO));
            ProjectContext.eventStatus.put(channel, EventStatus.STOPAREA);

        } finally {
            lock.unlock();
        }
    }

    private boolean replaceUserChannel(String username, Channel channel) {
        try {
            lock.lock();
            Channel channelTarget = null;
//          找到之前的渠道，把之前渠道的信息全部转移到新的渠道
            for (Map.Entry<Channel, User> entry : ProjectContext.session2UserIds.entrySet()) {
                if (entry.getValue().getUsername().equals(username)) {
                    channelTarget = entry.getKey();
                }
            }

//          进行顶号处理,挂掉旧的渠道
            if (channelTarget != null) {
//              切换渠道处理渠道消息
                User user = ProjectContext.session2UserIds.get(channelTarget);
                ProjectContext.session2UserIds.put(channel, user);
                ProjectContext.userToChannelMap.put(user, channel);
                ProjectContext.eventStatus.put(channel, ProjectContext.eventStatus.get(channelTarget));

//              顶号标记
                user.setOccupied(true);
                channelTarget.writeAndFlush(MessageUtil.turnToPacket("不好意思,有人在别处登录你的游戏号，请选择重新登录或者修改密码"));
                channelTarget.close();
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }


}
