package service.skillservice.service;

import config.impl.excel.SceneResourceLoad;
import config.impl.excel.UserSkillResourceLoad;
import core.annotation.Region;
import core.config.GrobalConfig;
import core.config.MessageConfig;
import core.channel.ChannelStatus;
import io.netty.channel.Channel;
import mapper.UserskillrelationMapper;
import core.annotation.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userskillrelation;
import pojo.UserskillrelationExample;
import service.skillservice.entity.UserSkill;
import utils.ChannelUtil;
import utils.MessageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName SkillService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
@Region
public class SkillService {
    @Autowired
    private UserskillrelationMapper userskillrelationMapper;

    /**
     * 查看用户技能
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "lookSkill",status = {ChannelStatus.SKILLVIEW})
    public void lookSkill(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        String skillLook = "";
        channel.writeAndFlush(MessageUtil.turnToPacket(skillLook));
        Map<String, Userskillrelation> map = user.getUserskillrelationMap();
        for (Map.Entry<String, Userskillrelation> entry : map.entrySet()) {
            UserSkill userSkill = UserSkillResourceLoad.skillMap.get(entry.getValue().getSkillid());
            skillLook += "键位:" + entry.getKey()
                    + "----技能名称:" + userSkill.getSkillName()
                    + "----技能伤害:" + userSkill.getDamage()
                    + "----技能cd:" + userSkill.getAttackCd()
                    + System.getProperty("line.separator");
        }
        channel.writeAndFlush(MessageUtil.turnToPacket(skillLook));
    }

    /**
     * 改变用户技能键位
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "change",status = {ChannelStatus.SKILLVIEW})
    public void changeSkill(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        String[] temp = msg.split("=");
        if (temp.length == GrobalConfig.THREE) {
            boolean flag = false;
            Map<String, Userskillrelation> map = user.getUserskillrelationMap();
            for (Map.Entry<String, Userskillrelation> entry : map.entrySet()) {
                UserSkill userSkill = UserSkillResourceLoad.skillMap.get(entry.getValue().getSkillid());
                if (userSkill.getSkillName().equals(temp[1])) {
                    flag = true;
                    Userskillrelation userskillrelation = entry.getValue();
                    map.remove(entry.getKey());
                    map.put(temp[2], userskillrelation);
//                  更新session
//                  更新数据库
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

    /**
     * 退出用户技能管理
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "quitSkill",status = {ChannelStatus.SKILLVIEW})
    public void quitSkill(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        channel.writeAndFlush(MessageUtil.turnToPacket("您已退出技能管理模块，进入" + SceneResourceLoad.sceneMap.get(user.getPos()).getName()));
        ChannelUtil.channelStatus.put(channel, ChannelStatus.COMMONSCENE);
    }

    /**
     * 进入技能管理界面
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "viewSkill",status = {ChannelStatus.COMMONSCENE})
    public void enterSkillView(Channel channel, String msg) {
        ChannelUtil.channelStatus.put(channel, ChannelStatus.SKILLVIEW);
        channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.SKILLVIEWMESG));
    }

    /**
     * 根据键位获取用户技能
     *
     * @param channel
     * @param key
     * @return
     */
    public UserSkill getUserSkillByKey(Channel channel, String key) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        if (!user.getUserskillrelationMap().containsKey(key)) {
            return null;
        }
        return UserSkillResourceLoad.skillMap.get(user.getUserskillrelationMap().get(key).getSkillid());
    }

    /**
     * 根据角色获取用户技能
     *
     * @param roleid
     * @return
     */
    public List<UserSkill> getUserSkillByUserRole(int roleid) {
        List<UserSkill> list = new ArrayList<>();
        for (Map.Entry<Integer, UserSkill> entry : UserSkillResourceLoad.skillMap.entrySet()) {
            if (roleid == entry.getValue().getRoleSkill()) {
                list.add(entry.getValue());
            }
        }
        return list;
    }

    /**
     * 刷新用户技能cd
     */
    public void refreshUserSkillCd(UserSkill userSkill, Userskillrelation userskillrelation) {
        userskillrelation.setSkillcds(System.currentTimeMillis() + userSkill.getAttackCd());
    }

    /**
     * 检查用户技能cd
     */
    public boolean checkUserSkillCd(Userskillrelation userskillrelation, Channel channel) {
        if (System.currentTimeMillis() < userskillrelation.getSkillcds()) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNSKILLCD));
            return false;
        }
        return true;
    }
}
