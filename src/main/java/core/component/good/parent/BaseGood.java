package core.component.good.parent;

import core.component.good.Equipment;
import core.component.good.HpMedicine;
import core.component.good.MpMedicine;
import core.context.ProjectContext;
import pojo.Userbag;

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

    public static String getGoodNameByUserbag(Userbag userbag) {
        if (userbag.getTypeof().equals(BaseGood.MPMEDICINE)) {
            MpMedicine mpMedicine = ProjectContext.mpMedicineMap.get(userbag.getWid());
            return "[蓝药--》] [物品id:" + userbag.getId() + "] [药品名称：" + mpMedicine.getName() + "]" + " [药品数量: " + userbag.getNum() + "]";
        }
        if (userbag.getTypeof().equals(BaseGood.EQUIPMENT)) {
            Equipment equipment = ProjectContext.equipmentMap.get(userbag.getWid());
            return "[武器--》] [物品id:" + userbag.getId() + "] [武器名称：" + equipment.getName() + "]" + " [武器耐久度：" + userbag.getDurability() + "]" + " [武器数量： " + userbag.getNum() + "]" + "[武器星级：]" + userbag.getStartlevel();
        }
        if (userbag.getTypeof().equals(BaseGood.HPMEDICINE)) {
            HpMedicine hpMedicine = ProjectContext.hpMedicineMap.get(userbag.getWid());
            return "[红药--》] [物品id:" + userbag.getId() + "] [药品名称：" + hpMedicine.getName() + "]" + " [武器数量： " + userbag.getNum() + "]";
        }
        return null;
    }

}
