package service.caculationservice.service;

import core.packet.ServerPacket;
import service.achievementservice.service.AchievementService;
import core.config.MessageConfig;
import io.netty.channel.Channel;
import mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import utils.ChannelUtil;
import utils.MessageUtil;

/**
 * @ClassName MoneyCaculationService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
public class MoneyCaculationService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AchievementService achievementService;

    /**
     * 加钱
     *
     * @param user
     * @param money
     */
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

    /**
     * 扣钱
     *
     * @param user
     * @param money
     */
    public boolean removeMoneyToUser(User user, String money) {
        int usermoney = Integer.parseInt(user.getMoney());
        int removemoney = Integer.parseInt(money);
        if (removemoney > usermoney) {
            Channel channelT = ChannelUtil.userToChannelMap.get(user);
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NO_ENOUGH_MONEY_TO_GIVE);
            MessageUtil.sendMessage(channelT, builder.build());
            return false;
        }
        usermoney -= removemoney;
        user.setMoney(String.valueOf(usermoney));
//      同步到数据库
        userMapper.updateByPrimaryKeySelective(user);
        return true;
    }

    /**
     * 检查用户是否拥有足够的金钱
     *
     * @param user
     * @param money
     * @return
     */
    public boolean checkUserHasEnoughMoney(User user, String money) {
        Integer userMoney = Integer.parseInt(user.getMoney());
        Integer moneyB = Integer.parseInt(money);
        if (userMoney >= moneyB) {
            return true;
        } else {
            return false;
        }
    }
}
