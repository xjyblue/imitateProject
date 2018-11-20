package event;

import io.netty.channel.Channel;
import memory.NettyMemory;
import org.springframework.stereotype.Component;
import pojo.User;
import team.Team;
import utils.DelimiterUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/11/19 14:23
 */
@Component("teamEvent")
public class TeamEvent {

    public void team(Channel channel, String msg) {
        if (msg.equals("t")) {
            if (getUser(channel).getTeamId() == null) {
                channel.writeAndFlush(DelimiterUtils.addDelimiter("你还没有队伍，可以输入t-create创建队伍或者t-add-（已有队伍的玩家名）加入队伍"));
                return;
            } else {
                Team team = getTeam(getUser(channel));
                String resp = "该队伍中有:[";
                if (team != null && team.getUserMap() != null) {
                    for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
                        resp += entry.getKey() + "  ";
                    }
                    resp+="]该队伍队长是["+team.getLeader().getUsername()+"]";
                    channel.writeAndFlush(DelimiterUtils.addDelimiter(resp));
                } else {
                    channel.writeAndFlush(DelimiterUtils.addDelimiter("你还没有队伍，可以输入t-create创建队伍或者t-add-（已有队伍的玩家名）加入队伍"));
                    return;
                }
            }
        }

        if(msg.equals("t-create")){
            User user = getUser(channel);
            if (getUser(channel).getTeamId() != null&&getTeam(getUser(channel))!=null) {
                channel.writeAndFlush(DelimiterUtils.addDelimiter("你已在队伍中无法创建队伍"));
                return;
            }else {
                Team team = new Team();
                team.setTeamId(UUID.randomUUID().toString());
                team.setLeader(user);
                user.setTeamId(team.getTeamId());
                HashMap<String,User> teamUserMap = new HashMap<>();
                teamUserMap.put(user.getUsername(),user);
                team.setUserMap(teamUserMap);
                NettyMemory.teamMap.put(user.getTeamId(),team);
                channel.writeAndFlush(DelimiterUtils.addDelimiter("你成功创建队伍"));
                return;
            }
        }

        if(msg.equals("t-remove")){
            User user = getUser(channel);
            Team team = getTeam(getUser(channel));
            if(team==null){
                channel.writeAndFlush(DelimiterUtils.addDelimiter("你不在队伍中请不要做无效操作"));
                return;
            }
            if(team!=null&&team.getLeader().getUsername().equals(user.getUsername())){
                NettyMemory.teamMap.remove(user.getTeamId());
                for (Map.Entry<String, User> entry : team.getUserMap().entrySet()) {
                   entry.getValue().setTeamId(null);
                }
                channel.writeAndFlush(DelimiterUtils.addDelimiter("你已经解散当前队伍"));
            }else {
                if(team.getUserMap().containsKey(user.getUsername())){
                    team.getUserMap().remove(user.getUsername());
                }
                user.setTeamId(null);
                channel.writeAndFlush(DelimiterUtils.addDelimiter("你已退出当前队伍"));
            }
        }

        if(msg.startsWith("t-add")){
            User user = getUser(channel);
            String temp[] = msg.split("-");
            User userLeader = getUserByName(temp[2]);
            if(userLeader==null||getTeam(userLeader)==null){
                channel.writeAndFlush(DelimiterUtils.addDelimiter("你所要加入的玩家队伍不存在"));
                return;
            }else {
                Team team = getTeam(userLeader);
                if(team != null){
                    user.setTeamId(team.getTeamId());
                    team.getUserMap().put(user.getUsername(),user);
                    channel.writeAndFlush(DelimiterUtils.addDelimiter("成功加入"+userLeader.getUsername()+"的队伍"));
                    return;
                }
            }
        }
    }

    private User getUserByName(String s) {
        for (Map.Entry<Channel, User> entry : NettyMemory.session2UserIds.entrySet()) {
            if(entry.getValue().getUsername().equals(s)){
                return entry.getValue();
            }
        }
        return null;
    }

    private Team getTeam(User user) {
        if (user.getTeamId() != null && NettyMemory.teamMap.containsKey(user.getTeamId())) {
            return NettyMemory.teamMap.get(user.getTeamId());
        }
        return null;
    }

    private User getUser(Channel channel) {
        return NettyMemory.session2UserIds.get(channel);
    }
}