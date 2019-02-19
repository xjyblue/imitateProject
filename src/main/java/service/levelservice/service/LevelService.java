package service.levelservice.service;

import config.impl.excel.AchievementResourceLoad;
import config.impl.excel.EquipmentResourceLoad;
import config.impl.excel.LevelResourceLoad;
import core.packet.ServerPacket;
import service.achievementservice.entity.Achievement;
import service.achievementservice.entity.AchievementConfig;
import service.achievementservice.service.AchievementService;
import core.component.good.Equipment;
import io.netty.channel.Channel;
import service.levelservice.entity.Level;
import mapper.UserMapper;
import org.springframework.stereotype.Component;
import pojo.Achievementprocess;
import pojo.User;
import pojo.Weaponequipmentbar;
import utils.ChannelUtil;
import utils.MessageUtil;
import utils.SpringContextUtil;

import java.util.Map;

/**
 * @ClassName LevelService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
public class LevelService {
    /**
     * 根据经验拿到等级
     *
     * @param experience
     * @return
     */
    public int getLevelByExperience(int experience) {
        for (Map.Entry<Integer, Level> entry : LevelResourceLoad.levelMap.entrySet()) {
            if (entry.getValue().getExperienceDown() <= experience && entry.getValue().getExperienceUp() >= experience) {
                return entry.getKey();
            }
        }
        return 11;
    }

    /**
     * 根据角色拿到血量上限
     *
     * @param user
     * @return
     */
    public String getMaxHp(User user) {
        Level level = LevelResourceLoad.levelMap.get(getLevelByExperience(user.getExperience()));
//      这里可以附加装备属性
        int addValue = 0;
//      职业因子计算
        addValue += Integer.parseInt(level.getMaxHp());
        double factor = caculateFactorByRole(level, user);
        addValue *= factor;
//      装备加成
        for (Map.Entry<Integer, Weaponequipmentbar> entry : user.getWeaponequipmentbarMap().entrySet()) {
            Weaponequipmentbar weaponequipmentbar = entry.getValue();
            Equipment equipment = EquipmentResourceLoad.equipmentMap.get(weaponequipmentbar.getWid());
            if (equipment.getLifeValue() != 0) {
                addValue += equipment.getLifeValue();
            }
        }
        return addValue + "";
    }

    /**
     * 根据经验值提升人物等级，触发成就
     *
     * @param user
     * @param value
     */
    public void upUserLevel(User user, String value) {
        int oleL = getLevelByExperience(user.getExperience());
        int newE = user.getExperience() + Integer.parseInt(value);
        user.setExperience(newE);
        if (getLevelByExperience(user.getExperience()) > oleL) {
//            提示人物升级信息
            Channel channelTarget = ChannelUtil.userToChannelMap.get(user);
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(">>>>>>>>>>>>>>恭喜您升到了万众瞩目的" + getLevelByExperience(user.getExperience()) + "级<<<<<<<<<<<<<<<");
            MessageUtil.sendMessage(channelTarget, builder.build());
        } else {
            Channel channelTarget = ChannelUtil.userToChannelMap.get(user);
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData("您当前经验值增长了" + value + ",你目前的经验值为：" + user.getExperience());
            MessageUtil.sendMessage(channelTarget, builder.build());
        }
        UserMapper userMapper = SpringContextUtil.getBean("userMapper");
//      升级就刷新人物的hp和mp
        user.setHp(getMaxHp(user));
        user.setMp(getMaxMp(user));

//      升级触发成就
        AchievementService achievementService = SpringContextUtil.getBean("achievementService");
        for (Achievementprocess achievementprocess : user.getAchievementprocesses()) {
            if (achievementprocess.getIffinish().equals(AchievementConfig.DOING_TASK) && achievementprocess.getType().equals(AchievementConfig.UPLEVEL)) {
                Achievement achievement = AchievementResourceLoad.achievementMap.get(achievementprocess.getAchievementid());
                achievementService.executeLevelUp(achievementprocess, user, achievement);
            }
        }
        userMapper.updateByPrimaryKeySelective(user);
    }

    /**
     * 人物mp最大值
     *
     * @param user
     * @return
     */
    public String getMaxMp(User user) {
        Level level = LevelResourceLoad.levelMap.get(getLevelByExperience(user.getExperience()));
        int addValue = 0;
//      职业因子计算
        addValue += Integer.parseInt(level.getMaxMp());
        double factor = caculateFactorByRole(level, user);
        addValue *= factor;
//      这里可以附加装备属性，修改装备属性的附加值可以提升装备伤害
        return addValue + "";
    }

    /**
     * 职业因子获取，可以进行更复杂的运算
     *
     * @param level
     * @param user
     * @return
     */
    public Double caculateFactorByRole(Level level, User user) {
        String levelCaculation = level.getCaculatePercent();
        String[] temps = levelCaculation.split("-");
        for (String temp : temps) {
            String[] tt = temp.split(":");
            if (tt[0].equals(user.getRoleid() + "")) {
                return Double.parseDouble(tt[1]);
            }
        }
        return 1.0;
    }
}
