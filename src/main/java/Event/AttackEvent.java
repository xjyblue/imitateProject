package Event;

import io.netty.channel.Channel;
import memory.NettyMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userskillrelation;
import skill.UserSkill;
import utils.DelimiterUtils;
import component.Monster;

import java.math.BigInteger;

@Component("attackEvent")
public class AttackEvent {
    @Autowired
    private CommonEvent commonEvent;

    public void attack(Channel channel, String msg) {
        if(msg.equals("b")||msg.startsWith("b-")){
            commonEvent.common(channel,msg);
            return;
        }
        if (msg.equals("q")) {
            NettyMemory.monsterMap.remove(NettyMemory.session2UserIds.get(channel));
            channel.writeAndFlush(DelimiterUtils.addDelimiter("退出战斗"));
            NettyMemory.eventStatus.put(channel,EventStatus.STOPAREA);
        } else {
            if (NettyMemory.userskillrelationMap.get(channel).containsKey(msg)) {
                User user = NettyMemory.session2UserIds.get(channel);
                for (Monster monster : NettyMemory.areaMap.get(user.getPos()).getMonsters()) {
                    UserSkill userSkill = NettyMemory.SkillMap.get(NettyMemory.userskillrelationMap.get(channel).get(msg).getSkillid());
                    Userskillrelation userskillrelation = NettyMemory.userskillrelationMap.get(channel).get(msg);
//                  技能CD检查
                    if (System.currentTimeMillis() > userskillrelation.getSkillcds() + userSkill.getAttackCd()) {
//                     人物蓝量检查
                        BigInteger userMp = new BigInteger(user.getMp());
                        BigInteger skillMp = new BigInteger(userSkill.getSkillMp());
                        if (userMp.compareTo(skillMp) > 0) {
//                          攻击逻辑
                            BigInteger attackDamage = new BigInteger(userSkill.getDamage());
                            BigInteger monsterLife = new BigInteger(monster.getValueOfLife());
//                         检查怪物血量
                            if (attackDamage.compareTo(monsterLife) >= 0) {
//                              蓝量计算逻辑
                                user.subMp(skillMp.toString());
                                String resp = System.getProperty("line.separator")
                                        + "[技能]:" + userSkill.getSkillName()
                                        + System.getProperty("line.separator")
                                        + "对[" + monster.getName()
                                        + "]造成了" + userSkill.getDamage() + "点伤害"
                                        + System.getProperty("line.separator")
                                        + "[怪物血量]:" + 0
                                        + System.getProperty("line.separator")
                                        + "[消耗蓝量]:" + userSkill.getSkillMp()
                                        + System.getProperty("line.separator")
                                        + "[人物剩余蓝量]:" + user.getMp()
                                        + System.getProperty("line.separator")
                                        + "怪物已死亡";
                                channel.writeAndFlush(DelimiterUtils.addDelimiter(resp));
//                                         修改怪物状态
                                monster.setValueOfLife("0");
                                monster.setStatus("0");
//                                  移除任务攻击记录
                                NettyMemory.monsterMap.remove(user);
//                                           切换场景
                                NettyMemory.eventStatus.put(channel, EventStatus.STOPAREA);
                            } else {
//                              生命值攻击计算逻辑
                                monsterLife = monsterLife.subtract(attackDamage);
                                monster.setValueOfLife(monsterLife.toString());
//                              蓝量计算逻辑
                                user.subMp(skillMp.toString());
                                String resp =
                                        System.getProperty("line.separator")
                                                + "[" + userSkill.getSkillName()
                                                + "]技能对" + monster.getName()
                                                + "造成了" + userSkill.getDamage() + "点伤害"
                                                + System.getProperty("line.separator")
                                                + "[消耗蓝量:]" + userSkill.getSkillMp()
                                                + System.getProperty("line.separator")
                                                + "[人物剩余蓝量值:]" + user.getMp()
                                                + System.getProperty("line.separator")
                                                + "怪物剩余血量:" + monster.getValueOfLife();
                                userskillrelation.setSkillcds(System.currentTimeMillis());
                                //TODO:数据库更新技能时间
                                channel.writeAndFlush(DelimiterUtils.addDelimiter(resp));
                            }
                        } else {
                            channel.writeAndFlush(DelimiterUtils.addDelimiter("技能蓝量不足"));
                        }
                    } else {
                            channel.writeAndFlush(DelimiterUtils.addDelimiter("技能冷却中"));
                    }
                }
            }
        }
    }
}
