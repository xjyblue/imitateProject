package event;

import buff.Buff;
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

//  填充攻击时技能产生的所有buff
    public void buffSolve(UserSkill userSkill, Monster monster, User user){
        for(Map.Entry<String,Integer>entry:userSkill.getBuffMap().entrySet()){
//          处理怪物中毒类型的buff
            if(entry.getKey().equals(BuffConfig.POISONINGBUFF)){
                Buff buff = NettyMemory.buffMap.get(entry.getValue());
                monster.getBufMap().put(entry.getKey(),entry.getValue());
                Map<String,Long> tempMap = new HashMap<>();
                tempMap.put(BuffConfig.POISONINGBUFF,System.currentTimeMillis()+buff.getKeepTime()*1000);
                NettyMemory.monsterBuffEndTime.put(monster,tempMap);
            }

            if(entry.getKey().equals(BuffConfig.DEFENSEBUFF)){
                Buff buff = NettyMemory.buffMap.get(entry.getValue());
                user.getBufferMap().put(BuffConfig.DEFENSEBUFF,buff.getBufferId());
                NettyMemory.userBuffEndTime.get(user).put(BuffConfig.DEFENSEBUFF,System.currentTimeMillis()+buff.getKeepTime()*1000);
            }
        }
    }

//  处理护盾buff效果的展示
    public BigInteger defendBuff(BigInteger monsterDamage, User user, Channel channel){
        if(NettyMemory.buffMap.containsKey(user.getBufferMap().get(BuffConfig.DEFENSEBUFF))){
            Buff buff = NettyMemory.buffMap.get(user.getBufferMap().get(BuffConfig.DEFENSEBUFF));
            BigInteger buffDefence = new BigInteger(buff.getInjurySecondValue());
            monsterDamage = monsterDamage.subtract(buffDefence);
            return monsterDamage;
        }
        return monsterDamage;
    }

}
