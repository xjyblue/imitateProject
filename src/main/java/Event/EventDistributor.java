package Event;


import java.math.BigInteger;
import java.util.*;
import java.util.Map.Entry;


import mapper.UserskillrelationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import Component.NPC;
import Component.Monster;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import mapper.UserMapper;
import memory.NettyMemory;
import pojo.User;
import pojo.Userskillrelation;
import pojo.UserskillrelationExample;
import skill.UserSkill;
import utils.DelimiterUtils;

/**
 * 具体时间分发器
 *
 * @author xiaojianyu
 */
@Component("eventDistributor")
public class EventDistributor {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserskillrelationMapper userskillrelationMapper;

    public void distributeEvent(ChannelHandlerContext ctx, String msg) {
        Channel ch = ctx.channel();
        if (!NettyMemory.eventStatus.containsKey(ch)) {
            if (msg.equals("d")) {
                NettyMemory.eventStatus.put(ch, EventStatus.LOGIN);
                ctx.writeAndFlush(DelimiterUtils.addDelimiter("请输入：用户名-密码"));
            }
            if (msg.equals("z")) {
                NettyMemory.eventStatus.put(ch, EventStatus.REGISTER);
                ctx.writeAndFlush(DelimiterUtils.addDelimiter("请输入：用户名-密码-确认密码"));
            }
        } else {
            String status = NettyMemory.eventStatus.get(ch);
            String temp[] = null;
            switch (status) {
                case EventStatus.LOGIN:
                    temp = msg.split("-");
                    if (temp.length != 2) {
                        ctx.writeAndFlush(DelimiterUtils.addDelimiter("输入错误命令"));
                    } else {
                        User user2 = userMapper.selectByPrimaryKey(temp[0]);
                        if (user2 == null || (!user2.getPassword().equals(temp[1]))) {
                            ctx.writeAndFlush(DelimiterUtils.addDelimiter("账户密码出错"));
                        } else {
                            //                             初始化玩家的技能start
                            UserskillrelationExample userskillrelationExample = new UserskillrelationExample();
                            UserskillrelationExample.Criteria criteria = userskillrelationExample.createCriteria();
                            criteria.andUsernameEqualTo(user2.getUsername());
                            List<Userskillrelation> userskillrelations = userskillrelationMapper.selectByExample(userskillrelationExample);
                            Map<String, Userskillrelation> userskillrelationMap = new HashMap<>();
                            for (Userskillrelation userskillrelation : userskillrelations) {
                                userskillrelationMap.put(userskillrelation.getKeypos(), userskillrelation);
                            }
                            NettyMemory.userskillrelationMap.put(ch, userskillrelationMap);
//                              初始化玩家的技能end
                            NettyMemory.session2UserIds.put(ch, user2);

                            ctx.writeAndFlush(DelimiterUtils.addDelimiter("登录成功，你已进入" + NettyMemory.areaMap.get(user2.getPos()).getName()));
                            NettyMemory.eventStatus.put(ch, EventStatus.STOPAREA);
                        }
                    }
                    break;
                case EventStatus.REGISTER:
                    temp = msg.split("-");
                    if (temp.length != 3) {
                        ctx.writeAndFlush(DelimiterUtils.addDelimiter("输入错误命令"));
                    } else {
                        if (!temp[1].equals(temp[2])) {
                            ctx.writeAndFlush(DelimiterUtils.addDelimiter("两次密码不一致"));
                        } else {
                            User user = new User();
                            user.setUsername(temp[0]);
                            user.setPassword(temp[1]);
                            user.setStatus("1");
                            user.setPos("0");
                            userMapper.insert(user);
                            ctx.writeAndFlush(DelimiterUtils.addDelimiter("注册成功请输入：用户名-密码进行登录"));
                            NettyMemory.eventStatus.put(ch, EventStatus.LOGIN);
                        }
                    }
                    break;
                case EventStatus.STOPAREA:
                    if (msg.startsWith("move")) {
                        temp = msg.split(" ");
                        User user = NettyMemory.session2UserIds.get(ch);
                        if (temp[1].equals(NettyMemory.areaMap.get(user.getPos()).getName())) {
                            ctx.writeAndFlush(DelimiterUtils.addDelimiter("原地无需移动"));
                        } else {
                            if (!NettyMemory.areaSet.contains(temp[1])) {
                                ctx.writeAndFlush(DelimiterUtils.addDelimiter("移动地点不存在"));
                            } else {
                                if (NettyMemory.areaMap.get(user.getPos()).getAreaSet().contains(temp[1])) {
                                    user.setPos(NettyMemory.areaToNum.get(temp[1]));
                                    userMapper.updateByPrimaryKeySelective(user);
                                    NettyMemory.session2UserIds.put(ch, user);
                                    ctx.writeAndFlush(DelimiterUtils.addDelimiter("已移动到" + temp[1]));
                                } else {
                                    ctx.writeAndFlush(DelimiterUtils.addDelimiter("请充值才能启用传送门"));
                                }
                            }
                        }
                    } else if (msg.startsWith("aoi")) {
                        User user = NettyMemory.session2UserIds.get(ch);
                        String allStatus = "玩家" + user.getUsername()
                                + "--------玩家的状态" + user.getStatus()
                                + "--------处于" + NettyMemory.areaMap.get(user.getPos()).getName()
                                + "--------玩家的HP量：" + user.getHp()
                                + "--------玩家的MP量：" + user.getMp()
                                + System.getProperty("line.separator");
                        for (Monster monster : NettyMemory.areaMap.get(user.getPos()).getMonsters()) {
                            allStatus += "怪物：" + monster.getName() + "的血量为：" + monster.getValueOfLife() + System.getProperty("line.separator");
                        }
                        for (Entry<Channel, User> entry : NettyMemory.session2UserIds.entrySet()) {
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
                        ctx.writeAndFlush(DelimiterUtils.addDelimiter(allStatus));
                    } else if (msg.startsWith("talk")) {
                        temp = msg.split("-");
                        if (temp.length != 2) {
                            ctx.writeAndFlush(DelimiterUtils.addDelimiter("输入错误命令"));
                        } else {
                            List<NPC> npcs = NettyMemory.areaMap.get(NettyMemory.session2UserIds.get(ch).getPos())
                                    .getNpcs();
                            for (NPC npc : npcs) {
                                if (npc.getName().split("-")[1].equals(temp[1])) {
                                    ctx.writeAndFlush(DelimiterUtils.addDelimiter(npc.getTalks().get(0)));
                                    break;
                                }
                            }
                            ctx.writeAndFlush(DelimiterUtils.addDelimiter("找不到此NPC"));
                        }
                    } else if (msg.equals("skillCheckout")) {
                        NettyMemory.eventStatus.put(ch, EventStatus.SKILLMANAGER);
                        ctx.writeAndFlush(DelimiterUtils.addDelimiter("请输入lookSkill查看技能，请输入change-技能名-键位配置技能"));
                    } else if (msg.startsWith("attack")) {
                        temp = msg.split("-");
//                        输入的键位是否存在
                        if (temp.length == 3 && NettyMemory.userskillrelationMap.get(ch).containsKey(temp[2])) {
                            User user = NettyMemory.session2UserIds.get(ch);
                            for (Monster monster : NettyMemory.areaMap.get(user.getPos()).getMonsters()) {
//                                输入的怪物是否存在
                                if (monster.getName().equals(temp[1])) {
                                    Userskillrelation userskillrelation = NettyMemory.userskillrelationMap.get(ch).get(temp[2]);
                                    UserSkill userSkill = NettyMemory.SkillMap.get(userskillrelation.getSkillid());
//                                    判断人物MP量是否足够
                                    BigInteger userMp = new BigInteger(user.getMp());
                                    BigInteger skillMp = new BigInteger(userSkill.getSkillMp());
                                    if (userMp.compareTo(skillMp) > 0) {
                                        userMp = userMp.subtract(skillMp);
                                        user.setMp(userMp.toString());
//                                    判断技能冷却
                                        if (System.currentTimeMillis() > userskillrelation.getSkillcds() + userSkill.getAttackCd()) {
                                            Map<String, Userskillrelation> map = NettyMemory.userskillrelationMap.get(ch);
//                                    切换到攻击模式
                                            NettyMemory.eventStatus.put(ch, EventStatus.ATTACK);
//                                    怪物掉血
                                            BigInteger attackDamage = new BigInteger(userSkill.getDamage());
                                            BigInteger monsterLife = new BigInteger(monster.getValueOfLife());
                                            monsterLife = monsterLife.subtract(attackDamage);
                                            monster.setValueOfLife(monsterLife.toString());
                                            String resp = System.getProperty("line.separator")
                                                    + "[技能]:" + userSkill.getSkillName()
                                                    + System.getProperty("line.separator")
                                                    + "对[" + monster.getName()
                                                    + "]造成了" + userSkill.getDamage() + "点伤害"
                                                    + System.getProperty("line.separator")
                                                    + "[怪物血量]:" + monster.getValueOfLife()
                                                    + System.getProperty("line.separator")
                                                    + "[消耗蓝量]:" + user.getMp()
                                                    + System.getProperty("line.separator");
                                            ctx.writeAndFlush(DelimiterUtils.addDelimiter(resp));

                                            //TODO:更新数据库人物技能蓝量
//                                    刷新技能时间
                                            userskillrelation.setSkillcds(System.currentTimeMillis());
//                                    人物掉血
                                            Timer timer = new Timer();
                                            //前一次执行程序结束后 2000ms 后开始执行下一次程序
                                            timer.schedule(new TimerTask() {
                                                public void run() {
                                                    BigInteger userMp = new BigInteger(user.getMp());
                                                    BigInteger monsterDamage = new BigInteger(monster.getMonsterSkillList().get(0).getDamage());
                                                    userMp = userMp.subtract(monsterDamage);
                                                    String resp = "怪物名称:" + monster.getName()
                                                            + "-----怪物技能:" + monster.getMonsterSkillList().get(0).getSkillName()
                                                            + "-----怪物的伤害:" + monster.getMonsterSkillList().get(0).getDamage()
                                                            + "-----你的剩余血:" + userMp.toString()
                                                            + System.getProperty("line.separator");
                                                    User user = NettyMemory.session2UserIds.get(ch);
                                                    user.setMp(userMp.toString());
                                                    NettyMemory.session2UserIds.put(ch, user);
                                                    //TODO:更新用户血量到数据库
                                                    ctx.writeAndFlush(DelimiterUtils.addDelimiter(resp));
                                                    NettyMemory.eventStatus.put(ch, EventStatus.ATTACK);

                                                }
                                            }, 0, 2000);
                                            NettyMemory.channelTimerMap.put(ch, timer);
//                                          提醒用户你已进入战斗模式
                                            ctx.writeAndFlush(DelimiterUtils.addDelimiter("你已经进入战斗模式"));
                                        }
                                    } else {
                                        ctx.writeAndFlush(DelimiterUtils.addDelimiter("人物MP值不足"));
                                    }
                                    break;
                                }
                            }
                        }
                    } else {
                        ctx.writeAndFlush(DelimiterUtils.addDelimiter("请输入有效指令"));
                    }
                    break;
                case EventStatus.SKILLMANAGER:
                    if (msg.equals("lookSkill")) {
                        String skillLook = "";
                        ctx.writeAndFlush(DelimiterUtils.addDelimiter(skillLook));
                        Map<String, Userskillrelation> map = NettyMemory.userskillrelationMap.get(ch);
                        for (Map.Entry<String, Userskillrelation> entry : map.entrySet()) {
                            UserSkill userSkill = NettyMemory.SkillMap.get(entry.getValue().getSkillid());
                            skillLook += "键位:" + entry.getKey()
                                    + "----技能名称:" + userSkill.getSkillName()
                                    + "----技能伤害:" + userSkill.getDamage()
                                    + "----技能cd:" + userSkill.getAttackCd()
                                    + System.getProperty("line.separator");
                        }
                        ctx.writeAndFlush(DelimiterUtils.addDelimiter(skillLook));
                    } else if (msg.startsWith("change")) {
                        temp = msg.split("-");
                        if (temp.length == 3) {
                            boolean flag = false;
                            Map<String, Userskillrelation> map = NettyMemory.userskillrelationMap.get(ch);
                            for (Map.Entry<String, Userskillrelation> entry : map.entrySet()) {
                                UserSkill userSkill = NettyMemory.SkillMap.get(entry.getValue().getSkillid());
                                if (userSkill.getSkillName().equals(temp[1])) {
                                    flag = true;
                                    Userskillrelation userskillrelation = entry.getValue();
                                    map.remove(entry.getKey());
                                    map.put(temp[2], userskillrelation);
//                                    更新session
                                    User user = NettyMemory.session2UserIds.get(ch);
//                                    更新数据库
                                    UserskillrelationExample userskillrelationExample = new UserskillrelationExample();
                                    UserskillrelationExample.Criteria criteria = userskillrelationExample.createCriteria();
                                    criteria.andUsernameEqualTo(user.getUsername());
                                    criteria.andSkillidEqualTo(userSkill.getSkillId());
                                    List<Userskillrelation> userskillrelations = userskillrelationMapper.selectByExample(userskillrelationExample);
                                    userskillrelations.get(0).setKeypos(temp[2]);
                                    userskillrelationMapper.updateByExample(userskillrelations.get(0), userskillrelationExample);
                                    break;
                                }
                            }
                            if (!flag) {
                                ctx.writeAndFlush(DelimiterUtils.addDelimiter("请输入有效指令"));
                            }
                        }
                    } else if (msg.equals("quitSkill")) {
                        User user = NettyMemory.session2UserIds.get(ch);
                        ctx.writeAndFlush(DelimiterUtils.addDelimiter("您已退出技能管理模块，进入" + NettyMemory.areaMap.get(user.getPos()).getName()));
                        NettyMemory.eventStatus.put(ch, EventStatus.STOPAREA);
                    } else {
                        ctx.writeAndFlush(DelimiterUtils.addDelimiter("请输入有效指令"));
                    }
                    break;
                case EventStatus.ATTACK:
                    if (msg.equals("q")) {
                        NettyMemory.channelTimerMap.get(ch).cancel();
                        NettyMemory.channelTimerMap.remove(ch);
                        ctx.writeAndFlush(DelimiterUtils.addDelimiter("退出战斗"));
                    } else {
                        if (NettyMemory.userskillrelationMap.get(ch).containsKey(msg)) {
                            User user = NettyMemory.session2UserIds.get(ch);
                            for (Monster monster : NettyMemory.areaMap.get(user.getPos()).getMonsters()) {
                                UserSkill userSkill = NettyMemory.SkillMap.get(NettyMemory.userskillrelationMap.get(ch).get(msg).getSkillid());
                                Userskillrelation userskillrelation = NettyMemory.userskillrelationMap.get(ch).get(msg);
//                              技能CD检查
                                if (System.currentTimeMillis() > userskillrelation.getSkillcds() + userSkill.getAttackCd()) {
//                                    人物蓝量检查
                                    BigInteger userMp = new BigInteger(user.getMp());
                                    BigInteger skillMp = new BigInteger(userSkill.getSkillMp());
                                    if (userMp.compareTo(skillMp) > 0) {
//                              攻击逻辑
                                        BigInteger attackDamage = new BigInteger(userSkill.getDamage());
                                        BigInteger monsterLife = new BigInteger(monster.getValueOfLife());
                                        monsterLife = monsterLife.subtract(attackDamage);
                                        monster.setValueOfLife(monsterLife.toString());
                                        String resp =
                                                System.getProperty("line.separator")
//                                                        + "[" + userSkill.getSkillName()
//                                                        + "]技能对" + monster.getName()
//                                                        + "造成了" + userSkill.getDamage() + "点伤害"
//                                                        + System.getProperty("line.separator")
//                                                        + "[消耗MP量:]" + userSkill.getSkillMp()
//                                                        + System.getProperty("line.separator")
//                                                        + " [当前MP值:]" + user.getMp()
                                                        + System.getProperty("line.separator")
                                                        + "怪物剩余血量:" + monster.getValueOfLife();
                                        userskillrelation.setSkillcds(System.currentTimeMillis());
                                        //TODO:数据库更新技能时间
                                        ctx.writeAndFlush(DelimiterUtils.addDelimiter(resp));
                                    } else {
                                        ctx.writeAndFlush(DelimiterUtils.addDelimiter("技能蓝量不足"));
                                    }
                                } else {
                                    ctx.writeAndFlush(DelimiterUtils.addDelimiter("技能冷却中，不要做任何操作"));
                                }
                            }
                        }
                    }
                    break;
            }
        }
    }
}
