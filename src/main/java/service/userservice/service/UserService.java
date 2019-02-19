package service.userservice.service;

import com.google.common.collect.Maps;
import config.impl.excel.SceneResourceLoad;
import core.annotation.order.OrderRegion;
import core.channel.ChannelStatus;
import core.annotation.order.Order;
import core.component.monster.Monster;
import core.config.OrderConfig;
import core.packet.ServerPacket;
import service.buffservice.entity.BuffConstant;
import service.npcservice.entity.Npc;
import io.netty.channel.Channel;
import service.levelservice.service.LevelService;
import mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import utils.ChannelUtil;
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
@OrderRegion
public class UserService {
    @Autowired
    private LevelService levelService;

    /**
     * aoi 方法
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.AOI_METHOD_ORDER, status = {ChannelStatus.COMMONSCENE})
    public void aoiMethod(Channel channel, String msg) {
        User user = ChannelUtil.channelToUserMap.get(channel);
        String allStatus = System.getProperty("line.separator")
                + "玩家[" + user.getUsername()
                + "] 玩家的状态[" + user.getStatus()
                + "] 玩家的经验[" + user.getExperience()
                + "] 玩家的等级[" + levelService.getLevelByExperience(user.getExperience())
                + "] 处于[" + SceneResourceLoad.sceneMap.get(user.getPos()).getName()
                + "] 玩家的HP量：[" + user.getHp()
                + "] 玩家的HP上限 [" + levelService.getMaxHp(user)
                + "] 玩家的MP量：[" + user.getMp()
                + "] 玩家的MP上限: [" + levelService.getMaxMp(user)
                + "] 玩家的金币：[" + user.getMoney()
                + "]" + System.getProperty("line.separator");
        for (Map.Entry<Channel, User> entry : ChannelUtil.channelToUserMap.entrySet()) {
            if (!user.getUsername().equals(entry.getValue().getUsername()) && user.getPos().equals(entry.getValue().getPos())) {
                allStatus += "其他玩家" + entry.getValue().getUsername() + "---" + entry.getValue().getStatus() + System.getProperty("line.separator");
            }
        }
        for (Npc npc : SceneResourceLoad.sceneMap.get(user.getPos()).getNpcs()) {
            allStatus += "Npc:" + npc.getName() + " 状态[" + npc.getStatus() + "]" + System.getProperty("line.separator");
        }
        for (Monster monster : SceneResourceLoad.sceneMap.get(user.getPos()).getMonsters()) {
            allStatus += "怪物有" + monster.getName() + " 生命值[" + monster.getValueOfLife()
                    + "] 攻击技能为[" + monster.getMonsterSkillList().get(0).getSkillName()
                    + "] 伤害为：[" + monster.getMonsterSkillList().get(0).getDamage() + "]";
        }
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData(allStatus);
        MessageUtil.sendMessage(channel,builder.build());
    }

    /**
     * 根据用户名获取用户
     *
     * @param s
     * @return
     */
    public User getUserByNameFromSession(String s) {
        for (Map.Entry<Channel, User> entry : ChannelUtil.channelToUserMap.entrySet()) {
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

    public void sendMessageByUserName(String username, String msg) {
        sendMessageByUserName(username, msg, null);
    }

    public void sendMessageByUserName(String username, String msg, String type) {
        for (Map.Entry<User, Channel> entry : ChannelUtil.userToChannelMap.entrySet()) {
            if (entry.getKey().getUsername().equals(username)) {
                Channel channel = entry.getValue();
                if (type == null) {
                    ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                    builder.setData(msg);
                    MessageUtil.sendMessage(channel,builder.build());
                } else {
                    //TODO:没了分类
//                    channel.writeAndFlush(MessageUtil.turnToPacket(msg, type));
                }
            }
        }
    }

    /**
     * 初始人物buff,玩家buff恢复默认值
     *
     * @param user
     */
    public void initUserBuff(User user) {
//     第一次登陆
        if (user.getBuffMap() == null) {
            Map<String, Integer> map = Maps.newConcurrentMap();
            map.put(BuffConstant.MPBUFF, 1000);
            map.put(BuffConstant.POISONINGBUFF, 2000);
            map.put(BuffConstant.DEFENSEBUFF, 3000);
            map.put(BuffConstant.SLEEPBUFF, 5000);
            map.put(BuffConstant.TREATMENTBUFF, 6000);
            map.put(BuffConstant.ALLPERSON, 4000);
            map.put(BuffConstant.BABYBUF, 7000);
            map.put(BuffConstant.TAUNTBUFF, 9000);
            user.setBuffMap(map);
            Map<String, Long> mapSecond = Maps.newConcurrentMap();
            mapSecond.put(BuffConstant.MPBUFF, 1000L);
            mapSecond.put(BuffConstant.POISONINGBUFF, 2000L);
            mapSecond.put(BuffConstant.DEFENSEBUFF, 3000L);
            mapSecond.put(BuffConstant.SLEEPBUFF, 1000L);
            mapSecond.put(BuffConstant.TREATMENTBUFF, 1000L);
            mapSecond.put(BuffConstant.ALLPERSON, 1000L);
            mapSecond.put(BuffConstant.BABYBUF, 1000L);
            mapSecond.put(BuffConstant.TAUNTBUFF, 1000L);
            user.setUserBuffEndTimeMap(mapSecond);
        } else {
//         后续初始化buff
            user.getBuffMap().put(BuffConstant.MPBUFF, 1000);
            user.getBuffMap().put(BuffConstant.POISONINGBUFF, 2000);
            user.getBuffMap().put(BuffConstant.DEFENSEBUFF, 3000);
            user.getBuffMap().put(BuffConstant.SLEEPBUFF, 5000);
            user.getBuffMap().put(BuffConstant.TREATMENTBUFF, 6000);
            user.getBuffMap().put(BuffConstant.ALLPERSON, 4000);
            user.getBuffMap().put(BuffConstant.BABYBUF, 7000);
            user.getBuffMap().put(BuffConstant.TAUNTBUFF, 9000);
//          时间
            user.getUserBuffEndTimeMap().put(BuffConstant.MPBUFF, 1000L);
            user.getUserBuffEndTimeMap().put(BuffConstant.POISONINGBUFF, 2000L);
            user.getUserBuffEndTimeMap().put(BuffConstant.DEFENSEBUFF, 3000L);
            user.getUserBuffEndTimeMap().put(BuffConstant.SLEEPBUFF, 1000L);
            user.getUserBuffEndTimeMap().put(BuffConstant.TREATMENTBUFF, 1000L);
            user.getUserBuffEndTimeMap().put(BuffConstant.ALLPERSON, 1000L);
            user.getUserBuffEndTimeMap().put(BuffConstant.BABYBUF, 1000L);
            user.getUserBuffEndTimeMap().put(BuffConstant.TAUNTBUFF, 1000L);
        }
//      初始化每个用户buff的终止时间
    }
}
