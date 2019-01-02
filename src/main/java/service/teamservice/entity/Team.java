package service.teamservice.entity;

import pojo.User;

import java.util.Map;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/11/19 14:13
 */
public class Team {
    private String teamId;

    private Map<String,User> userMap;

    private User leader;

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public Map<String, User> getUserMap() {
        return userMap;
    }

    public void setUserMap(Map<String, User> userMap) {
        this.userMap = userMap;
    }

    public User getLeader() {
        return leader;
    }

    public void setLeader(User leader) {
        this.leader = leader;
    }
}
