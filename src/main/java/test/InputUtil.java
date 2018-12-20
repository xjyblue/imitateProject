package test;

import achievement.Achievement;
import component.HpMedicine;
import component.NPC;
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
        File file = new File("C:\\Users\\server\\IdeaProjects\\imitateProject\\src\\main\\resources\\HpMedicine.xls");
        FileOutputStream hpMedicinefos = new FileOutputStream(file);

        //对象集合
        List<HpMedicine> hpMedicineList=new ArrayList<HpMedicine>();

        HpMedicine hpMedicine = new HpMedicine();
        hpMedicine.setId(1);
        hpMedicine.setCd(5);
        hpMedicine.setImmediate(true);
        hpMedicine.setKeepTime(0);
        hpMedicine.setReplyValue("500");
        hpMedicine.setSecondValue("3");
        hpMedicine.setName("红烧牛肉丸");
        hpMedicine.setType(Good.HPMEDICINE);
        hpMedicine.setBuyMoney("5000");
        hpMedicineList.add(hpMedicine);

        //设置属性别名（列名）
        LinkedHashMap<String, String> hpMedicineAlias = new LinkedHashMap<String, String>();
        hpMedicineAlias.put("id", "红药的id");
        hpMedicineAlias.put("immediate","红药是否为立即回复药品");
        hpMedicineAlias.put("cd","红药的cd");
        hpMedicineAlias.put("replyValue","红药每秒恢复的血量");
        hpMedicineAlias.put("keepTime","红药持续的时间");
        hpMedicineAlias.put("name","红药持续的名字");
        hpMedicineAlias.put("buyMoney","物品的价值");
        hpMedicineAlias.put("type","红药的种类");
        //标题
        String headLine="HpMedicine表";
        ExcelUtil.pojo2Excel(hpMedicineList, hpMedicinefos, hpMedicineAlias,headLine);
    }
}
