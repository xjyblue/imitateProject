package skill;

import java.util.Map;

public class UserSkill {

    private Integer skillId;

    private String skillName;

    private Long attackCd;

    private String damage;

    private String skillMp;

    private Map<String,Integer>buffMap;

    public Map<String, Integer> getBuffMap() {
        return buffMap;
    }

    public void setBuffMap(Map<String, Integer> buffMap) {
        this.buffMap = buffMap;
    }

    public String getSkillMp() {
        return skillMp;
    }

    public void setSkillMp(String skillMp) {
        this.skillMp = skillMp;
    }

    public Integer getSkillId() {
        return skillId;
    }

    public void setSkillId(Integer skillId) {
        this.skillId = skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public Long getAttackCd() {
        return attackCd;
    }

    public void setAttackCd(Long attackCd) {
        this.attackCd = attackCd;
    }

    public String getDamage() {
        return damage;
    }

    public void setDamage(String damage) {
        this.damage = damage;
    }

}
