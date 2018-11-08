package skill;

public class MonsterSkill {
   protected String skillId;

   protected String skillName;

   protected String attackCd;

   protected String damage;

    public String getSkillId() {
        return skillId;
    }

    public void setSkillId(String skillId) {
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
