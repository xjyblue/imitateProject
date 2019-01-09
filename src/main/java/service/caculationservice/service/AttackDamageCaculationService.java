package service.caculationservice.service;

import core.config.GrobalConfig;
import service.buffservice.entity.Buff;
import core.component.good.Equipment;
import service.buffservice.entity.BuffConstant;
import core.context.ProjectContext;
import io.netty.channel.Channel;
import service.levelservice.entity.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import core.packet.PacketType;
import pojo.User;
import pojo.Weaponequipmentbar;
import core.component.monster.MonsterSkill;
import service.levelservice.service.LevelService;
import utils.MessageUtil;

import java.math.BigInteger;
/**
 * @ClassName AttackCaculationService
 * @Description 计算伤害 后期可以改成责任链模式
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
public class AttackDamageCaculationService {
    @Autowired
    private LevelService levelService;

    /**
     * 人物攻击加成
     *
     * @param user
     * @param
     * @return
     */
    public BigInteger caculate(User user, String attackDamageValue) {
        BigInteger attackDamage = new BigInteger(attackDamageValue);
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


    /**
     * 怪物攻击减伤
     *
     * @param monsterSkill
     * @param user
     * @param target
     * @return
     */
    public BigInteger dealDefenseBuff(MonsterSkill monsterSkill, User user, User target) {
        if (user.getBuffMap().get(BuffConstant.DEFENSEBUFF) != GrobalConfig.DEFENSEBUFF_DEFAULTVALUE && user == target) {
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
