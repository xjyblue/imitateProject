package component.parent;

import component.Equipment;
import component.HpMedicine;
import component.MpMedicine;
import context.ProjectContext;
import pojo.Userbag;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/11/21 9:46
 */
//存储所有物品的共有属性
public class Good {

    public static final String EQUIPMENT = "3";

    public static final String MPMEDICINE = "1";

    public static final String HPMEDICINE = "2";

    public static final String CHANGEGOOD = "4";

    protected String name;

    protected String type;

    protected String buyMoney;

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
        if (userbag.getTypeof().equals(Good.MPMEDICINE)) {
            MpMedicine mpMedicine = ProjectContext.mpMedicineMap.get(userbag.getWid());
            return "[蓝药--》] [物品id:" + userbag.getId() + "] [药品名称：" + mpMedicine.getName() + "]" + " [药品数量: " + userbag.getNum() + "]";
        }
        if (userbag.getTypeof().equals(Good.EQUIPMENT)) {
            Equipment equipment = ProjectContext.equipmentMap.get(userbag.getWid());
            return "[武器--》] [物品id:" + userbag.getId() + "] [武器名称：" + equipment.getName() + "]" + " [武器耐久度：" + userbag.getDurability() + "]" + " [武器数量： " + userbag.getNum() + "]" + "[武器星级：]" + userbag.getStartlevel();
        }
        if (userbag.getTypeof().equals(Good.HPMEDICINE)) {
            HpMedicine hpMedicine = ProjectContext.hpMedicineMap.get(userbag.getWid());
            return "[红药--》] [物品id:" + userbag.getId() + "] [药品名称：" + hpMedicine.getName() + "]" + " [武器数量： " + userbag.getNum() + "]";
        }
        return null;
    }

}
