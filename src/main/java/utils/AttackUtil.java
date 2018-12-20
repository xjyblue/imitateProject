package utils;

import component.BossScene;
import component.Monster;
import config.DeadOrAliveConfig;
import event.EventStatus;
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

    public static void changeUserAttackMonster(User user, BossScene bossScene) {
//      处理其他玩家在打该boss
        Monster monster = null;
        for(Map.Entry<Integer,Monster> entry : ProjectContext.userToMonsterMap.get(user).entrySet()){
            monster = entry.getValue();
        }
        for (Map.Entry<String, User> entry : ProjectContext.teamMap.get(user.getTeamId()).getUserMap().entrySet()) {
            User userT = entry.getValue();
            Monster monsterT = null;
            if(ProjectContext.userToMonsterMap.containsKey(userT)){
                for(Map.Entry<Integer,Monster> monsterEntry : ProjectContext.userToMonsterMap.get(userT).entrySet()){
                    monsterT = monsterEntry.getValue();
                }
            }
            if (userT != user && ProjectContext.userToMonsterMap.containsKey(userT) && monsterT == monster) {
                for(Map.Entry<Integer,Monster> monsterEntry : ProjectContext.userToMonsterMap.get(user).entrySet()){
                    monster = monsterEntry.getValue();
                }
                ProjectContext.userToMonsterMap.get(entry.getValue()).remove(monster.getId());
                ProjectContext.eventStatus.put(ProjectContext.userToChannelMap.get(entry.getValue()), EventStatus.BOSSAREA);
            }
        }

//      处理本人正在打该boss，并且让二号boss攻击人物，如果二号boss死掉就生成第二场景的boss攻击人物
        for (Map.Entry<String, Monster> entry : bossScene.getMonsters().get(bossScene.getSequence().get(0)).entrySet()) {
            if (!entry.getValue().getStatus().equals(DeadOrAliveConfig.DEAD)) {
                for(Map.Entry<Integer,Monster> monsterEntry : ProjectContext.userToMonsterMap.get(user).entrySet()){
                    monster = monsterEntry.getValue();
                }
                ProjectContext.userToMonsterMap.get(user).remove(monster.getId());
                ProjectContext.userToMonsterMap.get(user).put(entry.getValue().getId(),entry.getValue());
                return;
            }
        }
    }

    public static void killBossMessageToAll(User user, Monster monster) {
        Team team = ProjectContext.teamMap.get(user.getTeamId());
        for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
            Channel channelTemp = ProjectContext.userToChannelMap.get(entry.getValue());
            channelTemp.writeAndFlush(MessageUtil.turnToPacket("玩家" + user.getUsername() + "击杀了：" + monster.getName()));
        }
    }

    public static void addMonsterToUserMonsterList(User user, Monster monster) {
        if (ProjectContext.userToMonsterMap.containsKey(user)) {
            ProjectContext.userToMonsterMap.get(user).put(monster.getId(),monster);
        } else {
            Map<Integer,Monster> map = new HashMap<>();
            map.put(monster.getId(),monster);
            ProjectContext.userToMonsterMap.put(user, map);
        }
    }
}
