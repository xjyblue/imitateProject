package service.bossservice.service;

import config.impl.excel.BossSceneConfigResourceLoad;
import config.impl.excel.SceneResourceLoad;
import config.impl.thread.ThreadPeriodTaskLoad;
import core.annotation.Region;
import core.packet.ServerPacket;
import service.sceneservice.entity.BossScene;
import core.component.monster.Monster;
import service.sceneservice.entity.Scene;
import core.config.MessageConfig;
import core.config.GrobalConfig;
import core.channel.ChannelStatus;
import io.netty.channel.Channel;
import core.annotation.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import service.teamservice.entity.Team;
import service.levelservice.service.LevelService;
import service.teamservice.entity.TeamCache;
import utils.ChannelUtil;
import utils.MessageUtil;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName BossService
 * @Description boss副本服务
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
@Region
public class BossService {
    @Autowired
    private LevelService levelService;

    /**
     * 进入boss副本
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "ef", status = {ChannelStatus.COMMONSCENE})
    public void enterBossArea(Channel channel, String msg) {
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.TWO) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERRORORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        User user = ChannelUtil.channelToUserMap.get(channel);
//      10级以下无法进入副本
        if (levelService.getLevelByExperience(user.getExperience()) < GrobalConfig.MIN_ENTER_BOSSSCENE) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.NOLEVELTOMOVE);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
        Team team = null;

//      处理用户死亡后重连副本逻辑
        if (user.getTeamId() != null && BossSceneConfigResourceLoad.bossAreaMap.containsKey(user.getTeamId())) {
            ChannelUtil.channelStatus.put(channel, ChannelStatus.BOSSSCENE);
//          进入副本，用户场景线程转移
            BossScene bossScene = BossSceneConfigResourceLoad.bossAreaMap.get(user.getTeamId());
            bossScene.getUserMap().put(user.getUsername(), user);

            Scene scene = SceneResourceLoad.sceneMap.get(user.getPos());
            scene.getUserMap().remove(user.getUsername());
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.REBRORNANDCONNECTBOSSAREA);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }

//      处理用户一个人加入副本的逻辑
        if (user.getTeamId() == null) {
            team = new Team();
            team.setTeamId(UUID.randomUUID().toString());
            team.setLeader(user);
            user.setTeamId(team.getTeamId());
            HashMap<String, User> teamUserMap = new HashMap<>(64);
            teamUserMap.put(user.getUsername(), user);
            team.setUserMap(teamUserMap);
            TeamCache.teamMap.put(user.getTeamId(), team);
        } else {
//      进入副本队员死亡检查
            team = TeamCache.teamMap.get(user.getTeamId());
            if (checkAllManAlive(team)) {
                ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
                builder.setData(MessageConfig.SOMEBODYDEAD);
                MessageUtil.sendMessage(channel, builder.build());
                return;
            }
        }
//      进入副本队长检查
        if (!team.getLeader().getUsername().equals(user.getUsername())) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.YOUARENOLEADER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }
//      生成新副本
        BossScene bossScene = new BossScene(user.getTeamId(), temp[1]);
        BossSceneConfigResourceLoad.bossAreaMap.put(team.getTeamId(), bossScene);
//      开启副本场景帧频线程
        Future future = ThreadPeriodTaskLoad.BOSS_AREA_THREAD_POOL.scheduleAtFixedRate(bossScene, 0, 30, TimeUnit.MILLISECONDS);
        ThreadPeriodTaskLoad.futureMap.put(bossScene.getTeamId(), future);
        bossScene.setFutureMap(ThreadPeriodTaskLoad.futureMap);

//      改变用户渠道状态
        changeChannelStatus(team, bossScene);
    }


    /**
     * 检查是否有人活着
     *
     * @param team
     * @return
     */
    private boolean checkAllManAlive(Team team) {
        for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
            if (entry.getValue().getStatus().equals(GrobalConfig.DEAD)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 第一次进入副本改变队伍中所有用户的状态和提示信息
     *
     * @param team
     * @param bossScene
     */
    private void changeChannelStatus(Team team, BossScene bossScene) {
        for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
//          移除之前场景线程的用户
            User user = entry.getValue();
            Scene sceneOld = SceneResourceLoad.sceneMap.get(user.getPos());
            sceneOld.getUserMap().remove(user.getUsername());
//          新场景添加用户
            bossScene.getUserMap().put(user.getUsername(), user);

//          更新渠道的状态
            Channel channel = ChannelUtil.userToChannelMap.get(entry.getValue());
            ChannelUtil.channelStatus.put(channel, ChannelStatus.BOSSSCENE);

            String resp = "进入" + bossScene.getBossName() + "副本,出现boss有：";
            for (Map.Entry<String, Monster> entryMonster : bossScene.getMonsters().get(bossScene.getSequence().get(0)).entrySet()) {
                resp += entryMonster.getValue().getName() + " ";
            }
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(resp);
            MessageUtil.sendMessage(channel, builder.build());
        }
    }

}
