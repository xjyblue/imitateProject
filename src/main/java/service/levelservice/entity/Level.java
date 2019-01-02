package service.levelservice.entity;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/12/5 11:22
 */
public class Level {

    private Integer level;

    private Integer experienceUp;

    private Integer experienceDown;

    private String maxHp;

    private String maxMp;

    private Integer upAttack;

    private String caculatePercent;

    public String getCaculatePercent() {
        return caculatePercent;
    }

    public void setCaculatePercent(String caculatePercent) {
        this.caculatePercent = caculatePercent;
    }

    public Integer getUpAttack() {
        return upAttack;
    }

    public void setUpAttack(Integer upAttack) {
        this.upAttack = upAttack;
    }

    public Integer getExperienceUp() {
        return experienceUp;
    }

    public void setExperienceUp(Integer experienceUp) {
        this.experienceUp = experienceUp;
    }

    public Integer getExperienceDown() {
        return experienceDown;
    }

    public void setExperienceDown(Integer experienceDown) {
        this.experienceDown = experienceDown;
    }

    public String getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(String maxHp) {
        this.maxHp = maxHp;
    }

    public String getMaxMp() {
        return maxMp;
    }

    public void setMaxMp(String maxMp) {
        this.maxMp = maxMp;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

}
