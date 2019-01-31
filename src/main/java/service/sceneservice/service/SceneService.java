package service.sceneservice.service;

import config.impl.excel.SceneResourceLoad;
import core.channel.ChannelStatus;
import core.annotation.Order;
import core.annotation.Region;
import core.config.GrobalConfig;
import core.config.MessageConfig;
import core.packet.ServerPacket;
import io.netty.channel.Channel;
import mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import service.levelservice.service.LevelService;
import service.sceneservice.entity.Scene;
import service.userservice.service.UserService;
import utils.ChannelUtil;
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
@Region
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
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "move", status = {ChannelStatus.COMMONSCENE})
    public void moveScene(Channel channel, String msg) {
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.TWO) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERRORORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }

        User user = ChannelUtil.channelToUserMap.get(channel);
        if (temp[1].equals(SceneResourceLoad.sceneMap.get(user.getPos()).getName())) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.UNMOVELOCAL);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }

        Scene sceneTarget = SceneResourceLoad.sceneMap.get(sceneService.getSceneByName(temp[1]).getId());
        if (levelService.getLevelByExperience(user.getExperience()) < Integer.parseInt(sceneTarget.getNeedLevel())) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NOLEVELTOMOVE);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }

//      起始之地有特殊功效，能让人物的血量和蓝量回满
        if (GrobalConfig.RECOVER_SCENE.equals(sceneTarget.getName())) {
            userService.recoverUser(user);
        }

//      场景的移动切换用户到不同的场景线程
        Scene scene = SceneResourceLoad.sceneMap.get(user.getPos());
        if (!SceneResourceLoad.sceneSet.contains(temp[1])) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NOTARGETTOMOVE);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }

        if (!SceneResourceLoad.sceneMap.get(user.getPos()).getSceneSet().contains(temp[1])) {
//           场景切换
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.REMOTEMOVEMESSAGE);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }

        scene.getUserMap().remove(user.getUsername());
        sceneTarget.getUserMap().put(user.getUsername(), user);
        user.setPos(sceneTarget.getId());
        userMapper.updateByPrimaryKeySelective(user);
        ChannelUtil.channelToUserMap.put(channel, user);
        ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
        builder.setData("已移动到" + temp[1]);
        MessageUtil.sendMessage(channel, builder.build());

//      场景切换触发aoi
        userService.aoiMethod(channel, null);
    }

    /**
     * 通过场景名拿到场景
     *
     * @param areaName
     * @return
     */
    public Scene getSceneByName(String areaName) {
        for (Map.Entry<String, Scene> entry : SceneResourceLoad.sceneMap.entrySet()) {
            if (areaName.equals(entry.getValue().getName())) {
                return entry.getValue();
            }
        }
        return null;
    }
}
