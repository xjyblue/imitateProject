package service.caculationservice.service;

import config.impl.excel.BuffResourceLoad;
import config.impl.excel.EquipmentResourceLoad;
import config.impl.excel.LevelResourceLoad;
import core.config.GrobalConfig;
import core.packet.ServerPacket;
import service.buffservice.entity.Buff;
import core.component.good.Equipment;
import service.buffservice.entity.BuffConstant;
import io.netty.channel.Channel;
import service.levelservice.entity.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Weaponequipmentbar;
import core.component.monster.MonsterSkill;
import service.levelservice.service.LevelService;
import utils.ChannelUtil;
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
    public Integer caculate(User user, String attackDamageValue) {
        Integer attackDamage = Integer.parseInt(attackDamageValue);

//      等级加成处理
        Level level = LevelResourceLoad.levelMap.get(levelService.getLevelByExperience(user.getExperience()));
        attackDamage += level.getUpAttack();

//      单一装备加成处理
        if (user.getWeaponequipmentbars() != null) {
            for (Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()) {
                Equipment equipment = EquipmentResourceLoad.equipmentMap.get(weaponequipmentbar.getWid());
//              武器加成
                if (weaponequipmentbar.getDurability() > 0) {
//                  武器星级加成
                    Integer equipAttackValue = equipment.getAddValue();
                    if (!weaponequipmentbar.getStartlevel().equals(0)) {
                        equipAttackValue *= weaponequipmentbar.getStartlevel();
                    }
                    attackDamage += equipAttackValue;
                    weaponequipmentbar.setDurability(weaponequipmentbar.getDurability() - 1);
                }
            }
        }
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
    public Integer dealDefenseBuff(MonsterSkill monsterSkill, User user, User target) {
        Integer monsterSkillDamage = Integer.parseInt(monsterSkill.getDamage());
        if (user.getBuffMap().get(BuffConstant.DEFENSEBUFF) != GrobalConfig.DEFENSEBUFF_DEFAULTVALUE && user == target) {
            Buff buff = BuffResourceLoad.buffMap.get(user.getBuffMap().get(BuffConstant.DEFENSEBUFF));
            Integer buffDefenceDamage = Integer.parseInt(buff.getInjurySecondValue());
            monsterSkillDamage -= buffDefenceDamage;
            Channel channelTemp = ChannelUtil.userToChannelMap.get(user);
            ServerPacket.UserbufResp.Builder builder = ServerPacket.UserbufResp.newBuilder();
            builder.setData("人物减伤buff减伤：" + buff.getInjurySecondValue() + "人物剩余血量：" + user.getHp());
            MessageUtil.sendMessage(channelTemp, builder.build());
            return monsterSkillDamage;
        }
        return monsterSkillDamage;

    }
}
