package service.attackservice.util;

import service.sceneservice.entity.BossScene;
import core.component.monster.Monster;
import service.buffservice.entity.BuffConstant;
import core.ChannelStatus;
import service.rewardservice.service.RewardService;
import io.netty.channel.Channel;
import core.context.ProjectContext;
import pojo.User;
import service.teamservice.entity.Team;
import utils.MessageUtil;
import utils.SpringContextUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName AttackUtil
 * @Description 战斗系统工具类
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class AttackUtil {

    public static void changeUserAttackMonster(User user, BossScene bossScene, Monster monster) {
//      清除所有玩家对刚刚boss的战斗，如果某些玩家在战斗的话
        if(ProjectContext.userToMonsterMap.containsKey(user)){
            ProjectContext.userToMonsterMap.get(user).remove(monster.getId());
        }
//      将所有用户弄为战斗状态
        for (Map.Entry<String, User> entry : bossScene.getUserMap().entrySet()) {
            Channel channelT = ProjectContext.userToChannelMap.get(entry.getValue());
            if(!ProjectContext.eventStatus.get(channelT).equals(ChannelStatus.DEADSCENE)){
                ProjectContext.eventStatus.put(channelT, ChannelStatus.ATTACK);
            }
        }

//      刷新所有玩家的buff
//      更新用户buff初始值
        Map<String, Integer> map = new HashMap<>(64);
        map.put(BuffConstant.MPBUFF, 1000);
        map.put(BuffConstant.POISONINGBUFF, 2000);
        map.put(BuffConstant.DEFENSEBUFF, 3000);
        map.put(BuffConstant.SLEEPBUFF, 5000);
        map.put(BuffConstant.TREATMENTBUFF, 6000);
        map.put(BuffConstant.ALLPERSON, 4000);
        map.put(BuffConstant.BABYBUF, 7000);
        user.setBuffMap(map);
//      buff终止时间
        Map<String, Long> mapSecond = new HashMap<>(64);
        mapSecond.put(BuffConstant.MPBUFF, 1000L);
        mapSecond.put(BuffConstant.POISONINGBUFF, 2000L);
        mapSecond.put(BuffConstant.DEFENSEBUFF, 3000L);
        mapSecond.put(BuffConstant.SLEEPBUFF, 1000L);
        mapSecond.put(BuffConstant.TREATMENTBUFF, 1000L);
        mapSecond.put(BuffConstant.ALLPERSON, 1000L);
        mapSecond.put(BuffConstant.BABYBUF, 1000L);
        ProjectContext.userBuffEndTime.put(user, mapSecond);
    }

    /**
     * 战胜提示
     * @param user
     * @param monster
     */
    public static void killBossMessageToAll(User user, Monster monster) {
        RewardService rewardService = SpringContextUtil.getBean("rewardService");
        Team team = ProjectContext.teamMap.get(user.getTeamId());
        for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
            Channel channelTemp = ProjectContext.userToChannelMap.get(entry.getValue());
            rewardService.getGoods(channelTemp, monster);
            channelTemp.writeAndFlush(MessageUtil.turnToPacket("玩家" + user.getUsername() + "击杀了：" + monster.getName()));
        }
    }

    /**
     * 为用户添加战斗的怪物
     * @param user
     * @param monster
     */
    public static void addMonsterToUserMonsterList(User user, Monster monster) {
        if (ProjectContext.userToMonsterMap.containsKey(user)) {
            ProjectContext.userToMonsterMap.get(user).put(monster.getId(), monster);
        } else {
            Map<Integer, Monster> map = new HashMap<>(64);
            map.put(monster.getId(), monster);
            ProjectContext.userToMonsterMap.put(user, map);
        }
    }
}
