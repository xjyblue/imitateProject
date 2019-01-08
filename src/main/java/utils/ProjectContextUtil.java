package utils;

import core.context.ProjectContext;
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
        Scene scene = ProjectContext.sceneMap.get(user.getPos());
        if (scene.getUserMap().containsKey(user.getUsername())) {
            scene.getUserMap().remove(user.getUsername());
        }
//              移除玩家的所有buff终止时间
        if (ProjectContext.userBuffEndTime.containsKey(user)) {
            ProjectContext.userBuffEndTime.remove(user);
        }
//              移除怪物的buff终止时间
        if (ProjectContext.userToMonsterMap.containsKey(user)) {
            ProjectContext.userToMonsterMap.remove(user);
        }
//              移除channel和用户的关联
        if (ProjectContext.session2UserIds.containsKey(ctx.channel())) {
            ProjectContext.session2UserIds.remove(ctx.channel());
        }
        if (ProjectContext.userToChannelMap.containsKey(user)) {
            ProjectContext.userToChannelMap.remove(user);
        }
//              移除渠道状态
        if (ProjectContext.eventStatus.containsKey(ctx.channel())) {
            ProjectContext.eventStatus.remove(ctx.channel());
        }
//              移除用户技能关联
        if (ProjectContext.userskillrelationMap.containsKey(user)) {
            ProjectContext.userskillrelationMap.remove(user);
        }
    }
}