package service.luckydrawservice.solve.impl;

import config.impl.excel.CollectGoodResourceLoad;
import core.annotation.good.GoodGet;
import core.annotation.good.GoodRegion;
import core.component.good.CollectGood;
import core.component.good.parent.BaseGood;
import mapper.UserbagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userbag;
import service.luckydrawservice.entity.LuckDrawGoodItem;
import service.luckydrawservice.solve.interf.RewardGoodToUser;
import service.userbagservice.service.UserbagService;

import java.util.UUID;

/**
 * @ClassName RewardCollectionGoodToUser
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/2/16 17:45
 * @Version 1.0
 **/
@Component
@GoodRegion
public class RewardCollectionGoodToUser implements RewardGoodToUser {

    @Autowired
    private UserbagMapper userbagMapper;

    @Override
    @GoodGet(type = BaseGood.CHANGEGOOD)
    public void rewardGoodToUser(User user, LuckDrawGoodItem luckDrawGoodItem) {
        boolean flag = false;
        Userbag userbagT = null;
        for (Userbag userbag : user.getUserBag()) {
            if (userbag.getWid().equals(luckDrawGoodItem.getWid())) {
                flag = true;
                userbagT = userbag;
            }
        }
        if (flag) {
            userbagT.setNum(userbagT.getNum() + luckDrawGoodItem.getNum());
        } else {
            userbagT = new Userbag();
            userbagT.setId(UUID.randomUUID().toString());
            userbagT.setNum(luckDrawGoodItem.getNum());
            userbagT.setWid(luckDrawGoodItem.getWid());
            userbagT.setTypeof(luckDrawGoodItem.getType());
            userbagT.setName(user.getUsername());
            user.getUserBag().add(userbagT);
//          数据库
            userbagMapper.insertSelective(userbagT);
        }
    }

}
