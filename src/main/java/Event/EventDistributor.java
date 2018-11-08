package Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                            UserskillrelationExample userskillrelationExample = new UserskillrelationExample();
                            UserskillrelationExample.Criteria criteria = userskillrelationExample.createCriteria();
                            criteria.andUsernameEqualTo(user2.getUsername());
                            List<Userskillrelation> userskillrelations = userskillrelationMapper.selectByExample(userskillrelationExample);
                            Map<String, UserSkill> userSkillMap = new HashMap<String, UserSkill>();
                            for (Userskillrelation userskillrelation : userskillrelations) {
                                userSkillMap.put(userskillrelation.getKeypos(), NettyMemory.userSkillMap.get(userskillrelation.getSkillid()));
                            }
                            user2.setMap(userSkillMap);
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
                    } else if(msg.startsWith("attack")) {
                        temp = msg.split("-");
//                        if(temp[1].equals(NettyMemory.))
                    }else {
                        ctx.writeAndFlush(DelimiterUtils.addDelimiter("请输入有效指令"));
                    }
                    break;
                case EventStatus.SKILLMANAGER:
                    if (msg.equals("lookSkill")) {
                        String skillLook = "";
                        Map<String, UserSkill> userSkillMap = NettyMemory.session2UserIds.get(ch).getMap();
                        for (Map.Entry<String, UserSkill> entry : userSkillMap.entrySet()) {
                            skillLook += "键位:" + entry.getKey()
                                    + "----技能名称:" + entry.getValue().getSkillName()
                                    + "----技能伤害:" + entry.getValue().getDamage()
                                    + "----技能cd:" + entry.getValue().getAttackCd()
                                    + System.getProperty("line.separator");
                        }
                        ctx.writeAndFlush(DelimiterUtils.addDelimiter(skillLook));
                    } else if (msg.startsWith("change")) {
                        temp = msg.split("-");
                        if (temp.length == 3) {
                            boolean flag = false;
                            Map<String, UserSkill> userSkillMap = NettyMemory.session2UserIds.get(ch).getMap();
                            for (Map.Entry<String, UserSkill> entry : userSkillMap.entrySet()) {
                                if (entry.getValue().getSkillName().equals(temp[1])) {
                                    flag = true;
                                    UserSkill tempSkill = entry.getValue();
                                    userSkillMap.remove(entry.getKey());
                                    userSkillMap.put(temp[2], tempSkill);
//                                    更新session
                                    User user = NettyMemory.session2UserIds.get(ch);
                                    user.setMap(userSkillMap);
                                    NettyMemory.session2UserIds.put(ch, user);
//                                    更新数据库
                                    UserskillrelationExample userskillrelationExample = new UserskillrelationExample();
                                    UserskillrelationExample.Criteria criteria = userskillrelationExample.createCriteria();
                                    criteria.andUsernameEqualTo(user.getUsername());
                                    criteria.andSkillidEqualTo(tempSkill.getSkillId());
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
            }
        }
    }
}
