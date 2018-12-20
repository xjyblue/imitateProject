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
        } else {
            User user = userMapper.getUser(temp[0], temp[1]);
            if (user == null) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORPASSWORD));
            } else {
//             解决玩家顶号问题

//             根据用户id拿出user对象从会话中
                replaceUserChannel(user.getUsername());


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
                ProjectContext.userskillrelationMap.put(channel, userskillrelationMap);

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
//              初始化玩家的技能end

//              这里注入事件处理器是为了让玩家自己心跳去消费命令，执行任务
                user.setEventDistributor(eventDistributor);
//              注入buff处理器，让用户去刷新自己的buff
                user.setBuffTask(buffTask);
                user.setBuffRefreshTime(0l);
                user.setIfOnline(true);

//              将玩家放入场景队列中
                Scene scene = ProjectContext.sceneMap.get(user.getPos());
                scene.getUserMap().put(user.getUsername(),user);


                ProjectContext.session2UserIds.put(channel, user);
                ProjectContext.userToChannelMap.put(user, channel);

                initUserProcess(channel);

                channel.writeAndFlush(MessageUtil.turnToPacket("登录成功，你已进入" + ProjectContext.sceneMap.get(user.getPos()).getName()));
                Role role = ProjectContext.roleMap.get(user.getRoleid());
                channel.writeAndFlush(MessageUtil.turnToPacket("   " + user.getUsername() + "    职业为:" + role.getName() + "] " + skillLook, PacketType.USERINFO));
                ProjectContext.eventStatus.put(channel, EventStatus.STOPAREA);

            }
        }
    }

    private void replaceUserChannel(String username) {
        try{
            lock.lock();
            for (Map.Entry<Channel, User> entry : ProjectContext.session2UserIds.entrySet()) {
                Channel channel = entry.getKey();
                if (entry.getValue().getUsername().equals(username)) {
//              进行顶号处理,之前的channel先挂掉
                    channel.writeAndFlush(MessageUtil.turnToPacket("不好意思,有人在别处登录你的游戏号，请选择重新登录或者修改密码"));
                    channel.close();
//              清除该channel和user的所有信息
                //todo: 处理。。。
                }
            }
        }finally {
            lock.unlock();
        }
    }

    private void initUserProcess(Channel channel) {
        //初始化玩家任务进度,如果玩家还没有进度
        //todo：后面改成注册弄入
        User user = ProjectContext.session2UserIds.get(channel);
        if (user.getAchievementprocesses().size() == 0) {
            for (Map.Entry<Integer, Achievement> entry : ProjectContext.achievementMap.entrySet()) {
                Achievementprocess achievementprocess = new Achievementprocess();
                achievementprocess.setIffinish(false);
                achievementprocess.setType(entry.getValue().getType());
                achievementprocess.setAchievementid(entry.getValue().getAchievementId());
                achievementprocess.setUsername(user.getUsername());

                if (entry.getValue().getType().equals(Achievement.UPLEVEL)) {
                    achievementprocess.setProcesss(LevelUtil.getLevelByExperience(user.getExperience()) + "");
                    if (Integer.parseInt(achievementprocess.getProcesss()) >= Integer.parseInt(entry.getValue().getTarget())) {
                        achievementprocess.setIffinish(true);
                    }
                } else {
                    achievementprocess.setProcesss(entry.getValue().getBegin());
                }
//              存数据库
                achievementprocessMapper.insert(achievementprocess);
            }
        }
        AchievementUtil.refreshAchievementInfo(user);
    }

}
