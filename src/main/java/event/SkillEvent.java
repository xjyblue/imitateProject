package event;

import config.MessageConfig;
import io.netty.channel.Channel;
import mapper.UserskillrelationMapper;
import memory.NettyMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userskillrelation;
import pojo.UserskillrelationExample;
import skill.UserSkill;
import utils.MessageUtil;

import java.util.List;
import java.util.Map;

@Component("skillEvent")
public class SkillEvent {
    @Autowired
    private UserskillrelationMapper userskillrelationMapper;

    public void skill(Channel channel, String msg) {
        String temp[] = null;
        if (msg.equals("lookSkill")) {
            String skillLook = "";
            channel.writeAndFlush(MessageUtil.turnToPacket(skillLook));
            Map<String, Userskillrelation> map = NettyMemory.userskillrelationMap.get(channel);
            for (Map.Entry<String, Userskillrelation> entry : map.entrySet()) {
                UserSkill userSkill = NettyMemory.SkillMap.get(entry.getValue().getSkillid());
                skillLook += "键位:" + entry.getKey()
                        + "----技能名称:" + userSkill.getSkillName()
                        + "----技能伤害:" + userSkill.getDamage()
                        + "----技能cd:" + userSkill.getAttackCd()
                        + System.getProperty("line.separator");
            }
            channel.writeAndFlush(MessageUtil.turnToPacket(skillLook));
        } else if (msg.startsWith("change")) {
            temp = msg.split("-");
            if (temp.length == 3) {
                boolean flag = false;
                Map<String, Userskillrelation> map = NettyMemory.userskillrelationMap.get(channel);
                for (Map.Entry<String, Userskillrelation> entry : map.entrySet()) {
                    UserSkill userSkill = NettyMemory.SkillMap.get(entry.getValue().getSkillid());
                    if (userSkill.getSkillName().equals(temp[1])) {
                        flag = true;
                        Userskillrelation userskillrelation = entry.getValue();
                        map.remove(entry.getKey());
                        map.put(temp[2], userskillrelation);
//                                    更新session
                        User user = NettyMemory.session2UserIds.get(channel);
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
                    channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
                }
            }
        } else if (msg.equals("quitSkill")) {
            User user = NettyMemory.session2UserIds.get(channel);
            channel.writeAndFlush(MessageUtil.turnToPacket("您已退出技能管理模块，进入" + NettyMemory.areaMap.get(user.getPos()).getName()));
            NettyMemory.eventStatus.put(channel, EventStatus.STOPAREA);
        } else {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
        }
    }
}
