package event;

import config.MessageConfig;
import io.netty.channel.Channel;
import level.Level;
import mapper.UserMapper;
import mapper.UserskillrelationMapper;
import memory.NettyMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userskillrelation;
import skill.UserSkill;
import utils.UserSkillUtil;
import utils.MessageUtil;

import java.util.List;

@Component("registerEvent")
public class RegisterEvent {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserskillrelationMapper userskillrelationMapper;
    public void register(Channel channel, String msg) {
        String[] temp = msg.split("-");
        if (temp.length != 4) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        if (!temp[1].equals(temp[2])) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.DOUBLEPASSWORDERROR));
            return;
        }
        if (!NettyMemory.roleMap.containsKey(Integer.parseInt(temp[3]))) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOROLE));
            return;
        }
//      设置用户基本信息
        User user = new User();
        user.setUsername(temp[0]);
        user.setPassword(temp[1]);
        user.setStatus("1");
        user.setPos("0");
        user.setMoney("10000");
        user.setRoleid(Integer.parseInt(temp[3]));
//      设置用户1级血量和1级的经验值
        user.setExperience(1);
        Level level = NettyMemory.levelMap.get(1);
        user.setMp(level.getMaxMp());
        user.setHp(level.getMaxHp());
        userMapper.insertSelective(user);

//      根据用户种族填充初始技能信息
        List<UserSkill> list = UserSkillUtil.getUserSkillByUserRole(user.getRoleid());
//      键位初始值，我们统一在注册时初始键位，后期玩家自己去调整自己想要的键位
        int count = 1;
        for(UserSkill userSkill : list){
            Userskillrelation userskillrelation = new Userskillrelation();
            userskillrelation.setSkillcds(System.currentTimeMillis());
            userskillrelation.setSkillid(userSkill.getSkillId());
            userskillrelation.setKeypos((count++)+"");
            userskillrelation.setUsername(user.getUsername());
            userskillrelationMapper.insert(userskillrelation);
        }

        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.REGISTERSUCCESS));
        NettyMemory.eventStatus.put(channel, EventStatus.LOGIN);


    }
}
