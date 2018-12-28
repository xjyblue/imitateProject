package test;

import achievement.Achievement;
import component.CollectGood;
import component.HpMedicine;
import component.NPC;
import component.Scene;
import component.parent.Good;
import level.Level;
import org.apache.poi.ss.formula.functions.Npv;
import role.Role;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class InputUtil {
    public static void main(String[]args) throws Exception {
        //将生成的excel转换成文件，还可以用作文件下载
        File file = new File("src/main/resources/CollectGood.xls");
        FileOutputStream hpMedicinefos = new FileOutputStream(file);

        //对象集合
        List<CollectGood> hpMedicineList=new ArrayList<>();
        CollectGood collectGood = new CollectGood();
        collectGood.setDesc("此物品有神秘力量");
        collectGood.setType(Good.CHANGEGOOD);
        collectGood.setName("魂石");
        collectGood.setBuyMoney("9999999");
        collectGood.setId(90000);



        //设置属性别名（列名）
        LinkedHashMap<String, String> hpMedicineAlias = new LinkedHashMap<String, String>();
        hpMedicineAlias.put("name", "物品的名字");
        hpMedicineAlias.put("id","物品的id");
        hpMedicineAlias.put("desc","物品的描述");
        hpMedicineAlias.put("buyMoney","物品的价格");
        hpMedicineAlias.put("type","物品的种类");

        //标题
        String headLine="HpMedicine表";
        ExcelUtil.pojo2Excel(hpMedicineList, hpMedicinefos, hpMedicineAlias,headLine);
    }
}
