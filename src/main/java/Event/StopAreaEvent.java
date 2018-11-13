package Event;

import caculation.AttackCaculation;
import component.Equipment;
import component.Monster;
import component.NPC;
import io.netty.channel.Channel;
import mapper.UserMapper;
import memory.NettyMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userskillrelation;
import pojo.Weaponequipmentbar;
import skill.UserSkill;
import utils.DelimiterUtils;

import java.math.BigInteger;
import java.util.*;

@Component("stopAreaEvent")
public class StopAreaEvent {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommonEvent commonEvent;
    @Autowired
    private AttackCaculation attackCaculation;

    public void stopArea(Channel channel, String msg) {
        if (msg.equals("b") || msg.startsWith("b-") || msg.equals("w")
                || msg.startsWith("w-")||msg.startsWith("fix-")||msg.startsWith("ww-")||msg.startsWith("wq-")) {
            commonEvent.common(channel, msg);
            return;
        }
        String temp[] = null;
        if (msg.startsWith("move")) {
            temp = msg.split("-");
            User user = NettyMemory.session2UserIds.get(channel);
            if (temp[1].equals(NettyMemory.areaMap.get(user.getPos()).getName())) {
                channel.writeAndFlush(DelimiterUtils.addDelimiter("原地无需移动"));
            } else {
                if (!NettyMemory.areaSet.contains(temp[1])) {
                    channel.writeAndFlush(DelimiterUtils.addDelimiter("移动地点不存在"));
                } else {
                    if (NettyMemory.areaMap.get(user.getPos()).getAreaSet().contains(temp[1])) {
                        user.setPos(NettyMemory.areaToNum.get(temp[1]));
                        userMapper.updateByPrimaryKeySelective(user);
                        NettyMemory.session2UserIds.put(channel, user);
                        channel.writeAndFlush(DelimiterUtils.addDelimiter("已移动到" + temp[1]));
                    } else {
                        channel.writeAndFlush(DelimiterUtils.addDelimiter("请充值才能启用传送门"));
                    }
                }
            }
        } else if (msg.startsWith("aoi")) {
            User user = NettyMemory.session2UserIds.get(channel);
            String allStatus = System.getProperty("line.separator")
                    + "玩家" + user.getUsername()
                    + "--------玩家的状态" + user.getStatus()
                    + "--------处于" + NettyMemory.areaMap.get(user.getPos()).getName()
                    + "--------玩家的HP量：" + user.getHp()
                    + "--------玩家的MP量：" + user.getMp()
                    + System.getProperty("line.separator");
            for (Monster monster : NettyMemory.areaMap.get(user.getPos()).getMonsters()) {
                allStatus += "怪物：" + monster.getName() + "的血量为：" + monster.getValueOfLife() + System.getProperty("line.separator");
            }
            for (Map.Entry<Channel, User> entry : NettyMemory.session2UserIds.entrySet()) {
                if (!user.getUsername().equals(entry.getValue().getUsername()) && user.getPos().equals(entry.getValue().getPos())) {
                    allStatus += "其他玩家" + entry.getValue().getUsername() + "---" + entry.getValue().getStatus() + System.getProperty("line.separator");
                }
            }
            for (NPC npc : NettyMemory.areaMap.get(user.getPos()).getNpcs()) {
                allStatus += "NPC:" + npc.getName() + "---" + npc.getStatus() + System.getProperty("line.separator");
            }
            for (Monster monster : NettyMemory.areaMap.get(user.getPos()).getMonsters()) {
                allStatus += "怪物有" + monster.getName() + "---生命值---" + monster.getValueOfLife()
                        + "---攻击技能为---" + monster.getMonsterSkillList().get(0).getSkillName()
                        + "伤害为：" + monster.getMonsterSkillList().get(0).getDamage() + System.getProperty("line.separator");
            }
            channel.writeAndFlush(DelimiterUtils.addDelimiter(allStatus));
        } else if (msg.startsWith("talk")) {
            temp = msg.split("-");
            if (temp.length != 2) {
                channel.writeAndFlush(DelimiterUtils.addDelimiter("输入错误命令"));
            } else {
                List<NPC> npcs = NettyMemory.areaMap.get(NettyMemory.session2UserIds.get(channel).getPos())
                        .getNpcs();
                for (NPC npc : npcs) {
                    if (npc.getName().split("-")[1].equals(temp[1])) {
                        channel.writeAndFlush(DelimiterUtils.addDelimiter(npc.getTalks().get(0)));
                        break;
                    }
                }
                channel.writeAndFlush(DelimiterUtils.addDelimiter("找不到此NPC"));
            }
        } else if (msg.equals("skillCheckout")) {
            NettyMemory.eventStatus.put(channel, EventStatus.SKILLMANAGER);
            channel.writeAndFlush(DelimiterUtils.addDelimiter("请输入lookSkill查看技能，请输入change-技能名-键位配置技能"));
        } else if (msg.startsWith("attack")) {
            temp = msg.split("-");
//                        输入的键位是否存在
            if (temp.length == 3 && NettyMemory.userskillrelationMap.get(channel).containsKey(temp[2])) {
                User user = NettyMemory.session2UserIds.get(channel);
                for (Monster monster : NettyMemory.areaMap.get(user.getPos()).getMonsters()) {
//                                输入的怪物是否存在
                    if (monster.getName().equals(temp[1]) && !monster.getStatus().equals("0")) {
                        Userskillrelation userskillrelation = NettyMemory.userskillrelationMap.get(channel).get(temp[2]);
                        UserSkill userSkill = NettyMemory.SkillMap.get(userskillrelation.getSkillid());
//                                    判断人物MP量是否足够
                        BigInteger userMp = new BigInteger(user.getMp());
                        BigInteger skillMp = new BigInteger(userSkill.getSkillMp());
                        if (userMp.compareTo(skillMp) > 0) {
                            userMp = userMp.subtract(skillMp);
                            user.setMp(userMp.toString());
//                                    判断技能冷却
                            if (System.currentTimeMillis() > userskillrelation.getSkillcds() + userSkill.getAttackCd()) {
//                               判断攻击完怪物是否死亡，生命值计算逻辑
                                BigInteger monsterLife = new BigInteger(monster.getValueOfLife());
                                BigInteger attackDamage = new BigInteger(userSkill.getDamage());
                                StringBuffer stringBuffer = new StringBuffer();
                                //                             攻击逻辑计算
                                attackDamage = attackCaculation.caculate(user, attackDamage);
                                String resp = out(user);
//                              蓝量计算逻辑
                                user.subMp(userSkill.getSkillMp());
                                if (attackDamage.compareTo(monsterLife) >= 0) {
                                    resp += System.getProperty("line.separator")
                                            + "[技能]:" + userSkill.getSkillName()
                                            + System.getProperty("line.separator")
                                            + "对[" + monster.getName()
                                            + "]造成了" + attackDamage + "点伤害"
                                            + System.getProperty("line.separator")
                                            + "[怪物血量]:" + 0
                                            + System.getProperty("line.separator")
                                            + "[消耗蓝量]:" + userSkill.getSkillMp()
                                            + System.getProperty("line.separator")
                                            + "[人物剩余蓝量]:" + user.getMp()
                                            + System.getProperty("line.separator")
                                            + "怪物已死亡";
                                    monster.setValueOfLife("0");
                                    channel.writeAndFlush(DelimiterUtils.addDelimiter(resp));
//                                                修改怪物状态
                                    monster.setStatus("0");
                                } else {
                                    Map<String, Userskillrelation> map = NettyMemory.userskillrelationMap.get(channel);
//                                    切换到攻击模式
                                    NettyMemory.eventStatus.put(channel, EventStatus.ATTACK);
//                                    怪物掉血，生命值计算逻辑
                                    monsterLife = monsterLife.subtract(attackDamage);
                                    monster.setValueOfLife(monsterLife.toString());
//                                  蓝量计算逻辑
                                    user.subMp(userSkill.getSkillMp());
                                    resp += System.getProperty("line.separator")
                                            + "[技能]:" + userSkill.getSkillName()
                                            + System.getProperty("line.separator")
                                            + "对[" + monster.getName()
                                            + "]造成了" + attackDamage + "点伤害"
                                            + System.getProperty("line.separator")
                                            + "[怪物血量]:" + monster.getValueOfLife()
                                            + System.getProperty("line.separator")
                                            + "[消耗蓝量]:" + userSkill.getSkillMp()
                                            + System.getProperty("line.separator")
                                            + "[人物剩余蓝量]:" + user.getMp()
                                            + System.getProperty("line.separator");
                                    channel.writeAndFlush(DelimiterUtils.addDelimiter(resp));
                                    //TODO:更新数据库人物技能蓝量
//                                    刷新技能时间
                                    userskillrelation.setSkillcds(System.currentTimeMillis());
//                                  记录任务当前攻击的怪物
                                    List<Monster> monsters = new ArrayList<Monster>();
                                    monsters.add(monster);
                                    NettyMemory.monsterMap.put(user, monsters);
//                                    提醒用户你已进入战斗模式
                                    channel.writeAndFlush(DelimiterUtils.addDelimiter("你已经进入战斗模式"));
                                }
                            }
                        } else {
                            channel.writeAndFlush(DelimiterUtils.addDelimiter("人物MP值不足"));
                        }
                        break;
                    }
                }
            }
        } else {
            channel.writeAndFlush(DelimiterUtils.addDelimiter("请输入有效指令"));
        }
    }

    private String out(User user) {
        String resp = "";
        for(Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()){
            Equipment equipment = NettyMemory.equipmentMap.get(weaponequipmentbar.getWid());
            resp += System.getProperty("line.separator")
                    + equipment.getName()+"剩余耐久为:" +weaponequipmentbar.getDurability()
                    ;
        }
        return resp;
    }

}
