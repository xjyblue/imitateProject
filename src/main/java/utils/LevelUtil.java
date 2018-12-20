package utils;

import component.Equipment;
import level.Level;
import context.ProjectContext;
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
        return 7;
    }

    //  根据角色拿到血量上限
    public static String getMaxHp(User user) {
        Level level = ProjectContext.levelMap.get(getLevelByExperience(user.getExperience()));
//      这里可以附加装备属性
        int addValue = 0;
        for (Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()) {
            Equipment equipment = ProjectContext.equipmentMap.get(weaponequipmentbar.getWid());
            if (equipment.getLifeValue() != 0) {
                addValue += equipment.getLifeValue();
            }
        }
        addValue += Integer.parseInt(level.getMaxHp());
        return addValue + "";
    }
}
