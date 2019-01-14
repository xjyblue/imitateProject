package service.petservice.service.entity;

/**
 * @ClassName PetSkillConfig
 * @Description 宠物的技能
 * @Author xiaojianyu
 * @Date 2019/1/9 18:21
 * @Version 1.0
 **/
public class PetSkillConfig {
    /**
     * 技能id
     */
    private String id;
    /**
     * 技能名
     */
    private String skillName;
    /**
     * 所属宠物的id
     */
    private String petId;
    /**
     * 技能所带伤害
     */
    private Integer damage;

    public Integer getDamage() {
        return damage;
    }

    public void setDamage(Integer damage) {
        this.damage = damage;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }
}
