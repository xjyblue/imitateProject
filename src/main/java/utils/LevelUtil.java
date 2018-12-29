package utils;

import achievement.Achievement;
import achievement.AchievementExecutor;
import component.Equipment;
import io.netty.channel.Channel;
import level.Level;
import context.ProjectContext;
import mapper.UserMapper;
import pojo.Achievementprocess;
import pojo.User;
import pojo.Weaponequipmentbar;

import java.util.Map;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/12/5 14:26
 */
public class LevelUtil {

    //  根据经验拿到等级
    public static int getLevelByExperience(int experience) {
        for (Map.Entry<Integer, Level> entry : ProjectContext.levelMap.entrySet()) {
            if (entry.getValue().getExperienceDown() <= experience && entry.getValue().getExperienceUp() >= experience) {
                return entry.getKey();
            }
        }
        return 11;
    }

    //  根据角色拿到血量上限
    public static String getMaxHp(User user) {
        Level level = ProjectContext.levelMap.get(getLevelByExperience(user.getExperience()));
//      这里可以附加装备属性
        int addValue = 0;
//      职业因子计算
        addValue += Integer.parseInt(level.getMaxHp());
        double factor = caculateFactorByRole(level, user);
        addValue *= factor;
//      装备加成
        for (Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()) {
            Equipment equipment = ProjectContext.equipmentMap.get(weaponequipmentbar.getWid());
            if (equipment.getLifeValue() != 0) {
                addValue += equipment.getLifeValue();
            }
        }
        return addValue + "";
    }

    //  根据经验值提升人物等级
    public static void upUserLevel(User user, String value) {
        int oleL = getLevelByExperience(user.getExperience());
        int newE = user.getExperience() + Integer.parseInt(value);
        user.setExperience(newE);
        if (getLevelByExperience(user.getExperience()) > oleL) {
//            提示人物升级信息
            Channel channelTarget = ProjectContext.userToChannelMap.get(user);
            channelTarget.writeAndFlush(MessageUtil.turnToPacket(">>>>>>>>>>>>>>恭喜您升到了万众瞩目的" + getLevelByExperience(user.getExperience()) + "级<<<<<<<<<<<<<<<"));
        } else {
            Channel channelTarget = ProjectContext.userToChannelMap.get(user);
            channelTarget.writeAndFlush(MessageUtil.turnToPacket("您当前经验值增长了" + value + ",你目前的经验值为：" + user.getExperience()));
        }
        UserMapper userMapper = SpringContextUtil.getBean("userMapper");
//      升级就刷新人物的hp和mp
        user.setHp(getMaxHp(user));
        user.setMp(getMaxMp(user));

//      升级触发成就
        AchievementExecutor achievementExecutor = SpringContextUtil.getBean("achievementExecutor");
        for (Achievementprocess achievementprocess : user.getAchievementprocesses()) {
            if (!achievementprocess.getIffinish() && achievementprocess.getType().equals(Achievement.UPLEVEL)) {
                Achievement achievement = ProjectContext.achievementMap.get(achievementprocess.getAchievementid());
                achievementExecutor.executeLevelUp(achievementprocess, user, achievement);
            }
        }
        userMapper.updateByPrimaryKeySelective(user);
    }


    public static String getMaxMp(User user) {
        Level level = ProjectContext.levelMap.get(getLevelByExperience(user.getExperience()));
        int addValue = 0;
//      职业因子计算
        addValue += Integer.parseInt(level.getMaxMp());
        double factor = caculateFactorByRole(level, user);
        addValue *= factor;
//      这里可以附加装备属性
        return addValue + "";
    }

    //  职业因子计算
    public static Double caculateFactorByRole(Level level, User user) {
        String levelCaculation = level.getCaculatePercent();
        String temps[] = levelCaculation.split("-");
        for (String temp : temps) {
            String tt[] = temp.split(":");
            if (tt[0].equals(user.getRoleid() + "")) {
                return Double.parseDouble(tt[1]);
            }
        }
        return 1.0;
    }
}
