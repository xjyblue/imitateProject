package component;

public class Equipment {

    private Integer id;
    //武器名称
    private String name;
    //耐久度
    private Integer durability;
    //经过计算后增加技能伤害
    private Integer addValue;

    public Equipment(Integer id,String name,Integer durability, Integer addValue) {
        this.id =id;
        this.name = name;
        this.durability = durability;
        this.addValue = addValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
