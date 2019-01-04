package service.teamservice.entity;

import pojo.User;

import java.util.Map;

/**
 * @ClassName Team
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class Team {
    /**
     * 队伍id
     */
    private String teamId;
    /**
     * 队伍中的用户
     */
    private Map<String,User> userMap;
    /**
     * 队伍中的领导者
     */
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
