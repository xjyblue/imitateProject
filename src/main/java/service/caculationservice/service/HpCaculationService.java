package service.caculationservice.service;

import core.component.monster.Monster;
import core.config.GrobalConfig;
import core.ChannelStatus;
import io.netty.channel.Channel;
import core.context.ProjectContext;
import service.levelservice.service.LevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;

/**
 * @ClassName HpCaculationService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
public class HpCaculationService {
    @Autowired
    private LevelService levelService;

    /**
     * 用户加血
     * @param user
     * @param recoverValue
     */
    public void addUserHp(User user, String recoverValue) {
        Integer userHp = Integer.parseInt(user.getHp());
        Integer maxHp = Integer.parseInt(levelService.getMaxMp(user));
        Integer recoverHp = Integer.parseInt(recoverValue);
        userHp += recoverHp;
        if (userHp > maxHp) {
            user.setHp(maxHp.toString());
        } else {
            user.setHp(userHp.toString());
        }
    }

    /**
     * 用户扣血
     * @param user
     * @param reduceValue
     */
    public void subUserHp(User user, String reduceValue) {
        Channel channel = ProjectContext.userToChannelMap.get(user);
        Integer reduceValueB = Integer.parseInt(reduceValue);
        Integer userHp = Integer.parseInt(user.getHp());
        userHp -= reduceValueB;
        if (userHp < 0) {
            userHp = 0;
        }
        if (ProjectContext.channelStatus.get(channel).equals(ChannelStatus.ATTACK) && userHp == 0 && user.getStatus().equals(GrobalConfig.ALIVE)) {
            user.setHp(GrobalConfig.MINVALUE);
            user.setStatus(GrobalConfig.DEAD);
//          移除用户所攻击的所有怪物
            ProjectContext.userToMonsterMap.remove(user);
            ProjectContext.channelStatus.put(channel, ChannelStatus.DEADSCENE);
        } else {
            user.setHp(userHp.toString());
        }
    }

    /**
     * 怪物扣血
     * @param monster
     * @param subValue
     */
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
