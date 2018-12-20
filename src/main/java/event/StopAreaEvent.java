package event;

import achievement.Achievement;
import achievement.AchievementExecutor;
import caculation.AttackCaculation;
import component.Scene;
import component.Equipment;
import component.Monster;
import component.NPC;
import config.MessageConfig;
import config.DeadOrAliveConfig;
import factory.MonsterFactory;
import io.netty.channel.Channel;
import mapper.UserMapper;
import mapper.UserbagMapper;
import context.ProjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.*;
import skill.UserSkill;
import utils.MessageUtil;
import utils.SceneUtil;
import utils.UserbagUtil;

import java.io.IOException;
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
    @Autowired
    private TeamEvent teamEvent;
    @Autowired
    private BossEvent bossEvent;
    @Autowired
    private ShopEvent shopEvent;
    @Autowired
    private OutfitEquipmentEvent outfitEquipmentEvent;
    @Autowired
    private ChatEvent chatEvent;
    @Autowired
    private EmailEvent emailEvent;
    @Autowired
    private PKEvent pkEvent;
    @Autowired
    private BuffEvent buffEvent;
    @Autowired
    private MonsterFactory monsterFactory;
    @Autowired
    private TransactionEvent transactionEvent;
    @Autowired
    private LabourUnionEvent labourUnionEvent;
    @Autowired
    private AchievementExecutor achievementExecutor;
    @Autowired
    private FriendEvent friendEvent;
    @Autowired
    private UserbagMapper userbagMapper;
    public void stopArea(Channel channel, String msg) throws IOException {
        if (msg.startsWith("iftrade") || msg.startsWith("ytrade") || msg.equals("ntrade")) {
            transactionEvent.trade(channel, msg);
            return;
        }
        if (msg.startsWith("pk")) {
            pkEvent.pkOthers(channel, msg);
            return;
        }
        if (msg.startsWith("email")) {
            emailEvent.email(channel, msg);
            return;
        }
        if (msg.startsWith("chat")) {
            chatEvent.chat(channel, msg);
            return;
        }
        if (msg.startsWith("s") && !msg.equals("skillCheckout")) {
            shopEvent.shop(channel, msg);
            return;
        }
        if (msg.equals("f")) {
            bossEvent.enterBossArea(channel, msg);
            return;
        }
        if (msg.startsWith("t") && !msg.startsWith("talk")) {
            teamEvent.team(channel, msg);
            return;
        }
        if (msg.equals("g")) {
            labourUnionEvent.solve(channel, msg);
            return;
        }
        if (msg.equals("p")) {
            friendEvent.solve(channel, msg);
            return;
        }
        if (msg.equals("aoi")) {
            commonEvent.common(channel, msg);
            return;
        }
        if (msg.startsWith("b") || msg.startsWith("w") || msg.startsWith("fix-")) {
            commonEvent.common(channel, msg);
            return;
        }
        if (msg.startsWith("i")) {
            upEquipmentStartlevel(channel, msg);
            return;
        }
        String temp[] = null;
        if (msg.startsWith("move")) {
            temp = msg.split("-");
            if (temp.length != 2) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
                return;
            }
            User user = ProjectContext.session2UserIds.get(channel);
            if (temp[1].equals(ProjectContext.sceneMap.get(user.getPos()).getName())) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNMOVELOCAL));
            } else {
//              场景的移动切换用户到不同的场景线程
                if (!ProjectContext.areaSet.contains(temp[1])) {
                    channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOTARGETTOMOVE));
                } else {
                    if (ProjectContext.sceneMap.get(user.getPos()).getSceneSet().contains(temp[1])) {
//                      场景切换
                        Scene scene = ProjectContext.sceneMap.get(user.getPos());
                        scene.getUserMap().remove(user.getUsername());

                        Scene sceneTarget = ProjectContext.sceneMap.get(SceneUtil.getSceneByName(temp[1]).getId());
                        sceneTarget.getUserMap().put(user.getUsername(), user);

                        user.setPos(sceneTarget.getId());
                        userMapper.updateByPrimaryKeySelective(user);

                        ProjectContext.session2UserIds.put(channel, user);
                        channel.writeAndFlush(MessageUtil.turnToPacket("已移动到" + temp[1]));
                    } else {
                        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.REMOTEMOVEMESSAGE));
                    }
                }
            }
        } else if (msg.startsWith("talk")) {
            temp = msg.split("-");
            User user = ProjectContext.session2UserIds.get(channel);
            if (temp.length != 2) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            } else {
                List<NPC> npcs = ProjectContext.sceneMap.get(ProjectContext.session2UserIds.get(channel).getPos())
                        .getNpcs();
                for (NPC npc : npcs) {
                    if (npc.getName().equals(temp[1])) {
                        channel.writeAndFlush(MessageUtil.turnToPacket(npc.getTalk()));
//                      人物任务触发
                        for (Achievementprocess achievementprocess : user.getAchievementprocesses()) {
                            Achievement achievement = ProjectContext.achievementMap.get(achievementprocess.getAchievementid());
                            if (achievementprocess.getType().equals(Achievement.TALKTONPC)) {
                                achievementExecutor.executeTalkNPC(achievementprocess, user, achievement, npc);
                            }
                        }
                        return;
                    }
                }
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOFOUNDNPC));
            }
        } else if (msg.equals("skillCheckout")) {
            ProjectContext.eventStatus.put(channel, EventStatus.SKILLMANAGER);
            channel.writeAndFlush(MessageUtil.turnToPacket("请输入lookSkill查看技能，请输入change-技能名-键位配置技能,请输入quitSkill退出技能管理界面"));
        } else if (msg.startsWith("attack")) {
            temp = msg.split("-");
//          输入的键位是否存在
            if (temp.length == 3 && ProjectContext.userskillrelationMap.get(channel).containsKey(temp[2])) {
                User user = ProjectContext.session2UserIds.get(channel);
                for (Monster monster : ProjectContext.sceneMap.get(user.getPos()).getMonsters()) {
//                 输入的怪物是否存在
                    if (monster.getName().equals(temp[1])) {
                        if (monster.getStatus().equals(DeadOrAliveConfig.DEAD)) {
                            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.DONOTATTACKDEADMONSTER));
                            return;
                        }
                        Userskillrelation userskillrelation = ProjectContext.userskillrelationMap.get(channel).get(temp[2]);
                        UserSkill userSkill = ProjectContext.skillMap.get(userskillrelation.getSkillid());
//                      判断人物MP量是否足够
                        BigInteger userMp = new BigInteger(user.getMp());
                        BigInteger skillMp = new BigInteger(userSkill.getSkillMp());
                        if (userMp.compareTo(skillMp) > 0) {
//                      蓝量计算
                            userMp = userMp.subtract(skillMp);
                            user.setMp(userMp.toString());
//                          判断技能冷却
                            if (System.currentTimeMillis() > userskillrelation.getSkillcds() + userSkill.getAttackCd()) {

//                               处理用户攻击产生的一系列buff
                                buffEvent.buffSolve(userskillrelation, userSkill, monster, user);

//                               判断攻击完怪物是否死亡，生命值计算逻辑
                                BigInteger attackDamage = new BigInteger(userSkill.getDamage());
//                              攻击逻辑计算
                                attackDamage = attackCaculation.caculate(user, attackDamage);
//                              怪物掉血，生命值计算逻辑
                                BigInteger monsterLife = monster.subLife(attackDamage);
                                String resp = out(user);
                                BigInteger minValueOfLife = new BigInteger("0");
                                if (monsterLife.compareTo(minValueOfLife) <= 0) {
                                    monster.setValueOfLife(minValueOfLife.toString());
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
                                    channel.writeAndFlush(MessageUtil.turnToPacket(resp));
//                                                修改怪物状态
                                    monster.setStatus(DeadOrAliveConfig.DEAD);
//                                  爆装备
                                    outfitEquipmentEvent.getGoods(channel, monster);

//                          移除死掉的怪物
                                    ProjectContext.sceneMap.get(user.getPos()).getMonsters().remove(monster);
//                          生成新的怪物
                                    Scene scene = ProjectContext.sceneMap.get(user.getPos());
                                    scene.getMonsters().add(monsterFactory.getMonsterByArea(user.getPos()));

                                } else {
                                    Map<String, Userskillrelation> map = ProjectContext.userskillrelationMap.get(channel);
//                                    切换到攻击模式
                                    ProjectContext.eventStatus.put(channel, EventStatus.ATTACK);
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
                                    //TODO:更新数据库人物技能蓝量
//                                    刷新技能时间
                                    userskillrelation.setSkillcds(System.currentTimeMillis());
//                                  记录任务当前攻击的怪物
                                    Map<Integer, Monster> monsterMap = new HashMap<>();
                                    monsterMap.put(monster.getId(), monster);
//                                  初始化怪物攻击时间
                                    monster.setAttackEndTime(0l);
                                    ProjectContext.userToMonsterMap.put(user, monsterMap);
//                                    提醒用户你已进入战斗模式
//                                    String jobId = UUID.randomUUID().toString();
//                                    MonsterAttackTask monsterAttackTask = new MonsterAttackTask(channel, jobId, ProjectContext.futureMap, outfitEquipmentEvent, buffEvent);
//                                    Future future = ProjectContext.monsterThreadPool.scheduleAtFixedRate(monsterAttackTask, 0, 1, TimeUnit.SECONDS);
//                                    ProjectContext.futureMap.put(jobId, future);
                                    channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ENTERFIGHT));
                                }
                            }
                        } else {
                            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNENOUGHMP));
                        }
                        break;
                    }
                }
            } else {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOKEYSKILL));
            }
        } else {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
        }
    }

    //  装备升星，先不注入后期注入
    private void upEquipmentStartlevel(Channel channel, String msg) {
        String[] temp = msg.split("=");
        if (temp.length != 2) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }
        User user = ProjectContext.session2UserIds.get(channel);
        Userbag userbag = UserbagUtil.getUserbagByUserbagId(user, temp[1]);
        if (userbag == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.GOODNOEXISTBAG));
            return;
        }
//      开始升星
        if (userbag.getStartlevel() == null) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOTOUPSTARTLEVEL));
            return;
        }

        user.subMoney(new BigInteger("100000"));
        userbag.setStartlevel(userbag.getStartlevel()+1);
        userbagMapper.updateByPrimaryKeySelective(userbag);
        channel.writeAndFlush(MessageUtil.turnToPacket("升星成功，升星花费100000金币,当前装备星级" + userbag.getStartlevel()));
        UserbagUtil.refreshUserbagInfo(channel);
    }

    private String out(User user) {
        String resp = "";
        for (Weaponequipmentbar weaponequipmentbar : user.getWeaponequipmentbars()) {
            Equipment equipment = ProjectContext.equipmentMap.get(weaponequipmentbar.getWid());
            resp += System.getProperty("line.separator")
                    + equipment.getName() + "剩余耐久为:" + weaponequipmentbar.getDurability();
        }
        return resp;
    }

}
