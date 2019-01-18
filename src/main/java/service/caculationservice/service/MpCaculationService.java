package service.caculationservice.service;

import core.config.MessageConfig;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import service.levelservice.service.LevelService;
import service.skillservice.entity.UserSkill;
import utils.ChannelUtil;
import utils.MessageUtil;

/**
 * @ClassName MpCaculationService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
public class MpCaculationService {

    @Autowired
    private LevelService levelService;

    /**
     * 扣蓝
     * @param user
     * @param subMp
     */
    public void subUserMp(User user, String subMp) {
        Integer userMp = Integer.parseInt(user.getMp());
        Integer subMpI = Integer.parseInt(subMp);
        userMp -= subMpI;
        if (userMp < 0) {
            userMp = 0;
        }
        user.setMp(userMp.toString());
    }

    /**
     * 加蓝
     * @param user
     * @param addMp
     */
    public void addUserMp(User user, String addMp) {
        Integer userMp = Integer.parseInt(user.getMp());
        Integer addMpI = Integer.parseInt(addMp);
        userMp += addMpI;
        Integer maxMp = Integer.parseInt(levelService.getMaxMp(user));
        if(userMp>maxMp){
            user.setMp(maxMp.toString());
        }else {
            user.setMp(userMp.toString());
        }
    }

    /**
     * 校验用户的mp是否足够
     *
     * @param user
     * @param userSkill
     * @return
     */
    public boolean checkUserMpEnough(User user, UserSkill userSkill) {
        Integer userMp = Integer.parseInt(user.getMp());
        Integer skillMp = Integer.parseInt(userSkill.getSkillMp());
        Channel channel = ChannelUtil.userToChannelMap.get(user);
        if (userMp < skillMp) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNENOUGHMP));
            return false;
        }
        return true;
    }
}
