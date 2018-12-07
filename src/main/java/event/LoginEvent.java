package event;

import buff.Buff;
import config.BuffConfig;
import config.MessageConfig;
import io.netty.channel.Channel;
import mapper.UserMapper;
import mapper.UserskillrelationMapper;
import memory.NettyMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import packet.PacketType;
import pojo.*;
import role.Role;
import skill.UserSkill;
import utils.MessageUtil;

import java.util.*;

@Component("loginEvent")
public class LoginEvent {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserskillrelationMapper userskillrelationMapper;

    public void login(Channel channel, String msg) {
        String temp[] = msg.split("-");
        if (temp.length != 2) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
        } else {
            User user = userMapper.getUser(temp[0],temp[1]);
            if (user == null) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORPASSWORD));
            } else {
//             解决玩家断线重连

//             初始化玩家的技能start
                UserskillrelationExample userskillrelationExample = new UserskillrelationExample();
                UserskillrelationExample.Criteria criteria = userskillrelationExample.createCriteria();
                criteria.andUsernameEqualTo(user.getUsername());
                List<Userskillrelation> userskillrelations = userskillrelationMapper.selectByExample(userskillrelationExample);
                Map<String, Userskillrelation> userskillrelationMap = new HashMap<>();
                String skillLook = "";
                for (Userskillrelation userskillrelation : userskillrelations) {
                    UserSkill userSkill = NettyMemory.SkillMap.get(userskillrelation.getSkillid());
                    userskillrelationMap.put(userskillrelation.getKeypos(), userskillrelation);
                    skillLook += "[键位-"+ userskillrelation.getKeypos()+"-技能名称-"+userSkill.getSkillName()+"-技能伤害-"+userSkill.getDamage()+"技能cd"+userSkill.getAttackCd()+"] ";
                }
                NettyMemory.userskillrelationMap.put(channel, userskillrelationMap);

//                channel.writeAndFlush(MessageUtil.turnToPacket(skillLook,PacketType.USERINFO));

//                初始化玩家的各种buffer
                Map<String,Integer> map = new HashMap<>();
                map.put(BuffConfig.MPBUFF,1000);
                map.put(BuffConfig.POISONINGBUFF,2000);
                map.put(BuffConfig.DEFENSEBUFF,3000);
                map.put(BuffConfig.SLEEPBUFF,5000);
                map.put(BuffConfig.TREATMENTBUFF,6000);
                map.put(BuffConfig.ALLPERSON,4000);
                map.put(BuffConfig.BABYBUF,7000);
                user.setBuffMap(map);
//              初始化每个用户buff的终止时间
                Map<String,Long> mapSecond = new HashMap<>();
                mapSecond.put(BuffConfig.MPBUFF,1000l);
                mapSecond.put(BuffConfig.POISONINGBUFF,2000l);
                mapSecond.put(BuffConfig.DEFENSEBUFF,3000l);
                mapSecond.put(BuffConfig.SLEEPBUFF,1000l);
                mapSecond.put(BuffConfig.TREATMENTBUFF,1000l);
                mapSecond.put(BuffConfig.ALLPERSON,1000l);
                mapSecond.put(BuffConfig.BABYBUF,1000l);

                if(!NettyMemory.userBuffEndTime.containsKey(user)){
                    NettyMemory.userBuffEndTime.put(user,mapSecond);
                }
//                初始化玩家的技能end
                NettyMemory.session2UserIds.put(channel, user);
                NettyMemory.userToChannelMap.put(user,channel);
                channel.writeAndFlush(MessageUtil.turnToPacket("登录成功，你已进入" + NettyMemory.areaMap.get(user.getPos()).getName()));
                Role role = NettyMemory.roleMap.get(user.getRoleid());
                channel.writeAndFlush(MessageUtil.turnToPacket("   "+user.getUsername()+"    职业为:"+role.getName()+"] "+skillLook, PacketType.USERINFO));
                NettyMemory.eventStatus.put(channel, EventStatus.STOPAREA);
            }
        }
    }

}
