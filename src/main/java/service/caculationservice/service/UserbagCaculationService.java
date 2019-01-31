package service.caculationservice.service;

import config.impl.excel.AchievementResourceLoad;
import config.impl.excel.EquipmentResourceLoad;
import service.achievementservice.entity.Achievement;
import service.achievementservice.entity.AchievementConfig;
import service.achievementservice.service.AchievementService;
import core.component.good.Equipment;
import core.component.good.parent.BaseGood;
import io.netty.channel.Channel;
import mapper.UserbagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.Achievementprocess;
import pojo.User;
import pojo.Userbag;
import service.userbagservice.service.UserbagService;
import utils.ChannelUtil;


/**
 * @ClassName UserbagCaculationService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
public class UserbagCaculationService {
    @Autowired
    private UserbagMapper userbagMapper;
    @Autowired
    private AchievementService achievementService;
    @Autowired
    private UserbagService userbagService;

    /**
     * 新增用户背包道具,在线
     *
     * @param user
     * @param value
     */
    public void addUserBagForUser(User user, Userbag value) {
        value.setName(user.getUsername());
        if (value.getTypeof().equals(BaseGood.EQUIPMENT)) {
            user.getUserBag().add(value);
            if (userbagMapper.selectByPrimaryKey(value.getId()) != null) {
                userbagMapper.updateByPrimaryKeySelective(value);
            } else {
                userbagMapper.insertSelective(value);
            }
        } else if (value.getTypeof().equals(BaseGood.MPMEDICINE) || value.getTypeof().equals(BaseGood.HPMEDICINE) || value.getTypeof().equals(BaseGood.CHANGEGOOD)) {
            boolean flag = true;
            for (Userbag userbag : user.getUserBag()) {
                if (userbag.getWid().equals(value.getWid())) {
                    userbag.setNum(userbag.getNum() + value.getNum());
                    userbagMapper.updateByPrimaryKeySelective(userbag);
                    flag = false;
                    break;
                }
            }
            if (flag) {
                user.getUserBag().add(value);
                if (userbagMapper.selectByPrimaryKey(value.getId()) != null) {
                    userbagMapper.updateByPrimaryKeySelective(value);
                } else {
                    userbagMapper.insertSelective(value);
                }
            }
        }

//      触发成就
        if (value.getTypeof().equals(BaseGood.EQUIPMENT)) {
            Equipment equipment = EquipmentResourceLoad.equipmentMap.get(value.getWid());
            for (Achievementprocess achievementprocess : user.getAchievementprocesses()) {
                Achievement achievement = AchievementResourceLoad.achievementMap.get(achievementprocess.getAchievementid());
                if (achievementprocess.getType().equals(AchievementConfig.COLLECT)) {
                    achievementService.executeCollect(achievementprocess, equipment, user, achievement);
                }
            }
        }
        Channel channel = ChannelUtil.userToChannelMap.get(user);
        userbagService.refreshUserbagInfo(channel, null);
    }

    /**
     * 移除用户背包道具
     *
     * @param user
     * @param userbag
     * @param num
     */
    public void removeUserbagFromUser(User user, Userbag userbag, Integer num) {
        if (num.equals(userbag.getNum())) {
            user.getUserBag().remove(userbag);
            userbagMapper.updateByPrimaryKey(userbag);
        } else {
            userbag.setNum(userbag.getNum() - num);
            userbagMapper.updateByPrimaryKey(userbag);
        }
        Channel channel = ChannelUtil.userToChannelMap.get(user);
        userbagService.refreshUserbagInfo(channel, null);
    }

}
