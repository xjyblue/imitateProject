package caculation;

import achievement.AchievementExecutor;
import mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;

import java.math.BigInteger;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/12/17 11:13
 */
@Component
public class MoneyCaculation {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AchievementExecutor achievementExecutor;

    public void addMoneyToUser(User user, String money) {
        BigInteger usermoney = new BigInteger(user.getMoney());
        BigInteger addmoney = new BigInteger(money);
        usermoney = usermoney.add(addmoney);
        user.setMoney(usermoney.toString());
//      处理用户成就
        achievementExecutor.executeMoneyAchievement(user);
//      同步到数据库
        userMapper.updateByPrimaryKeySelective(user);
    }

    public void removeMoneyToUser(User user, String money) {

    }
}
