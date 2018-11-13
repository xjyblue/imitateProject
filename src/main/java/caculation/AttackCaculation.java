package caculation;

import component.Equipment;
import memory.NettyMemory;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Weaponequipmentbar;

import java.math.BigInteger;

@Component("attackCaculation")
public class AttackCaculation {

    public BigInteger caculate(User user, BigInteger attackDamage) {
        if (user.getWeaponequipmentbars() != null) {
            for (Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()) {
                Equipment equipment = NettyMemory.equipmentMap.get(weaponequipmentbar.getWid());
                if (weaponequipmentbar.getDurability() > 0) {
                    attackDamage = attackDamage.add(new BigInteger(equipment.getAddValue() + ""));
                    weaponequipmentbar.setDurability(weaponequipmentbar.getDurability() - 1);
                }
            }
        }
        return attackDamage;
    }
}
