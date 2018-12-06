package event;

import buff.Buff;
import component.BossArea;
import component.Monster;
import config.BuffConfig;
import io.netty.channel.Channel;
import memory.NettyMemory;
import org.springframework.stereotype.Component;
import pojo.User;
import skill.UserSkill;
import utils.MessageUtil;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/11/27 17:35
 */
@Component("buffEvent")
public class BuffEvent {
    private static final int allAttackFlag = 1;
    private static final int commonAttackFlag = 0;

    //  填充攻击时技能产生的所有buff
    public int buffSolve(UserSkill userSkill, Monster monster, User user) {
        for (Map.Entry<String, Integer> entry : userSkill.getBuffMap().entrySet()) {
//          处理怪物中毒类型的buff,人物让怪物中毒
            if (entry.getKey().equals(BuffConfig.POISONINGBUFF)) {
                Buff buff = NettyMemory.buffMap.get(entry.getValue());
                monster.getBufMap().put(entry.getKey(), entry.getValue());
                Map<String, Long> tempMap = new HashMap<>();
                tempMap.put(BuffConfig.POISONINGBUFF, System.currentTimeMillis() + buff.getKeepTime() * 1000);
                NettyMemory.monsterBuffEndTime.put(monster, tempMap);
            }
//          人物对自己产生护盾
            if (entry.getKey().equals(BuffConfig.DEFENSEBUFF)) {
                Buff buff = NettyMemory.buffMap.get(entry.getValue());
                user.getBuffMap().put(BuffConfig.DEFENSEBUFF, buff.getBufferId());
                NettyMemory.userBuffEndTime.get(user).put(BuffConfig.DEFENSEBUFF, System.currentTimeMillis() + buff.getKeepTime() * 1000);
            }
//          人物集体治疗
            if (entry.getKey().equals(BuffConfig.TREATMENTBUFF)) {
                Buff buff = NettyMemory.buffMap.get(entry.getValue());
                user.getBuffMap().put(BuffConfig.TREATMENTBUFF, buff.getBufferId());
                //TODO:如果想改成集体快速回血和缓慢回血，这里做区别比较好
                NettyMemory.userBuffEndTime.get(user).put(BuffConfig.TREATMENTBUFF, System.currentTimeMillis() + buff.getKeepTime() * 1000);
            }
//          集体伤害处理
            if (entry.getKey().equals(BuffConfig.ALLPERSON)) {
                Buff buff = NettyMemory.buffMap.get(entry.getValue());
                Channel channelTemp = NettyMemory.userToChannelMap.get(user);
                if (NettyMemory.bossAreaMap.containsKey(user.getTeamId())) {
                    channelTemp.writeAndFlush(MessageUtil.turnToPacket("你使用了" + userSkill.getSkillName() + "对怪物造成了集体伤害" + userSkill.getDamage()));
                    BossArea bossArea = NettyMemory.bossAreaMap.get(user.getTeamId());
                    for (Map.Entry<String, Monster> monsterEntry : bossArea.getMonsters().get(bossArea.getSequence().get(0)).entrySet()) {
                        monsterEntry.getValue().subLife(new BigInteger(userSkill.getDamage()));
                    }
                }
                return allAttackFlag;
            }

//          召唤使用召唤技能，召唤出怪物产生召唤buff，代表召唤怪在战斗中出现的时间
            if (entry.getKey().equals(BuffConfig.BABYBUF)) {
                Buff buff = NettyMemory.buffMap.get(entry.getValue());
                user.getBuffMap().put(BuffConfig.BABYBUF, buff.getBufferId());
                NettyMemory.userBuffEndTime.get(user).put(BuffConfig.BABYBUF, System.currentTimeMillis() + buff.getKeepTime() * 1000);
            }

        }
        return commonAttackFlag;
    }

    //  处理护盾buff效果的展示
    public BigInteger defendBuff(BigInteger monsterDamage, User user, Channel channel) {
        if (NettyMemory.buffMap.containsKey(user.getBuffMap().get(BuffConfig.DEFENSEBUFF))) {
            Buff buff = NettyMemory.buffMap.get(user.getBuffMap().get(BuffConfig.DEFENSEBUFF));
            if (monsterDamage.compareTo(new BigInteger(buff.getInjurySecondValue())) <= 0) {
//              相当于没有攻击
                return new BigInteger("0");
            }
            BigInteger buffDefence = new BigInteger(buff.getInjurySecondValue());
            monsterDamage = monsterDamage.subtract(buffDefence);
            return monsterDamage;
        }
        return monsterDamage;
    }

}
