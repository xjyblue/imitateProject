package service.attackservice.util;

import component.scene.BossScene;
import component.monster.Monster;
import service.buffservice.entity.BuffConstant;
import event.EventStatus;
import service.rewardservice.service.RewardService;
import io.netty.channel.Channel;
import context.ProjectContext;
import pojo.User;
import service.teamservice.entity.Team;
import utils.MessageUtil;
import utils.SpringContextUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/12/6 21:09
 */
public class AttackUtil {

    public static void changeUserAttackMonster(User user, BossScene bossScene, Monster monster) {
//      清除所有玩家对刚刚boss的战斗，如果某些玩家在战斗的话
        if(ProjectContext.userToMonsterMap.containsKey(user)){
            ProjectContext.userToMonsterMap.get(user).remove(monster.getId());
        }
//      将所有用户弄为战斗状态
        for (Map.Entry<String, User> entry : bossScene.getUserMap().entrySet()) {
            Channel channelT = ProjectContext.userToChannelMap.get(entry.getValue());
            if(!ProjectContext.eventStatus.get(channelT).equals(EventStatus.DEADAREA)){
                ProjectContext.eventStatus.put(channelT,EventStatus.ATTACK);
            }
        }

//      刷新所有玩家的buff
//      更新用户buff初始值
        Map<String, Integer> map = new HashMap<>();
        map.put(BuffConstant.MPBUFF, 1000);
        map.put(BuffConstant.POISONINGBUFF, 2000);
        map.put(BuffConstant.DEFENSEBUFF, 3000);
        map.put(BuffConstant.SLEEPBUFF, 5000);
        map.put(BuffConstant.TREATMENTBUFF, 6000);
        map.put(BuffConstant.ALLPERSON, 4000);
        map.put(BuffConstant.BABYBUF, 7000);
        user.setBuffMap(map);
//      buff终止时间
        Map<String, Long> mapSecond = new HashMap<>();
        mapSecond.put(BuffConstant.MPBUFF, 1000l);
        mapSecond.put(BuffConstant.POISONINGBUFF, 2000l);
        mapSecond.put(BuffConstant.DEFENSEBUFF, 3000l);
        mapSecond.put(BuffConstant.SLEEPBUFF, 1000l);
        mapSecond.put(BuffConstant.TREATMENTBUFF, 1000l);
        mapSecond.put(BuffConstant.ALLPERSON, 1000l);
        mapSecond.put(BuffConstant.BABYBUF, 1000l);
        ProjectContext.userBuffEndTime.put(user, mapSecond);
    }

    public static void killBossMessageToAll(User user, Monster monster) {
        RewardService rewardService = SpringContextUtil.getBean("rewardService");
        Team team = ProjectContext.teamMap.get(user.getTeamId());
        for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
            Channel channelTemp = ProjectContext.userToChannelMap.get(entry.getValue());
            rewardService.getGoods(channelTemp, monster);
            channelTemp.writeAndFlush(MessageUtil.turnToPacket("玩家" + user.getUsername() + "击杀了：" + monster.getName()));
        }
    }

    public static void addMonsterToUserMonsterList(User user, Monster monster) {
        if (ProjectContext.userToMonsterMap.containsKey(user)) {
            ProjectContext.userToMonsterMap.get(user).put(monster.getId(), monster);
        } else {
            Map<Integer, Monster> map = new HashMap<>();
            map.put(monster.getId(), monster);
            ProjectContext.userToMonsterMap.put(user, map);
        }
    }
}
