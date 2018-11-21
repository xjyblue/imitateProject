package component.parent;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/11/21 9:46
 */
//存储所有物品的共有属性
public class Good {

    public static final String EQUIPMENT = "3";

    public static final String MPMEDICINE = "1";

    public static final String HPMEDICINE = "2";

    protected String name;

    protected String type;

    protected String buyMoney;

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
