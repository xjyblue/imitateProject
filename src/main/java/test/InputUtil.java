package test;

import component.boss.BossSceneConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class InputUtil {
    public static void main(String[]args) throws Exception {
        //将生成的excel转换成文件，还可以用作文件下载
        File file = new File("src/main/resources/BossSceneConfig.xls");
        FileOutputStream hpMedicinefos = new FileOutputStream(file);

        //对象集合
        List<BossSceneConfig> hpMedicineList=new ArrayList<>();
        BossSceneConfig bossSceneConfig = new BossSceneConfig();
        bossSceneConfig.setBossSceneId("1");
        bossSceneConfig.setBossSceneName("天魔灵殿");
        bossSceneConfig.setKeeptime(5000L);
        bossSceneConfig.setSequences("A0-A1-A2-A3");




        //设置属性别名（列名）
        LinkedHashMap<String, String> hpMedicineAlias = new LinkedHashMap<String, String>();
        hpMedicineAlias.put("bossSceneId","副本的id");
        hpMedicineAlias.put("sequences","副本场景的顺序");
        hpMedicineAlias.put("keeptime","副本的持续时间");
        hpMedicineAlias.put("bossSceneName","副本的名字");

        //标题
        String headLine="BossSceneConfig表";
        ExcelUtil.pojo2Excel(hpMedicineList, hpMedicinefos, hpMedicineAlias,headLine);
    }
}
