package service.caculationservice.service;

import core.component.good.Equipment;
import core.component.monster.Monster;
import core.config.GrobalConfig;
import core.channel.ChannelStatus;
import io.netty.channel.Channel;
import mapper.UserMapper;
import service.attackservice.util.AttackUtil;
import service.levelservice.service.LevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import service.userservice.service.UserService;
import utils.ChannelUtil;

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
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    /**
     * 用户加血
     *
     * @param user
     * @param recoverValue
     */
    public void addUserHp(User user, String recoverValue) {
        Integer userHp = Integer.parseInt(user.getHp());
        Integer maxHp = Integer.parseInt(levelService.getMaxHp(user));
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
     *
     * @param user
     * @param reduceValue
     */
    public void subUserHp(User user, String reduceValue) {
        Channel channel = ChannelUtil.userToChannelMap.get(user);
        Integer reduceValueB = Integer.parseInt(reduceValue);
        Integer userHp = Integer.parseInt(user.getHp());
        userHp -= reduceValueB;
        if (userHp < 0) {
            userHp = 0;
        }
        if (ChannelUtil.channelStatus.get(channel).equals(ChannelStatus.ATTACK) && userHp == 0 && user.getStatus().equals(GrobalConfig.ALIVE)) {
//          人物设置为死亡
            user.setHp(GrobalConfig.MINVALUE);
            user.setStatus(GrobalConfig.DEAD);
//          移除用户所攻击的所有怪物
            AttackUtil.removeAllMonster(user);
//          初始化人物buff
            userService.initUserBuff(user);
            ChannelUtil.channelStatus.put(channel, ChannelStatus.DEADSCENE);
        } else {
            user.setHp(userHp.toString());
        }
    }

    /**
     * 怪物扣血
     *
     * @param monster
     */
    public void subMonsterHp(Monster monster, Integer subHp) {
        Integer monsterHp = Integer.parseInt(monster.getValueOfLife());
        monsterHp -= subHp;
        if (monsterHp < 0) {
            monster.setValueOfLife(GrobalConfig.MINVALUE);
            monster.setStatus(GrobalConfig.DEAD);
        } else {
            monster.setValueOfLife(monsterHp.toString());
        }
    }

    public void subUserHpByTakeOffEquip(User user, Equipment equipment) {
        if (Integer.parseInt(user.getHp())  > Integer.parseInt(levelService.getMaxHp(user))) {
            user.setHp(levelService.getMaxHp(user));
            userMapper.updateByPrimaryKeySelective(user);

        }
    }
}
