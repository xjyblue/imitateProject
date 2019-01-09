package service.sceneservice.service;

import core.config.GrobalConfig;
import core.config.MessageConfig;
import core.context.ProjectContext;
import io.netty.channel.Channel;
import mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import service.levelservice.service.LevelService;
import service.sceneservice.entity.Scene;
import service.userservice.service.UserService;
import utils.MessageUtil;

import java.util.Map;

/**
 * @ClassName SceneService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
public class SceneService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private LevelService levelService;
    @Autowired
    private UserService userService;
    @Autowired
    private SceneService sceneService;

    /**
     * 移动场景，线程切换
     * @param channel
     * @param msg
     */
    public void moveScene(Channel channel, String msg) {
        String[] temp = msg.split("-");
        if (temp.length != GrobalConfig.TWO) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }

        User user = ProjectContext.channelToUserMap.get(channel);
        if (temp[1].equals(ProjectContext.sceneMap.get(user.getPos()).getName())) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.UNMOVELOCAL));
            return;
        }

        Scene sceneTarget = ProjectContext.sceneMap.get(sceneService.getSceneByName(temp[1]).getId());
        if (levelService.getLevelByExperience(user.getExperience()) < Integer.parseInt(sceneTarget.getNeedLevel())) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOLEVELTOMOVE));
            return;
        }

//      起始之地有特殊功效，能让人物的血量和蓝量回满
        if (GrobalConfig.RECOVER_SCENE.equals(sceneTarget.getName())) {
            userService.recoverUser(user);
        }

//      场景的移动切换用户到不同的场景线程
        Scene scene = ProjectContext.sceneMap.get(user.getPos());
        if (!ProjectContext.sceneSet.contains(temp[1])) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.NOTARGETTOMOVE));
            return;
        }

        if (!ProjectContext.sceneMap.get(user.getPos()).getSceneSet().contains(temp[1])) {
//           场景切换
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.REMOTEMOVEMESSAGE));
            return;
        }

        scene.getUserMap().remove(user.getUsername());
        sceneTarget.getUserMap().put(user.getUsername(), user);
        user.setPos(sceneTarget.getId());
        userMapper.updateByPrimaryKeySelective(user);
        ProjectContext.channelToUserMap.put(channel, user);
        channel.writeAndFlush(MessageUtil.turnToPacket("已移动到" + temp[1]));
    }

    /**
     * 通过场景名拿到场景
     * @param areaName
     * @return
     */
    public Scene getSceneByName(String areaName) {
        for (Map.Entry<String, Scene> entry : ProjectContext.sceneMap.entrySet()) {
            if (areaName.equals(entry.getValue().getName())) {
                return entry.getValue();
            }
        }
        return null;
    }
}
