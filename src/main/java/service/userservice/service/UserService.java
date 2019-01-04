package service.userservice.service;

import core.component.monster.Monster;
import service.npcservice.entity.Npc;
import io.netty.channel.Channel;
import core.context.ProjectContext;
import service.levelservice.service.LevelService;
import mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import utils.MessageUtil;
import utils.SpringContextUtil;

import java.util.Map;

/**
 * @ClassName UserService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
public class UserService {
    @Autowired
    private LevelService levelService;

    /**
     * aoi 方法
     * @param channel
     * @param msg
     */
    public void aoiMethod(Channel channel, String msg) {
        User user = ProjectContext.session2UserIds.get(channel);
        String allStatus = System.getProperty("line.separator")
                + "玩家[" + user.getUsername()
                + "] 玩家的状态[" + user.getStatus()
                + "] 玩家的经验[" + user.getExperience()
                + "] 玩家的等级[" + levelService.getLevelByExperience(user.getExperience())
                + "] 处于[" + ProjectContext.sceneMap.get(user.getPos()).getName()
                + "] 玩家的HP量：[" + user.getHp()
                + "] 玩家的HP上限 [" + levelService.getMaxHp(user)
                + "] 玩家的MP量：[" + user.getMp()
                + "] 玩家的MP上限: [" + levelService.getMaxMp(user)
                + "] 玩家的金币：[" + user.getMoney()
                + "]" + System.getProperty("line.separator");
        for (Map.Entry<Channel, User> entry : ProjectContext.session2UserIds.entrySet()) {
            if (!user.getUsername().equals(entry.getValue().getUsername()) && user.getPos().equals(entry.getValue().getPos())) {
                allStatus += "其他玩家" + entry.getValue().getUsername() + "---" + entry.getValue().getStatus() + System.getProperty("line.separator");
            }
        }
        for (Npc npc : ProjectContext.sceneMap.get(user.getPos()).getNpcs()) {
            allStatus += "Npc:" + npc.getName() + " 状态[" + npc.getStatus() + "]" + System.getProperty("line.separator");
        }
        for (Monster monster : ProjectContext.sceneMap.get(user.getPos()).getMonsters()) {
            allStatus += "怪物有" + monster.getName() + " 生命值[" + monster.getValueOfLife()
                    + "] 攻击技能为[" + monster.getMonsterSkillList().get(0).getSkillName()
                    + "] 伤害为：[" + monster.getMonsterSkillList().get(0).getDamage() + "]";
        }
        channel.writeAndFlush(MessageUtil.turnToPacket(allStatus));
    }

    /**
     * 根据用户名获取用户
     * @param s
     * @return
     */
    public User getUserByNameFromSession(String s) {
        for (Map.Entry<Channel, User> entry : ProjectContext.session2UserIds.entrySet()) {
            if (entry.getValue().getUsername().equals(s)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public void recoverUser(User user) {
        int userHp = Integer.parseInt(levelService.getMaxHp(user));
        int userMp = Integer.parseInt(levelService.getMaxMp(user));
        user.setHp(userHp + "");
        user.setMp(userMp + "");
//      更新用户血量和mp
        UserMapper userMapper = SpringContextUtil.getBean("userMapper");
        userMapper.updateByPrimaryKeySelective(user);
    }

}
