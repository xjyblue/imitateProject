package service.skillservice.entity;

import java.util.Map;
/**
 * @ClassName UserSkill
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class UserSkill {
    /**
     * 技能id
     */
    private Integer skillId;
    /**
     * 技能名称
     */
    private String skillName;
    /**
     * 技能cd
     */
    private Long attackCd;
    /**
     * 技能伤害
     */
    private String damage;
    /**
     * 技能蓝量
     */
    private String skillMp;
    /**
     * 技能附带buff
     */
    private String bufferMapId;
    /**
     * 技能所带buff
     */
    private Map<String,Integer>buffMap;
    /**
     * 技能所属角色
     */
    private Integer roleSkill;

    public Integer getRoleSkill() {
        return roleSkill;
    }

    public void setRoleSkill(Integer roleSkill) {
        this.roleSkill = roleSkill;
    }

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
