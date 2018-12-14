package event;

import achievement.Achievement;
import config.BuffConfig;
import config.MessageConfig;
import io.netty.channel.Channel;
import mapper.AchievementprocessMapper;
import mapper.UserMapper;
import mapper.UserskillrelationMapper;
import memory.NettyMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import packet.PacketType;
import pojo.*;
import role.Role;
import skill.UserSkill;
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
                    UserSkill userSkill = NettyMemory.SkillMap.get(userskillrelation.getSkillid());
                    userskillrelationMap.put(userskillrelation.getKeypos(), userskillrelation);
                    skillLook += "[键位-" + userskillrelation.getKeypos() + "-技能名称-" + userSkill.getSkillName() + "-技能伤害-" + userSkill.getDamage() + "技能cd" + userSkill.getAttackCd() + "] ";
                }
                NettyMemory.userskillrelationMap.put(channel, userskillrelationMap);

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

                if (!NettyMemory.userBuffEndTime.containsKey(user)) {
                    NettyMemory.userBuffEndTime.put(user, mapSecond);
                }
//                初始化玩家的技能end

                NettyMemory.session2UserIds.put(channel, user);
                NettyMemory.userToChannelMap.put(user, channel);

                initUserProcess(channel);

                channel.writeAndFlush(MessageUtil.turnToPacket("登录成功，你已进入" + NettyMemory.areaMap.get(user.getPos()).getName()));
                Role role = NettyMemory.roleMap.get(user.getRoleid());
                channel.writeAndFlush(MessageUtil.turnToPacket("   " + user.getUsername() + "    职业为:" + role.getName() + "] " + skillLook, PacketType.USERINFO));
                NettyMemory.eventStatus.put(channel, EventStatus.STOPAREA);

            }
        }
    }

    private void replaceUserChannel(String username) {
        try{
            lock.lock();
            for (Map.Entry<Channel, User> entry : NettyMemory.session2UserIds.entrySet()) {
                Channel channel = entry.getKey();
                if (entry.getValue().getUsername().equals(username)) {
//              进行顶号处理,之前的channel先挂掉
                    channel.writeAndFlush(MessageUtil.turnToPacket("不好意思,有人在别处登录你的游戏号，请选择重新登录或者修改密码"));
                    channel.close();
//              清除该channel和user的所有信息

                }
            }
        }finally {
            lock.unlock();
        }
    }

    private void initUserProcess(Channel channel) {
        //初始化玩家任务进度,如果玩家还没有进度
        //todo：后面改成注册弄入
        User user = NettyMemory.session2UserIds.get(channel);
        if (user.getAchievementprocesses().size() == 0) {
            for (Map.Entry<Integer, Achievement> entry : NettyMemory.achievementMap.entrySet()) {
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
