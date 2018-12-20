package caculation;

import component.Equipment;
import context.ProjectContext;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Weaponequipmentbar;
import java.math.BigInteger;

@Component("attackCaculation")
public class AttackCaculation {

    public BigInteger caculate(User user, BigInteger attackDamage) {

//      单一装备加成处理
        if (user.getWeaponequipmentbars() != null) {
            for (Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()) {
                Equipment equipment = ProjectContext.equipmentMap.get(weaponequipmentbar.getWid());
                if (weaponequipmentbar.getDurability() > 0) {
                    attackDamage = attackDamage.add(new BigInteger(equipment.getAddValue() + ""));
                    weaponequipmentbar.setDurability(weaponequipmentbar.getDurability() - 1);
                }
            }
        }
        return attackDamage;
    }
}
