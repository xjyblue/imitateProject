package caculation;

import component.Monster;
import config.GrobalConfig;
import event.EventStatus;
import io.netty.channel.Channel;
import context.ProjectContext;
import utils.LevelUtil;
import org.springframework.stereotype.Component;
import pojo.User;

import java.math.BigInteger;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/12/5 16:14
 */

@Component("hpCaculation")
public class HpCaculation {

    //  计算血量全部在这里，顺便可以加减血
    public void addUserHp(User user, String recoverValue) {
        Integer userHp = Integer.parseInt(user.getHp());
        Integer maxHp = Integer.parseInt(LevelUtil.getMaxMp(user));
        Integer recoverHp = Integer.parseInt(recoverValue);
        userHp += recoverHp;
        if (userHp > maxHp) {
            user.setHp(maxHp.toString());
        } else {
            user.setHp(userHp.toString());
        }
    }

    public void subUserHp(User user, String reduceValue) {
        Channel channel = ProjectContext.userToChannelMap.get(user);
        Integer reduceValueB = Integer.parseInt(reduceValue);
        Integer userHp = Integer.parseInt(user.getHp());
        userHp -= reduceValueB;
        if (userHp < 0) {
            userHp = 0;
        }
        if (ProjectContext.eventStatus.get(channel).equals(EventStatus.ATTACK) && userHp == 0 && user.getStatus().equals(GrobalConfig.ALIVE)) {
            user.setHp(GrobalConfig.MINVALUE);
            user.setStatus(GrobalConfig.DEAD);
//          移除用户所攻击的所有怪物
            ProjectContext.userToMonsterMap.remove(user);
            ProjectContext.eventStatus.put(channel, EventStatus.DEADAREA);
        } else {
            user.setHp(userHp.toString());
        }
    }

    public void subMonsterHp(Monster monster, String subValue) {
        Integer subHp = Integer.parseInt(subValue);
        Integer monsterHp = Integer.parseInt(monster.getValueOfLife());
        monsterHp -= subHp;
        if (monsterHp < 0) {
            monster.setValueOfLife(GrobalConfig.MINVALUE);
        } else {
            monster.setValueOfLife(monsterHp.toString());
        }
    }
}
