package component;

import skill.MonsterSkill;

import java.math.BigInteger;
import java.util.List;

public class Monster {
    private Integer id;

    private String name;

    private String type;

    private volatile String valueOfLife;

    private String status;

    private List<MonsterSkill> monsterSkillList;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Monster(String name, String type, String valueOfLife, List<MonsterSkill> monsterSkillList,String status) {
        this.name = name;
        this.type = type;
        this.valueOfLife = valueOfLife;
        this.monsterSkillList = monsterSkillList;
        this.status = status;
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

    public synchronized void setValueOfLife(String valueOfLife) {
        this.valueOfLife = valueOfLife;
    }

    public List<MonsterSkill> getMonsterSkillList() {
        return monsterSkillList;
    }

    public void setMonsterSkillList(List<MonsterSkill> monsterSkillList) {
        this.monsterSkillList = monsterSkillList;
    }

    public synchronized BigInteger subLife(BigInteger attackDamage) {
        BigInteger monsterLife = new BigInteger(this.valueOfLife);
        this.valueOfLife = monsterLife.subtract(attackDamage).toString();
        return new BigInteger(this.valueOfLife);

    }
}