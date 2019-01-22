package service.registerservice.service;

import config.impl.excel.AchievementResourceLoad;
import config.impl.excel.LevelResourceLoad;
import config.impl.excel.RoleResourceLoad;
import core.annotation.Region;
import core.packet.ServerPacket;
import lombok.extern.slf4j.Slf4j;
import service.achievementservice.entity.Achievement;
import core.config.GrobalConfig;
import core.config.MessageConfig;
import core.channel.ChannelStatus;
import io.netty.channel.Channel;
import service.levelservice.entity.Level;
import mapper.AchievementprocessMapper;
import mapper.UserMapper;
import mapper.UserskillrelationMapper;
import core.annotation.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.Achievementprocess;
import pojo.User;
import pojo.Userskillrelation;
import service.skillservice.entity.UserSkill;
import service.levelservice.service.LevelService;
import service.skillservice.service.SkillService;
import utils.ChannelUtil;
import utils.MessageUtil;

import java.util.List;
import java.util.Map;

/**
 * @ClassName RegisterService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
@Region
@Slf4j
public class RegisterService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserskillrelationMapper userskillrelationMapper;
    @Autowired
    private AchievementprocessMapper achievementprocessMapper;
    @Autowired
    private LevelService levelService;
    @Autowired
    private SkillService skillService;

    /**
     * 注册
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "register", status = {ChannelStatus.REGISTER})
    public void register(Channel channel, String msg) {
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.FIVE) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERRORORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        if (!temp[GrobalConfig.TWO].equals(temp[GrobalConfig.THREE])) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.DOUBLEPASSWORDERROR);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        if (!RoleResourceLoad.roleMap.containsKey(Integer.parseInt(temp[GrobalConfig.FOUR]))) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NOROLE);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
//      设置用户基本信息
        User user = new User();
        user.setUsername(temp[1]);
        user.setPassword(temp[2]);
        user.setStatus(GrobalConfig.ALIVE);
        user.setPos(GrobalConfig.MINVALUE);
        user.setMoney("10000");
        user.setRoleid(Integer.parseInt(temp[4]));
//      设置用户1级血量和1级的经验值
        user.setExperience(1);
        Level level = LevelResourceLoad.levelMap.get(1);
        user.setMp(level.getMaxMp());
        user.setHp(level.getMaxHp());

        userMapper.insertSelective(user);


//      根据用户种族填充初始技能信息
        List<UserSkill> list = skillService.getUserSkillByUserRole(user.getRoleid());
//      键位初始值，我们统一在注册时初始键位，后期玩家自己去调整自己想要的键位
        int count = 1;
        for (UserSkill userSkill : list) {
            Userskillrelation userskillrelation = new Userskillrelation();
            userskillrelation.setSkillcds(System.currentTimeMillis());
            userskillrelation.setSkillid(userSkill.getSkillId());
            userskillrelation.setKeypos((count++) + "");
            userskillrelation.setUsername(user.getUsername());
            userskillrelationMapper.insert(userskillrelation);
        }


//      为用户新增任务进程
        for (Map.Entry<Integer, Achievement> entry : AchievementResourceLoad.achievementMap.entrySet()) {
            Achievementprocess achievementprocess = new Achievementprocess();
            achievementprocess.setIffinish(false);
            achievementprocess.setType(entry.getValue().getType());
            achievementprocess.setAchievementid(entry.getValue().getAchievementId());
            achievementprocess.setUsername(user.getUsername());

            if (entry.getValue().getType().equals(Achievement.UPLEVEL)) {
                achievementprocess.setProcesss(levelService.getLevelByExperience(user.getExperience()) + "");
                if (Integer.parseInt(achievementprocess.getProcesss()) >= Integer.parseInt(entry.getValue().getTarget())) {
                    achievementprocess.setIffinish(true);
                }
            } else {
                achievementprocess.setProcesss(entry.getValue().getBegin());
            }
//              存数据库
            achievementprocessMapper.insert(achievementprocess);
        }

        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(MessageConfig.REGISTERSUCCESS);
        MessageUtil.sendMessage(channel,builder.build());
        ChannelUtil.channelStatus.put(channel, ChannelStatus.LOGIN);
    }
}
