package service.luckydrawservice.solve.impl;

import config.impl.excel.EquipmentResourceLoad;
import core.annotation.good.GoodGet;
import core.annotation.good.GoodRegion;
import core.component.good.Equipment;
import core.component.good.parent.BaseGood;
import mapper.UserbagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userbag;
import service.luckydrawservice.entity.LuckDrawGoodItem;
import service.luckydrawservice.solve.interf.RewardGoodToUser;

import java.util.UUID;

/**
 * @ClassName RewardEquipMentToUser
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/2/16 17:38
 * @Version 1.0
 **/
@Component
@GoodRegion
public class RewardEquipMentToUser implements RewardGoodToUser {

    @Autowired
    private UserbagMapper userbagMapper;

    @GoodGet(type = BaseGood.EQUIPMENT)
    @Override
    public void rewardGoodToUser(User user, LuckDrawGoodItem luckDrawGoodItem) {
        Equipment equipment = EquipmentResourceLoad.equipmentMap.get(luckDrawGoodItem.getWid());
        Userbag userbag = new Userbag();
        userbag.setName(user.getUsername());
        userbag.setStartlevel(equipment.getStartLevel());
        userbag.setTypeof(luckDrawGoodItem.getType());
        userbag.setNum(1);
        userbag.setId(UUID.randomUUID().toString());
        userbag.setWid(luckDrawGoodItem.getWid());
        userbag.setDurability(equipment.getDurability());
//      缓存
        user.getUserBag().add(userbag);
//      数据库
        userbagMapper.insertSelective(userbag);
    }
}
