package test;

import component.CollectGood;
import component.HpMedicine;
import component.Scene;

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
        FileInputStream scenefis = new FileInputStream(new File("src/main/resources/CollectGood.xls"));
        LinkedHashMap<String, String> collectGoodAlias = new LinkedHashMap<>();
        collectGoodAlias.put("物品的名字","name");
        collectGoodAlias.put("物品的id","id");
        collectGoodAlias.put("物品的描述","desc");
        collectGoodAlias.put("物品的价格","buyMoney");
        collectGoodAlias.put("物品的种类","type");
        List<CollectGood> sceneList = ExcelUtil.excel2Pojo(scenefis, CollectGood.class,collectGoodAlias);

        System.out.println(UUID.randomUUID().toString());
    }
}

