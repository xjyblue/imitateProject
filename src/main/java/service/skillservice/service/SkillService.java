package service.skillservice.service;

import core.config.MessageConfig;
import core.ChannelStatus;
import io.netty.channel.Channel;
import mapper.UserskillrelationMapper;
import core.context.ProjectContext;
import order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userskillrelation;
import pojo.UserskillrelationExample;
import service.skillservice.entity.UserSkill;
import utils.MessageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户技能管理服务
 */
@Component
public class SkillService {
    @Autowired
    private UserskillrelationMapper userskillrelationMapper;

    //  查看用户技能
    @Order(orderMsg = "lookSkill")
    public void lookSkill(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        String skillLook = "";
        channel.writeAndFlush(MessageUtil.turnToPacket(skillLook));
        Map<String, Userskillrelation> map = ProjectContext.userskillrelationMap.get(user);
        for (Map.Entry<String, Userskillrelation> entry : map.entrySet()) {
            UserSkill userSkill = ProjectContext.skillMap.get(entry.getValue().getSkillid());
            skillLook += "键位:" + entry.getKey()
                    + "----技能名称:" + userSkill.getSkillName()
                    + "----技能伤害:" + userSkill.getDamage()
                    + "----技能cd:" + userSkill.getAttackCd()
                    + System.getProperty("line.separator");
        }
        channel.writeAndFlush(MessageUtil.turnToPacket(skillLook));
    }

    //  改变用户技能键位
    @Order(orderMsg = "change")
    public void changeSkill(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        String[] temp = msg.split("-");
        if (temp.length == 3) {
            boolean flag = false;
            Map<String, Userskillrelation> map = ProjectContext.userskillrelationMap.get(user);
            for (Map.Entry<String, Userskillrelation> entry : map.entrySet()) {
                UserSkill userSkill = ProjectContext.skillMap.get(entry.getValue().getSkillid());
                if (userSkill.getSkillName().equals(temp[1])) {
                    flag = true;
                    Userskillrelation userskillrelation = entry.getValue();
                    map.remove(entry.getKey());
                    map.put(temp[2], userskillrelation);
//                                    更新session
//                                    更新数据库
                    UserskillrelationExample userskillrelationExample = new UserskillrelationExample();
                    UserskillrelationExample.Criteria criteria = userskillrelationExample.createCriteria();
                    criteria.andUsernameEqualTo(user.getUsername());
                    criteria.andSkillidEqualTo(userSkill.getSkillId());
                    List<Userskillrelation> userskillrelations = userskillrelationMapper.selectByExample(userskillrelationExample);
                    userskillrelations.get(0).setKeypos(temp[2]);
                    userskillrelationMapper.updateByExample(userskillrelations.get(0), userskillrelationExample);
                    channel.writeAndFlush(MessageUtil.turnToPacket("键位更换成功"));
                    break;
                }
            }
            if (!flag) {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            }
        }
    }

    //  退出用户技能管理
    @Order(orderMsg = "quitSkill")
    public void quitSkill(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        channel.writeAndFlush(MessageUtil.turnToPacket("您已退出技能管理模块，进入" + ProjectContext.sceneMap.get(user.getPos()).getName()));
        ProjectContext.eventStatus.put(channel, ChannelStatus.COMMONSCENE);
    }

    public void enterSkillView(Channel channel, String msg) {
        ProjectContext.eventStatus.put(channel, ChannelStatus.SKILLVIEW);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.SKILLVIEWMESG));
    }

    public UserSkill getUserSkillByKey(Channel channel, String key) {
        User user = ProjectContext.session2UserIds.get(channel);
        if (!ProjectContext.userskillrelationMap.get(user).containsKey(key)) {
            return null;
        }
        return ProjectContext.skillMap.get(ProjectContext.userskillrelationMap.get(user).get(key).getSkillid());
    }

    public List<UserSkill> getUserSkillByUserRole(int roleid) {
        List<UserSkill> list = new ArrayList<>();
        for (Map.Entry<Integer, UserSkill> entry : ProjectContext.skillMap.entrySet()) {
            if (roleid == entry.getValue().getRoleSkill()) {
                list.add(entry.getValue());
            }
        }
        return list;
    }
}
