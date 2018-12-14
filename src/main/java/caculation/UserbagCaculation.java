package caculation;

import achievement.Achievement;
import achievement.AchievementExecutor;
import component.Equipment;
import component.parent.Good;
import io.netty.channel.Channel;
import mapper.UserbagMapper;
import memory.NettyMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.Achievementprocess;
import pojo.User;
import pojo.Userbag;
import utils.UserbagUtil;



/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/12/13 12:11
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
        } else if (value.getTypeof().equals(Good.MPMEDICINE)) {
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

        if(value.getTypeof().equals(Good.EQUIPMENT)){
            Equipment equipment = NettyMemory.equipmentMap.get(value.getWid());
            for (Achievementprocess achievementprocess : user.getAchievementprocesses()) {
                Achievement achievement = NettyMemory.achievementMap.get(achievementprocess.getAchievementid());
                if (achievementprocess.getType().equals(Achievement.COLLECT)) {
                    achievementExecutor.executeCollect(achievementprocess, equipment, user, achievement);
                }
            }
        }
        Channel channel = NettyMemory.userToChannelMap.get(user);
        UserbagUtil.refreshUserbagInfo(channel);
    }

    public void removeUserbagFromUser(User user, Userbag userbag, Integer num) {
        if (num == userbag.getNum()) {
            user.getUserBag().remove(userbag);
            userbag.setName(null);
            userbagMapper.updateByPrimaryKey(userbag);
        } else {
            userbag.setNum(userbag.getNum() - num);
            userbagMapper.updateByPrimaryKey(userbag);
        }
        Channel channel = NettyMemory.userToChannelMap.get(user);
        UserbagUtil.refreshUserbagInfo(channel);
    }

}
