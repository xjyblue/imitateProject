package service.attackservice.util;

import core.packet.ServerPacket;
import service.sceneservice.entity.BossScene;
import core.component.monster.Monster;
import core.channel.ChannelStatus;
import service.rewardservice.service.RewardService;
import io.netty.channel.Channel;
import pojo.User;
import service.teamservice.entity.Team;
import service.teamservice.entity.TeamCache;
import utils.ChannelUtil;
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
            Channel channelT = ChannelUtil.userToChannelMap.get(entry.getValue());
            if (!ChannelUtil.channelStatus.get(channelT).equals(ChannelStatus.DEADSCENE)) {
                ChannelUtil.channelStatus.put(channelT, ChannelStatus.ATTACK);
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
        Team team = TeamCache.teamMap.get(user.getTeamId());
        for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
            Channel channelTemp = ChannelUtil.userToChannelMap.get(entry.getValue());
            rewardService.getGoods(channelTemp, monster);
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData("玩家" + user.getUsername() + "击杀了：" + monster.getName());
            MessageUtil.sendMessage(channelTemp, builder.build());
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
