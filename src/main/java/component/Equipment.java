package component;

public class Equipment {
    //耐久度
    private Integer durability;
    //经过计算后增加技能伤害
    private Integer addValue;
    //武器是否有耐久
    private boolean ifUsed;

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

    public boolean isIfUsed() {
        return ifUsed;
    }

    public void setIfUsed(boolean ifUsed) {
        this.ifUsed = ifUsed;
    }
}
