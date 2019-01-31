package pojo;

public class Achievementprocess {
    private Integer id;

    private String username;

    private Integer iffinish;

    private Integer achievementid;

    private String processs;

    private Integer type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public Integer getIffinish() {
        return iffinish;
    }

    public void setIffinish(Integer iffinish) {
        this.iffinish = iffinish;
    }

    public Integer getAchievementid() {
        return achievementid;
    }

    public void setAchievementid(Integer achievementid) {
        this.achievementid = achievementid;
    }

    public String getProcesss() {
        return processs;
    }

    public void setProcesss(String processs) {
        this.processs = processs == null ? null : processs.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}