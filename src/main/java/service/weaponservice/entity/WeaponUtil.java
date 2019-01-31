package service.weaponservice.entity;

import com.sun.org.apache.regexp.internal.RE;
import core.config.GrobalConfig;
import pojo.Userbag;

/**
 * @ClassName WeaponUtil
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/30 15:18
 * @Version 1.0
 **/
public class WeaponUtil {

    public static final Integer WEAPON_TWO = 2;
    public static final Integer WEAPON_ONE = 1;

    /**
     * 根据背包格子的武器拿到武器对应的装备栏位置
     *
     * @param userbag
     * @return
     */
    public static Integer getWeaponPos(Userbag userbag) {
        if (userbag.getWid() >= GrobalConfig.EQUIPMENT_WEAPON_START && userbag.getWid() < GrobalConfig.EQUIPMENT_WEAPON_END) {
            return WEAPON_ONE;
        }
        if (userbag.getWid() >= GrobalConfig.HAT_WEAPON_START && userbag.getWid() < GrobalConfig.HAT_WEAPON_END) {
            return WEAPON_TWO;
        }
        return 0;

    }
}
