package service.levelservice.entity;

/**
 * @ClassName Level
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class Level {
    /**
     * 等级
     */
    private Integer level;
    /**
     * 经验上限
     */
    private Integer experienceUp;
    /**
     * 经验下线
     */
    private Integer experienceDown;
    /**
     * 最大血量
     */
    private String maxHp;
    /**
     * 最大蓝量
     */
    private String maxMp;
    /**
     * 攻击提升
     */
    private Integer upAttack;
    /**
     * 计算因子
     */
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
