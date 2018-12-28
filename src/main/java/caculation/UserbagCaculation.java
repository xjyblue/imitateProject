package caculation;

import achievement.Achievement;
import achievement.AchievementExecutor;
import component.Equipment;
import component.parent.Good;
import io.netty.channel.Channel;
import mapper.UserbagMapper;
import context.ProjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.Achievementprocess;
import pojo.User;
import pojo.Userbag;
import utils.UserbagUtil;



/**
 * Description ：nettySpringServer
 * Created by server on 2018/12/13 12:11
 */
@Component
public class UserbagCaculation {
    @Autowired
    private UserbagMapper userbagMapper;
    @Autowired
    private AchievementExecutor achievementExecutor;

    public void addUserBagForUser(User user, Userbag value) {
        value.setName(user.getUsername());
        if (value.getTypeof().equals(Good.EQUIPMENT)) {
            user.getUserBag().add(value);
            if (userbagMapper.selectByPrimaryKey(value.getId()) != null) {
                userbagMapper.updateByPrimaryKeySelective(value);
            } else {
                userbagMapper.insertSelective(value);
            }
        } else if (value.getTypeof().equals(Good.MPMEDICINE)||value.getTypeof().equals(Good.HPMEDICINE)||value.getTypeof().equals(Good.CHANGEGOOD)) {
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
        if(value.getTypeof().equals(Good.EQUIPMENT)){
            Equipment equipment = ProjectContext.equipmentMap.get(value.getWid());
            for (Achievementprocess achievementprocess : user.getAchievementprocesses()) {
                Achievement achievement = ProjectContext.achievementMap.get(achievementprocess.getAchievementid());
                if (achievementprocess.getType().equals(Achievement.COLLECT)) {
                    achievementExecutor.executeCollect(achievementprocess, equipment, user, achievement);
                }
            }
        }
        Channel channel = ProjectContext.userToChannelMap.get(user);
        UserbagUtil.refreshUserbagInfo(channel);
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
        UserbagUtil.refreshUserbagInfo(channel);
    }

}
