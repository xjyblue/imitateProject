package service.teamservice.entity;

import com.google.common.collect.Sets;
import pojo.User;

import java.util.Map;
import java.util.Set;

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
     * 队伍名称
     */
    private String teamName;
    /**
     * 队伍中的用户
     */
    private Map<String, User> userMap;
    /**
     * 队伍中的领导者
     */
    private User leader;
    /**
     * 领队的id
     */
    private String leaderId;

    /**
     * 进入副本的投票
     */
    private Set<String> voteSet = Sets.newHashSet();

    /**
     * 进入副本的编号
     */
    private String bossAreaVoteTarget;

    public String getBossAreaVoteTarget() {
        return bossAreaVoteTarget;
    }

    public void setBossAreaVoteTarget(String bossAreaVoteTarget) {
        this.bossAreaVoteTarget = bossAreaVoteTarget;
    }

    public Set<String> getVoteSet() {
        return voteSet;
    }

    public void setVoteSet(Set<String> voteSet) {
        this.voteSet = voteSet;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
    }

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
