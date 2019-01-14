package service.buffservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import service.buffservice.entity.Buff;
import com.google.common.collect.Lists;
import service.caculationservice.service.HpCaculationService;
import service.sceneservice.entity.BossScene;
import core.component.monster.Monster;
import service.buffservice.entity.BuffConstant;
import core.config.GrobalConfig;
import io.netty.channel.Channel;
import core.context.ProjectContext;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userskillrelation;
import service.skillservice.entity.UserSkill;
import utils.MessageUtil;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName AttackBuffService
 * @Description 处理战斗中技能buff所附带的效果
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
public class AttackBuffService {
    @Autowired
    private HpCaculationService hpCaculationService;
    /**
     * 特殊攻击，buff产生攻击效果，技能攻击不往下走
     */
    public static final int BUFF_ATTACK_FLAG = 1;
    /**
     * 普通攻击激活攻击buff效果，技能往下走
     */
    public static final int COMMON_ATTACK_FLAG = 0;

    /**
     * 填充攻击时技能产生的所有buff
     *
     * @param userskillrelation
     * @param userSkill
     * @param monster
     * @param user
     * @return
     */
    public int buffSolve(Userskillrelation userskillrelation, UserSkill userSkill, Monster monster, User user) {
        for (Map.Entry<String, Integer> entry : userSkill.getBuffMap().entrySet()) {
//          处理怪物中毒类型的buff,人物让怪物中毒
            if (entry.getKey().equals(BuffConstant.POISONINGBUFF)) {
                poisoningBuffSolve(monster, entry.getKey(), entry.getValue());
            }

//          人物对自己产生护盾
            if (entry.getKey().equals(BuffConstant.DEFENSEBUFF)) {
                defendBuffSolve(user, entry.getValue());
            }

//          人物集体治疗
            if (entry.getKey().equals(BuffConstant.TREATMENTBUFF)) {
                treatBuffSolve(user, entry.getValue());
            }

//          集体伤害处理
            if (entry.getKey().equals(BuffConstant.ALLPERSON)) {
                return allPersonBuffSolve(user, userskillrelation, userSkill);
            }
//          集体解除控制
            if (entry.getKey().equals(BuffConstant.RELIEVEBUFF)) {
                relieveBuffSolve(user);
            }
//          嘲讽
            if (entry.getKey().equals(BuffConstant.TAUNTBUFF)) {
                tauntBuffSolve(user, entry.getValue());
            }
        }

        return COMMON_ATTACK_FLAG;
    }

    /**
     * 处理嘲讽buff
     *
     * @param user
     */
    private void tauntBuffSolve(User user, Integer buffValue) {
        Buff buff = ProjectContext.buffMap.get(buffValue);
        user.getBuffMap().put(BuffConstant.TAUNTBUFF, buff.getBufferId());
        user.getUserBuffEndTimeMap().put(BuffConstant.TAUNTBUFF, System.currentTimeMillis() + buff.getKeepTime() * 1000);
    }

    /**
     * 人物使用集体伤害buff对怪物造成伤害
     *
     * @param user
     * @param userskillrelation
     * @param userSkill
     * @return
     */
    private int allPersonBuffSolve(User user, Userskillrelation userskillrelation, UserSkill userSkill) {
        Channel channelTemp = ProjectContext.userToChannelMap.get(user);
        userskillrelation.setSkillcds(userSkill.getAttackCd() + System.currentTimeMillis());
        if (user.getTeamId() != null && ProjectContext.bossAreaMap.containsKey(user.getTeamId())) {
            channelTemp.writeAndFlush(MessageUtil.turnToPacket("你使用了" + userSkill.getSkillName() + "对怪物造成了集体伤害" + userSkill.getDamage()));
            BossScene bossScene = ProjectContext.bossAreaMap.get(user.getTeamId());
            for (Map.Entry<String, Monster> monsterEntry : bossScene.getMonsters().get(bossScene.getSequence().get(0)).entrySet()) {
                hpCaculationService.subMonsterHp(monsterEntry.getValue(), userSkill.getDamage());
                BigInteger monsterLife = new BigInteger(monsterEntry.getValue().getValueOfLife());
                BigInteger minLife = new BigInteger(GrobalConfig.MINVALUE);
                if (monsterLife.compareTo(minLife) <= 0) {
                    monsterEntry.getValue().setValueOfLife(GrobalConfig.MINVALUE);
                    monsterEntry.getValue().setStatus(GrobalConfig.DEAD);
                }
            }
        } else {
            for (Monster monsterTemp : ProjectContext.sceneMap.get(user.getPos()).getMonsters()) {
                if (monsterTemp.getStatus().equals(GrobalConfig.ALIVE)) {
                    hpCaculationService.subMonsterHp(monsterTemp, userSkill.getDamage());
                }
            }
        }
        return BUFF_ATTACK_FLAG;
    }

    /**
     * 解控buff激活解控
     *
     * @param user
     */
    private void relieveBuffSolve(User user) {
        List<User> userTarget = null;
        if (user.getTeamId() != null && ProjectContext.bossAreaMap.containsKey(user.getTeamId())) {
            BossScene bossScene = ProjectContext.bossAreaMap.get(user.getTeamId());
            userTarget = Lists.newArrayList(bossScene.getUserMap().values());
        } else {
            userTarget = Lists.newArrayList();
            userTarget.add(user);
        }
        for (User userT : userTarget) {
            userT.getBuffMap().put(BuffConstant.SLEEPBUFF, 5000);
            Channel channelT = ProjectContext.userToChannelMap.get(user);
            channelT.writeAndFlush(MessageUtil.turnToPacket(user.getUsername() + "解除了怪物的眩晕效果"));
        }
    }

    /**
     * 人技能治疗buff激活
     *
     * @param user
     * @param buffValue
     */
    private void treatBuffSolve(User user, Integer buffValue) {
        Buff buff = ProjectContext.buffMap.get(buffValue);
        user.getBuffMap().put(BuffConstant.TREATMENTBUFF, buff.getBufferId());
        user.getUserBuffEndTimeMap().put(BuffConstant.TREATMENTBUFF, System.currentTimeMillis() + buff.getKeepTime() * 1000);
    }

    /**
     * 人攻击怪物防御buff激活
     *
     * @param user
     * @param buffValue
     */
    private void defendBuffSolve(User user, Integer buffValue) {
        Buff buff = ProjectContext.buffMap.get(buffValue);
        user.getBuffMap().put(BuffConstant.DEFENSEBUFF, buff.getBufferId());
        user.getUserBuffEndTimeMap().put(BuffConstant.DEFENSEBUFF, System.currentTimeMillis() + buff.getKeepTime() * 1000);
    }

    /**
     * 人攻击怪物中毒buff激活
     *
     * @param monster
     * @param buffName
     * @param buffValue
     */
    private void poisoningBuffSolve(Monster monster, String buffName, Integer buffValue) {
        Buff buff = ProjectContext.buffMap.get(buffValue);
        monster.getBufMap().put(buffName, buffValue);
        Map<String, Long> tempMap = new HashMap<>(64);
        monster.getMonsterBuffEndTimeMap().put(BuffConstant.POISONINGBUFF, System.currentTimeMillis() + buff.getKeepTime() * 1000);
    }

    /**
     * 怪物攻击人防御buff减伤
     *
     * @param monsterDamage
     * @param user
     * @return
     */
    public BigInteger monsterAttackDefendBuff(BigInteger monsterDamage, User user) {
        if (ProjectContext.buffMap.containsKey(user.getBuffMap().get(BuffConstant.DEFENSEBUFF))) {
            Buff buff = ProjectContext.buffMap.get(user.getBuffMap().get(BuffConstant.DEFENSEBUFF));
            if (monsterDamage.compareTo(new BigInteger(buff.getInjurySecondValue())) <= 0) {
//              相当于没有攻击
                return new BigInteger(GrobalConfig.MINVALUE);
            }
            BigInteger buffDefence = new BigInteger(buff.getInjurySecondValue());
            monsterDamage = monsterDamage.subtract(buffDefence);
            return monsterDamage;
        }
        return monsterDamage;
    }

}
