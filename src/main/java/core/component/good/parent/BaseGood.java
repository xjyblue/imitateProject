package core.component.good.parent;

/**
 * @ClassName BaseGood
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class BaseGood {

    public static final String EQUIPMENT = "3";

    public static final String MPMEDICINE = "1";

    public static final String MONEY = "0";

    public static final String HPMEDICINE = "2";

    public static final String CHANGEGOOD = "4";
    /**
     * 名字
     */
    protected String name;
    /**
     * 类别
     */
    protected String type;
    /**
     * 购入价
     */
    protected String buyMoney;
    /**
     * 描述
     */
    protected String desc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getBuyMoney() {
        return buyMoney;
    }

    public void setBuyMoney(String buyMoney) {
        this.buyMoney = buyMoney;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
