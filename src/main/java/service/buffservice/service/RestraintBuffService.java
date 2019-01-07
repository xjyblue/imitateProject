package service.buffservice.service;

import core.component.monster.Monster;
import core.config.GrobalConfig;
import core.config.MessageConfig;
import core.context.ProjectContext;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userskillrelation;
import service.buffservice.entity.BuffConstant;
import service.skillservice.entity.UserSkill;
import utils.MessageUtil;

/**
 * @ClassName RestraintBuffService
 * @Description 技能反制效果
 * @Author xiaojianyu
 * @Date 2019/1/7 10:02
 * @Version 1.0
 **/
@Component
public class RestraintBuffService {
    @Autowired
    private AttackBuffService attackBuffService;

    public boolean restraintBuff(UserSkill userSkill, User user, Userskillrelation userskillrelation, Monster monster) {
        Channel channel = ProjectContext.userToChannelMap.get(user);
        if (user.getBuffMap().get(BuffConstant.SLEEPBUFF) != GrobalConfig.SLEEPBUFF_DEFAULTVALUE) {
            if (userSkill.getBuffMap().containsKey(BuffConstant.RELIEVEBUFF)) {
                attackBuffService.buffSolve(userskillrelation, userSkill, monster, user);
                return true;
            } else {
                channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.SLEEPMESSAGE));
                return false;
            }
        }
        return true;
    }
}