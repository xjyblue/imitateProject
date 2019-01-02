package service.caculationservice.service;

import service.buffservice.entity.Buff;
import component.good.Equipment;
import service.buffservice.entity.BuffConstant;
import context.ProjectContext;
import io.netty.channel.Channel;
import service.levelservice.entity.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import packet.PacketType;
import pojo.User;
import pojo.Weaponequipmentbar;
import component.monster.MonsterSkill;
import service.levelservice.service.LevelService;
import utils.MessageUtil;

import java.math.BigInteger;

@Component
public class AttackCaculationService {
    @Autowired
    private LevelService levelService;

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
        Level level = ProjectContext.levelMap.get(levelService.getLevelByExperience(user.getExperience()));
        BigInteger levelUp = new BigInteger(level.getUpAttack() + "");
        attackDamage = attackDamage.add(levelUp);

        return attackDamage;
    }


    //  怪物攻击减伤
    public static BigInteger dealDefenseBuff(MonsterSkill monsterSkill, User user, User target) {
        if (user.getBuffMap().get(BuffConstant.DEFENSEBUFF) != 3000 && user == target) {
            Buff buff = ProjectContext.buffMap.get(user.getBuffMap().get(BuffConstant.DEFENSEBUFF));
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
