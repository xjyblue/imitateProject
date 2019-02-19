package service.luckydrawservice.solve.impl;

import com.google.j2objc.annotations.AutoreleasePool;
import core.annotation.good.GoodGet;
import core.annotation.good.GoodRegion;
import core.component.good.parent.BaseGood;
import mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import service.luckydrawservice.entity.LuckDrawGoodItem;
import service.luckydrawservice.solve.interf.RewardGoodToUser;

/**
 * @ClassName RewardMoneyToUser
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/2/16 15:51
 * @Version 1.0
 **/
@Component
@GoodRegion
public class RewardMoneyToUser implements RewardGoodToUser {
    @Autowired
    private UserMapper userMapper;

    @Override
    @GoodGet(type = BaseGood.MONEY)
    public void rewardGoodToUser(User user, LuckDrawGoodItem luckDrawGoodItem) {
        user.setMoney((luckDrawGoodItem.getNum()+Integer.parseInt(user.getMoney())) + "");
        userMapper.updateByPrimaryKeySelective(user);
    }

}
