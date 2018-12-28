package caculation;

import buff.Buff;
import component.Equipment;
import config.BuffConfig;
import context.ProjectContext;
import io.netty.channel.Channel;
import level.Level;
import org.springframework.stereotype.Component;
import packet.PacketType;
import pojo.User;
import pojo.Weaponequipmentbar;
import skill.MonsterSkill;
import utils.LevelUtil;
import utils.MessageUtil;

import java.math.BigInteger;

@Component("attackCaculation")
public class AttackCaculation {


    //  人物攻击加成
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

//      等级加成处理
        Level level = ProjectContext.levelMap.get(LevelUtil.getLevelByExperience(user.getExperience()));
        BigInteger levelUp = new BigInteger(level.getUpAttack() + "");
        attackDamage = attackDamage.add(levelUp);

        return attackDamage;
    }


    //  怪物攻击减伤
    public static BigInteger dealDefenseBuff(MonsterSkill monsterSkill, User user, User target) {
        if (user.getBuffMap().get(BuffConfig.DEFENSEBUFF) != 3000 && user == target) {
            Buff buff = ProjectContext.buffMap.get(user.getBuffMap().get(BuffConfig.DEFENSEBUFF));
            BigInteger mosterSkillDamage = new BigInteger(monsterSkill.getDamage());
            BigInteger buffDefenceDamage = new BigInteger(buff.getInjurySecondValue());
            mosterSkillDamage = mosterSkillDamage.subtract(buffDefenceDamage);
            Channel channelTemp = ProjectContext.userToChannelMap.get(user);
            channelTemp.writeAndFlush(MessageUtil.turnToPacket("人物减伤buff减伤：" + buff.getInjurySecondValue() + "人物剩余血量：" + user.getHp(), PacketType.USERBUFMSG));
            return mosterSkillDamage;
        }
        return new BigInteger(monsterSkill.getDamage());

    }
}
