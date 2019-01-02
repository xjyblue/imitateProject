package component.good;

import component.good.parent.PGood;

public class Equipment extends PGood {

    private Integer id;
    //耐久度
    private Integer durability;
    //增加技能伤害
    private Integer addValue;
    //增加生命值
    private Integer lifeValue;
    //星级
    private Integer startLevel;

    public Equipment(Integer id,String name,Integer durability, Integer addValue) {
        this.id =id;
        this.name = name;
        this.durability = durability;
        this.addValue = addValue;
    }

    public Equipment(){
    }

    public Integer getLifeValue() {
        return lifeValue;
    }

    public void setLifeValue(Integer lifeValue) {
        this.lifeValue = lifeValue;
    }

    public Integer getStartLevel() {
        return startLevel;
    }

    public void setStartLevel(Integer startLevel) {
        this.startLevel = startLevel;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDurability() {
        return durability;
    }

    public void setDurability(Integer durability) {
        this.durability = durability;
    }

    public Integer getAddValue() {
        return addValue;
    }

    public void setAddValue(Integer addValue) {
        this.addValue = addValue;
    }
}
