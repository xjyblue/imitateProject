package utils;

import component.BossArea;
import component.Monster;
import config.DeadOrAliveConfig;
import event.EventStatus;
import io.netty.channel.Channel;
import memory.NettyMemory;
import pojo.User;
import team.Team;

import java.util.Map;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/12/6 21:09
 */
public class AttackUtil {

    public static void changeUserAttackMonster(User user, BossArea bossArea) {
//      移除其他玩家在打该boss
        Monster monster = NettyMemory.monsterMap.get(user).get(0);
        for(Map.Entry<String,User>entry:NettyMemory.teamMap.get(user.getTeamId()).getUserMap().entrySet()){
            if(entry.getValue()!=user&&NettyMemory.monsterMap.get(entry.getValue()).get(0)==monster){
                NettyMemory.monsterMap.get(entry.getValue()).remove(0);
                NettyMemory.eventStatus.put(NettyMemory.userToChannelMap.get(entry.getValue()), EventStatus.BOSSAREA);
            }
        }

        for(Map.Entry<String,Monster>entry:bossArea.getMonsters().get(bossArea.getSequence().get(0)).entrySet()){
            if(!entry.getValue().getStatus().equals(DeadOrAliveConfig.DEAD)){
                NettyMemory.monsterMap.get(user).remove(0);
                NettyMemory.monsterMap.get(user).add(entry.getValue());
                return;
            }
        }
    }

    public static void killBossMessageToAll(User user,Monster monster) {
        Team team = NettyMemory.teamMap.get(user.getTeamId());
        for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
            Channel channelTemp = NettyMemory.userToChannelMap.get(entry.getValue());
            channelTemp.writeAndFlush(MessageUtil.turnToPacket("玩家"+user.getUsername()+"击杀了："+monster.getName()));
        }
    }
}
