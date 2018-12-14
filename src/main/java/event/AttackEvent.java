package event;

import caculation.AttackCaculation;
import component.Area;
import component.BossArea;
import component.Equipment;
import config.BuffConfig;
import config.MessageConfig;
import config.DeadOrAliveConfig;
import factory.MonsterFactory;
import io.netty.channel.Channel;
import memory.NettyMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userskillrelation;
import pojo.Weaponequipmentbar;
import skill.UserSkill;
import team.Team;
import utils.AttackUtil;
import utils.MessageUtil;
import component.Monster;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("attackEvent")
public class AttackEvent {
    @Autowired
    private CommonEvent commonEvent;
    @Autowired
    private AttackCaculation attackCaculation;
    @Autowired
    private OutfitEquipmentEvent outfitEquipmentEvent;
    @Autowired
    private ShopEvent shopEvent;
    @Autowired
    private ChatEvent chatEvent;
    @Autowired
    private BuffEvent buffEvent;
    @Autowired
    private MonsterFactory monsterFactory;

    public void attack(Channel channel, String msg) throws IOException {

        if (msg.startsWith("chat")) {
            chatEvent.chat(channel, msg);
            return;
        }
        if (msg.startsWith("s")) {
            shopEvent.shop(channel, msg);
            return;
        }
        if (msg.startsWith("b") || msg.startsWith("w") || msg.startsWith("fix-")) {
            commonEvent.common(channel, msg);
            return;
        }

//      转移攻击目标
        if(msg.startsWith("attack")){
            User user = NettyMemory.session2UserIds.get(channel);
            String temp[]=msg.split("-");
            if(temp.length!=3){
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
                return;
            }
//          在副本中打
            if(NettyMemory.bossAreaMap.containsKey(user.getTeamId())){
                BossArea bossArea = NettyMemory.bossAreaMap.get(user.getTeamId());
                Map<String,Monster> monsterMap = bossArea.getMonsters().get(bossArea.getSequence().get(0));
                NettyMemory.monsterMap.get(user).remove(0);
                NettyMemory.monsterMap.get(user).add(monsterMap.get(temp[1]));
                attackKeySolve(channel,temp[2]);
                return;
            }
//          在普通场景中打
        }


        if (msg.equals("q")) {
            NettyMemory.monsterMap.remove(NettyMemory.session2UserIds.get(channel));
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.RETREATFIGHT));
            NettyMemory.eventStatus.put(channel, EventStatus.STOPAREA);
        } else {
            attackKeySolve(channel,msg);
        }

    }

    private void attackKeySolve(Channel channel,String msg) throws IOException {
        if (NettyMemory.userskillrelationMap.get(channel).containsKey(msg)) {
            User user = NettyMemory.session2UserIds.get(channel);
//          怪物buff处理
            if (user.getBuffMap().get(BuffConfig.SLEEPBUFF) != 5000) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.SLEEPMESSAGE));
                return;
            }

            Monster monster = NettyMemory.monsterMap.get(user).get(0);
            UserSkill userSkill = NettyMemory.SkillMap.get(NettyMemory.userskillrelationMap.get(channel).get(msg).getSkillid());
            Userskillrelation userskillrelation = NettyMemory.userskillrelationMap.get(channel).get(msg);
//          技能CD检查
            if (System.currentTimeMillis() > userskillrelation.getSkillcds() + userSkill.getAttackCd()) {

//              技能buff处理
                if(buffEvent.buffSolve(userSkill, monster, user)==1){
                    return;
                }

//              人物蓝量检查
                BigInteger userMp = new BigInteger(user.getMp());
                BigInteger skillMp = new BigInteger(userSkill.getSkillMp());
                if (userMp.compareTo(skillMp) > 0) {
                    BigInteger attackDamage = new BigInteger(userSkill.getDamage());
//                          增加装备攻击属性
//                          更新武器耐久度
                    attackDamage = attackCaculation.caculate(user, attackDamage);
//                          攻击逻辑
                    BigInteger monsterLife = monster.subLife(attackDamage);
//                         蓝量计算
                    user.setMp(userMp.subtract(skillMp).toString());
                    String resp = out(user);
                    BigInteger minValueOfLife = new BigInteger("0");
//                         检查怪物血量
                    if (monsterLife.compareTo(minValueOfLife) <= 0) {
//                              蓝量计算逻辑
                        monster.setValueOfLife(minValueOfLife.toString());
                        if (user.getMp().equals("0")) {
                            user.setMp("0");
                        }
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
                        channel.writeAndFlush(MessageUtil.turnToPacket(resp));
                        monster.setStatus(DeadOrAliveConfig.DEAD);

//                      boss战斗场景
                        if (monster.getType().equals(Monster.TYPEOFBOSS)) {
                            BossArea bossArea = NettyMemory.bossAreaMap.get(user.getTeamId());
                            bossArea.getMonsters().get(bossArea.getSequence().get(0)).get(monster.getName()).setStatus(DeadOrAliveConfig.DEAD);
//                          更改用户攻击的boss
                            AttackUtil.changeUserAttackMonster(user,bossArea);
                            AttackUtil.killBossMessageToAll(user,monster);
                        }

//                          普通战斗场景
                        if (monster.getType().equals(Monster.TYPEOFCOMMONMONSTER)) {
//                         移除死掉的怪物
                            NettyMemory.areaMap.get(user.getPos()).getMonsters().remove(monster);
//                          生成新的怪物
                            Area area = NettyMemory.areaMap.get(user.getPos());
                            area.getMonsters().add(monsterFactory.getMonsterByArea(user.getPos()));
                        }
                    } else {
                        resp +=
                                System.getProperty("line.separator")
                                        + "[" + userSkill.getSkillName()
                                        + "]技能对" + monster.getName()
                                        + "造成了" + attackDamage + "点伤害"
                                        + System.getProperty("line.separator")
                                        + "[消耗蓝量:]" + userSkill.getSkillMp()
                                        + System.getProperty("line.separator")
                                        + "[人物剩余蓝量值:]" + user.getMp()
                                        + System.getProperty("line.separator")
                                        + "怪物剩余血量:" + monster.getValueOfLife();
                        userskillrelation.setSkillcds(System.currentTimeMillis());
//                  更新用户总战斗伤害的值
                        if (user.getTeamId() != null && NettyMemory.bossAreaMap.get(user.getTeamId()) != null) {
                            if (!NettyMemory.bossAreaMap.get(user.getTeamId()).getDamageAll().containsKey(user)) {
                                NettyMemory.bossAreaMap.get(user.getTeamId()).getDamageAll().put(user, attackDamage.toString());
                            } else {
                                String newDamageValue = NettyMemory.bossAreaMap.get(user.getTeamId()).getDamageAll().get(user);
                                BigInteger newDamageValueI = new BigInteger(newDamageValue).add(attackDamage);
                                NettyMemory.bossAreaMap.get(user.getTeamId()).getDamageAll().put(user, newDamageValueI.toString());
                            }
                        }
                        channel.writeAndFlush(MessageUtil.turnToPacket(resp));
                    }
                } else {
                    channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNENOUGHMP));
                }
            } else {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNSKILLCD));
            }
        }
    }

    private Map<String, Monster> changeToMap(User user) {
        List<Monster> monsterList = NettyMemory.areaMap.get(user.getPos()).getMonsters();
        Map<String, Monster> map = new HashMap<>();
        for (Monster monster : monsterList) {
            map.put(monster.getId() + "", monster);
        }
        return map;
    }

    private String out(User user) {
        String resp = "";
        for (Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()) {
            Equipment equipment = NettyMemory.equipmentMap.get(weaponequipmentbar.getWid());
            resp += System.getProperty("line.separator")
                    + equipment.getName() + "剩余耐久为:" + weaponequipmentbar.getDurability()
            ;
        }
        return resp;
    }

    private Map<String, Monster> getMonsterMap(User user) {
        return NettyMemory.bossAreaMap.get(getTeam(user).getTeamId()).getMap();
    }

    private Team getTeam(User user) {
        if (!NettyMemory.teamMap.containsKey(user.getTeamId())) return null;
        return NettyMemory.teamMap.get(user.getTeamId());
    }
}
