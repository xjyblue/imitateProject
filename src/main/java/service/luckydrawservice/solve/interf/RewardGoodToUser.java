package service.luckydrawservice.solve.interf;

import pojo.User;
import service.luckydrawservice.entity.LuckDrawGoodItem;

/**
 * @ClassName RewardGoodToUser
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/2/16 15:51
 * @Version 1.0
 **/
public interface RewardGoodToUser {

    public static final String METHOD_NAME = "rewardGoodToUser";

    public void rewardGoodToUser(User user, LuckDrawGoodItem luckDrawGoodItem);
}
