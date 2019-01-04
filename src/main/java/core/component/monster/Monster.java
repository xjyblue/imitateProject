package core.component.monster;

import java.util.List;
import java.util.Map;
import java.util.Objects;
/**
 * @ClassName Monster
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class Monster {
    /**
     * 普通怪
     */
    public static final String TYPEOFBOSS = "1";
    /**
     * boss怪
     */
    public static final String TYPEOFCOMMONMONSTER = "0";
    /**
     * 怪物id
     */
    private Integer id;
    /**
     * 怪物名称
     */
    private String name;
    /**
     * 怪物类别 boss还是普通怪
     */
    private String type;
    /**
     * 生命值
     */
    private String valueOfLife;
    /**
     * 状态
     */
    private String status;
    /**
     * 怪物技能
     */
    private List<MonsterSkill> monsterSkillList;
    /**
     * 怪物刷新的概率
     */
    private Integer probability;
    /**
     * 怪物技能id后面能转成上面的list
     */
    private String skillIds;
    /**
     * 怪物是否存在
     */
    private boolean ifExist;
    /**
     * 怪物所有buff
     */
    private Map<String, Integer> bufMap;
    /**
     * 怪物的位置
     */
    private String pos;
    /**
     * 怪物的奖励
     */
    private String reward;
    /**
     * 怪物的经验
     */
    private Integer experience;
    /**
     * 攻击间隔时间
     */
    private Long attackEndTime;
    /**
     *  buff刷新间隔时间
     */
    private Long buffRefreshTime;

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public Long getBuffRefreshTime() {
        return buffRefreshTime;
    }

    public void setBuffRefreshTime(Long buffRefreshTime) {
        this.buffRefreshTime = buffRefreshTime;
    }

    public Long getAttackEndTime() {
        return attackEndTime;
    }

    public void setAttackEndTime(Long attackEndTime) {
        this.attackEndTime = attackEndTime;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getSkillIds() {
        return skillIds;
    }

    public void setSkillIds(String skillIds) {
        this.skillIds = skillIds;
    }

    public Map<String, Integer> getBufMap() {
        return bufMap;
    }

    public void setBufMap(Map<String, Integer> bufMap) {
        this.bufMap = bufMap;
    }

    public boolean isIfExist() {
        return ifExist;
    }

    public void setIfExist(boolean ifExist) {
        this.ifExist = ifExist;
    }

    public Integer getProbability() {
        return probability;
    }

    public void setProbability(Integer probability) {
        this.probability = probability;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Monster(Integer id, String name, String type, String valueOfLife, List<MonsterSkill> monsterSkillList, String status) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.valueOfLife = valueOfLife;
        this.monsterSkillList = monsterSkillList;
        this.status = status;
    }

    public Monster() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValueOfLife() {
        return valueOfLife;
    }

    public void setValueOfLife(String valueOfLife) {
        this.valueOfLife = valueOfLife;
    }

    public List<MonsterSkill> getMonsterSkillList() {
        return monsterSkillList;
    }

    public void setMonsterSkillList(List<MonsterSkill> monsterSkillList) {
        this.monsterSkillList = monsterSkillList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Monster monster = (Monster) o;
        return Objects.equals(id, monster.id) &&
                Objects.equals(name, monster.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
