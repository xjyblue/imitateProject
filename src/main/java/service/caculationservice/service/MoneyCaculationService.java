package service.caculationservice.service;

import service.achievementservice.service.AchievementService;
import core.config.MessageConfig;
import core.context.ProjectContext;
import io.netty.channel.Channel;
import mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import utils.MessageUtil;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/12/17 11:13
 */
@Component
public class MoneyCaculationService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AchievementService achievementService;

    public void addMoneyToUser(User user, String money) {
        int usermoney = Integer.parseInt(user.getMoney());
        int addmoney = Integer.parseInt(money);
        usermoney += addmoney;
        user.setMoney(String.valueOf(usermoney));
//      处理用户成就
        achievementService.executeMoneyAchievement(user);
//      同步到数据库
        userMapper.updateByPrimaryKeySelective(user);
    }

    public void removeMoneyToUser(User user, String money) {
        int usermoney = Integer.parseInt(user.getMoney());
        int removemoney = Integer.parseInt(money);
        if (removemoney > usermoney) {
            Channel channelT = ProjectContext.userToChannelMap.get(user);
            channelT.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOENOUGHMONEYTOGIVE));
            return;
        }
        usermoney -= removemoney;
        user.setMoney(String.valueOf(usermoney));
//      同步到数据库
        userMapper.updateByPrimaryKeySelective(user);
    }
}
