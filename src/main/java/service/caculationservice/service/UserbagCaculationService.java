package service.caculationservice.service;

import service.achievementservice.entity.Achievement;
import service.achievementservice.service.AchievementService;
import core.component.good.Equipment;
import core.component.good.parent.PGood;
import io.netty.channel.Channel;
import mapper.UserbagMapper;
import core.context.ProjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.Achievementprocess;
import pojo.User;
import pojo.Userbag;
import service.userbagservice.service.UserbagService;


/**
 * Description ：nettySpringServer
 * Created by server on 2018/12/13 12:11
 */
@Component
public class UserbagCaculationService {
    @Autowired
    private UserbagMapper userbagMapper;
    @Autowired
    private AchievementService achievementService;
    @Autowired
    private UserbagService userbagService;

    public void addUserBagForUser(User user, Userbag value) {
        value.setName(user.getUsername());
        if (value.getTypeof().equals(PGood.EQUIPMENT)) {
            user.getUserBag().add(value);
            if (userbagMapper.selectByPrimaryKey(value.getId()) != null) {
                userbagMapper.updateByPrimaryKeySelective(value);
            } else {
                userbagMapper.insertSelective(value);
            }
        } else if (value.getTypeof().equals(PGood.MPMEDICINE)||value.getTypeof().equals(PGood.HPMEDICINE)||value.getTypeof().equals(PGood.CHANGEGOOD)) {
            boolean flag = true;
            for (Userbag userbag : user.getUserBag()) {
                if (userbag.getWid().equals(value.getWid())) {
                    userbag.setNum(userbag.getNum() + value.getNum());
                    userbagMapper.updateByPrimaryKeySelective(userbag);
                    flag = false;
                    break;
                }
            }
            if(flag){
                user.getUserBag().add(value);
                if (userbagMapper.selectByPrimaryKey(value.getId()) != null) {
                    userbagMapper.updateByPrimaryKeySelective(value);
                } else {
                    userbagMapper.insertSelective(value);
                }
            }
        }

//      触发成就
        if(value.getTypeof().equals(PGood.EQUIPMENT)){
            Equipment equipment = ProjectContext.equipmentMap.get(value.getWid());
            for (Achievementprocess achievementprocess : user.getAchievementprocesses()) {
                Achievement achievement = ProjectContext.achievementMap.get(achievementprocess.getAchievementid());
                if (achievementprocess.getType().equals(Achievement.COLLECT)) {
                    achievementService.executeCollect(achievementprocess, equipment, user, achievement);
                }
            }
        }
        Channel channel = ProjectContext.userToChannelMap.get(user);
        userbagService.refreshUserbagInfo(channel,null);
    }

    public void removeUserbagFromUser(User user, Userbag userbag, Integer num) {
        if (num == userbag.getNum()) {
            user.getUserBag().remove(userbag);
            //todo:
            userbag.setName(null);
            userbagMapper.updateByPrimaryKey(userbag);
        } else {
            userbag.setNum(userbag.getNum() - num);
            userbagMapper.updateByPrimaryKey(userbag);
        }
        Channel channel = ProjectContext.userToChannelMap.get(user);
        userbagService.refreshUserbagInfo(channel,null);
    }

}
