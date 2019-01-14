package service.attackservice.util;

import service.sceneservice.entity.BossScene;
import core.component.monster.Monster;
import core.channel.ChannelStatus;
import service.rewardservice.service.RewardService;
import io.netty.channel.Channel;
import core.context.ProjectContext;
import pojo.User;
import service.teamservice.entity.Team;
import utils.MessageUtil;
import utils.SpringContextUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
        user.getUserToMonsterMap().remove(monster.getId());
//      将所有用户弄为战斗状态
        for (Map.Entry<String, User> entry : bossScene.getUserMap().entrySet()) {
            Channel channelT = ProjectContext.userToChannelMap.get(entry.getValue());
            if (!ProjectContext.channelStatus.get(channelT).equals(ChannelStatus.DEADSCENE)) {
                ProjectContext.channelStatus.put(channelT, ChannelStatus.ATTACK);
            }
        }
    }

    /**
     * 战胜提示
     *
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
     *
     * @param user
     * @param monster
     */
    public static void addMonsterToUserMonsterList(User user, Monster monster) {
        user.getUserToMonsterMap().put(monster.getId(), monster);
    }

    public static void removeAllMonster(User userTarget) {
        Set<Map.Entry<Integer, Monster>> entries = userTarget.getUserToMonsterMap().entrySet();
        Iterator<Map.Entry<Integer, Monster>> iteratorMap = entries.iterator();
        while (iteratorMap.hasNext()) {
            Map.Entry<Integer, Monster> next = iteratorMap.next();
            userTarget.getUserToMonsterMap().remove(next.getKey());
        }
    }
}
