package utils;

import component.BossScene;
import component.Monster;
import config.BuffConfig;
import event.EventStatus;
import event.OutfitEquipmentEvent;
import io.netty.channel.Channel;
import context.ProjectContext;
import pojo.User;
import team.Team;

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
        map.put(BuffConfig.MPBUFF, 1000);
        map.put(BuffConfig.POISONINGBUFF, 2000);
        map.put(BuffConfig.DEFENSEBUFF, 3000);
        map.put(BuffConfig.SLEEPBUFF, 5000);
        map.put(BuffConfig.TREATMENTBUFF, 6000);
        map.put(BuffConfig.ALLPERSON, 4000);
        map.put(BuffConfig.BABYBUF, 7000);
        user.setBuffMap(map);
//      buff终止时间
        Map<String, Long> mapSecond = new HashMap<>();
        mapSecond.put(BuffConfig.MPBUFF, 1000l);
        mapSecond.put(BuffConfig.POISONINGBUFF, 2000l);
        mapSecond.put(BuffConfig.DEFENSEBUFF, 3000l);
        mapSecond.put(BuffConfig.SLEEPBUFF, 1000l);
        mapSecond.put(BuffConfig.TREATMENTBUFF, 1000l);
        mapSecond.put(BuffConfig.ALLPERSON, 1000l);
        mapSecond.put(BuffConfig.BABYBUF, 1000l);
        ProjectContext.userBuffEndTime.put(user, mapSecond);
    }

    public static void killBossMessageToAll(User user, Monster monster) {
        OutfitEquipmentEvent outfitEquipmentEvent = SpringContextUtil.getBean("outfitEquipmentEvent");
        Team team = ProjectContext.teamMap.get(user.getTeamId());
        for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
            Channel channelTemp = ProjectContext.userToChannelMap.get(entry.getValue());
            outfitEquipmentEvent.getGoods(channelTemp, monster);
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
