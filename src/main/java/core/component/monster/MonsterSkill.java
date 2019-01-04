package core.component.monster;

import java.util.Map;
/**
 * @ClassName MonsterSkill
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class MonsterSkill {
    private Integer skillId;

    private String skillName;

    private String attackCd;

    private String damage;

    private String bufferMapId;

    private Map<String,Integer> buffMap;

    public String getBufferMapId() {
        return bufferMapId;
    }

    public void setBufferMapId(String bufferMapId) {
        this.bufferMapId = bufferMapId;
    }

    public Map<String, Integer> getBuffMap() {
        return buffMap;
    }

    public void setBuffMap(Map<String, Integer> buffMap) {
        this.buffMap = buffMap;
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

    public String getAttackCd() {
        return attackCd;
    }

    public void setAttackCd(String attackCd) {
        this.attackCd = attackCd;
    }

    public String getDamage() {
        return damage;
    }

    public void setDamage(String damage) {
        this.damage = damage;
    }
}
