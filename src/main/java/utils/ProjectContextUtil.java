package utils;

import config.impl.excel.SceneResourceLoad;
import io.netty.channel.ChannelHandlerContext;
import pojo.User;
import service.sceneservice.entity.Scene;
import service.teamservice.service.TeamService;

/**
 * @ClassName ProjectContextUtil
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/8 11:00
 * @Version 1.0
 **/
public class ProjectContextUtil {

    /**
     * 清空玩家上下文信息
     * @param ctx
     * @param user
     */
    public static void clearContextUserInfo(ChannelHandlerContext ctx, User user) {
        TeamService teamService = SpringContextUtil.getBean("teamService");
//      移除队伍或者副本中玩家
        if (user.getTeamId() != null) {
            teamService.handleUserOffline(user);
        }
//      移除场景下的玩家
        Scene scene = SceneResourceLoad.sceneMap.get(user.getPos());
        if (scene.getUserMap().containsKey(user.getUsername())) {
            scene.getUserMap().remove(user.getUsername());
        }
//              移除channel和用户的关联
        if (ChannelUtil.channelToUserMap.containsKey(ctx.channel())) {
            ChannelUtil.channelToUserMap.remove(ctx.channel());
        }
        if (ChannelUtil.userToChannelMap.containsKey(user)) {
            ChannelUtil.userToChannelMap.remove(user);
        }
//              移除渠道状态
        if (ChannelUtil.channelStatus.containsKey(ctx.channel())) {
            ChannelUtil.channelStatus.remove(ctx.channel());
        }
    }
}
