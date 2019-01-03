package service.buffservice.service;

import service.buffservice.entity.Buff;
import com.google.common.collect.Lists;
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
 * Description ：nettySpringServer
 * Created by server on 2018/11/27 17:35
 */
@Component
public class BuffService {
    private static final int allAttackFlag = 1;
    private static final int commonAttackFlag = 0;

    //  填充攻击时技能产生的所有buff
    public int buffSolve(Userskillrelation userskillrelation, UserSkill userSkill, Monster monster, User user) {
        for (Map.Entry<String, Integer> entry : userSkill.getBuffMap().entrySet()) {
//          处理怪物中毒类型的buff,人物让怪物中毒
            if (entry.getKey().equals(BuffConstant.POISONINGBUFF)) {
                Buff buff = ProjectContext.buffMap.get(entry.getValue());
                monster.getBufMap().put(entry.getKey(), entry.getValue());
                Map<String, Long> tempMap = new HashMap<>();
                tempMap.put(BuffConstant.POISONINGBUFF, System.currentTimeMillis() + buff.getKeepTime() * 1000);
                ProjectContext.monsterBuffEndTime.put(monster, tempMap);
            }
//          人物对自己产生护盾
            if (entry.getKey().equals(BuffConstant.DEFENSEBUFF)) {
                Buff buff = ProjectContext.buffMap.get(entry.getValue());
                user.getBuffMap().put(BuffConstant.DEFENSEBUFF, buff.getBufferId());
                ProjectContext.userBuffEndTime.get(user).put(BuffConstant.DEFENSEBUFF, System.currentTimeMillis() + buff.getKeepTime() * 1000);
            }
//          人物集体治疗
            if (entry.getKey().equals(BuffConstant.TREATMENTBUFF)) {
                Buff buff = ProjectContext.buffMap.get(entry.getValue());
                user.getBuffMap().put(BuffConstant.TREATMENTBUFF, buff.getBufferId());
                //TODO:如果想改成集体快速回血和缓慢回血，这里做区别比较好
                ProjectContext.userBuffEndTime.get(user).put(BuffConstant.TREATMENTBUFF, System.currentTimeMillis() + buff.getKeepTime() * 1000);
            }
//          集体伤害处理
            //todo:要改掉这里
            if (entry.getKey().equals(BuffConstant.ALLPERSON)) {
                Buff buff = ProjectContext.buffMap.get(entry.getValue());
                Channel channelTemp = ProjectContext.userToChannelMap.get(user);
                userskillrelation.setSkillcds(userSkill.getAttackCd() + System.currentTimeMillis());
                if (user.getTeamId() != null && ProjectContext.bossAreaMap.containsKey(user.getTeamId())) {
                    channelTemp.writeAndFlush(MessageUtil.turnToPacket("你使用了" + userSkill.getSkillName() + "对怪物造成了集体伤害" + userSkill.getDamage()));
                    BossScene bossScene = ProjectContext.bossAreaMap.get(user.getTeamId());
                    for (Map.Entry<String, Monster> monsterEntry : bossScene.getMonsters().get(bossScene.getSequence().get(0)).entrySet()) {
                        monsterEntry.getValue().subLife(new BigInteger(userSkill.getDamage()));
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
                            monsterTemp.subLife(new BigInteger(userSkill.getDamage()));
                        }
                    }
                }
                return allAttackFlag;
            }

//          召唤使用召唤技能，召唤出怪物产生召唤buff，代表召唤怪在战斗中出现的时间
            if (entry.getKey().equals(BuffConstant.BABYBUF)) {
                Buff buff = ProjectContext.buffMap.get(entry.getValue());
                user.getBuffMap().put(BuffConstant.BABYBUF, buff.getBufferId());
                ProjectContext.userBuffEndTime.get(user).put(BuffConstant.BABYBUF, System.currentTimeMillis() + buff.getKeepTime() * 1000);
            }

//          集体解除控制
            if (entry.getKey().equals(BuffConstant.RELIEVEBUFF)) {
                Buff buff = ProjectContext.buffMap.get(entry.getValue());
                List<User> userTarget = null;
                if (user.getTeamId() != null && ProjectContext.bossAreaMap.containsKey(user.getTeamId())) {
                    BossScene bossScene = ProjectContext.bossAreaMap.get(user.getTeamId());
                    userTarget = Lists.newArrayList(bossScene.getUserMap().values());
                } else {
                    userTarget = Lists.newArrayList();
                    userTarget.add(user);
                }
                for (User userT : userTarget) {
                    userT.getBuffMap().put(BuffConstant.SLEEPBUFF,5000);
                    Channel channelT = ProjectContext.userToChannelMap.get(user);
                    channelT.writeAndFlush(MessageUtil.turnToPacket(user.getUsername() + "解除了怪物的眩晕效果"));
                }
            }

        }
        return commonAttackFlag;
    }

    //  处理护盾buff效果的展示
    public BigInteger defendBuff(BigInteger monsterDamage, User user, Channel channel) {
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
