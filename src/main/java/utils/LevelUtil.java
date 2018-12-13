package utils;

import level.Level;
import memory.NettyMemory;
import pojo.User;

import java.util.Map;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/12/5 14:26
 */
public class LevelUtil {

    public static int getLevelByExperience(int experience) {
        for (Map.Entry<Integer, Level> entry : NettyMemory.levelMap.entrySet()) {
            if(entry.getValue().getExperienceDown()<=experience&&entry.getValue().getExperienceUp()>=experience){
                return entry.getKey();
            }
        }
        return 7;
    }

    public static String getMaxHp(User user){
        Level level = NettyMemory.levelMap.get(getLevelByExperience(user.getExperience()));
//      这里可以附加装备属性
        return level.getMaxHp();
    }
}
