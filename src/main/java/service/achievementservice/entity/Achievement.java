package service.achievementservice.entity;

/**
 * @ClassName Achievement
 * @Description 成就模块
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class Achievement {

    private Integer achievementId;

    private String name;

    private Integer type;

    private String target;

    private String begin;

    private String parent;

    private String sons;

    private String reward;

    private Integer npcId;

    private boolean getTask;

    public Integer getNpcId() {

        return npcId;
    }

    public void setNpcId(Integer npcId) {
        this.npcId = npcId;
    }

    public boolean isGetTask() {
        return getTask;
    }

    public void setGetTask(boolean getTask) {
        this.getTask = getTask;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getSons() {
        return sons;
    }

    public void setSons(String sons) {
        this.sons = sons;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public Integer getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(Integer achievementId) {
        this.achievementId = achievementId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
