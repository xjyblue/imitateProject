package test;

import component.HpMedicine;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

/**
 * server
 */
public class TestSecond {
    public static void main(String[] argv) throws Exception {
        //指定输入文件
        FileInputStream hpfis = new FileInputStream(new File("C:\\Users\\server\\IdeaProjects\\imitateProject\\src\\main\\resources\\HpMedicine.xls"));
        LinkedHashMap<String, String> hpMedicineAlias = new LinkedHashMap<>();
        hpMedicineAlias.put("红药的id","id");
        hpMedicineAlias.put("红药是否为立即回复药品","immediate");
        hpMedicineAlias.put("红药的cd","cd");
        hpMedicineAlias.put("红药每秒恢复的血量","replyValue");
        hpMedicineAlias.put("红药持续的时间","keepTime");
        hpMedicineAlias.put("红药持续的名字","name");
        hpMedicineAlias.put("物品的价值","buyMoney");
        hpMedicineAlias.put("红药的种类","type");
        List<HpMedicine> levelList = ExcelUtil.excel2Pojo(hpfis, HpMedicine.class, hpMedicineAlias);

        System.out.println(UUID.randomUUID().toString());
    }
}

