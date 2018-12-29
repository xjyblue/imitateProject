package test;

import component.BossSceneConfig;
import component.CollectGood;

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
        FileInputStream bossSceneConfigfis = new FileInputStream(new File("src/main/resources/BossSceneConfig.xls"));
        LinkedHashMap<String, String> bossSceneConfigfisAlias = new LinkedHashMap<>();
        bossSceneConfigfisAlias.put("副本的id","bossSceneId");
        bossSceneConfigfisAlias.put("副本场景的顺序","sequences");
        bossSceneConfigfisAlias.put("副本的持续时间","keeptime");
        bossSceneConfigfisAlias.put("副本的名字","bossSceneName");

        List<BossSceneConfig> bossSceneConfigList = ExcelUtil.excel2Pojo(bossSceneConfigfis, BossSceneConfig.class,bossSceneConfigfisAlias);

        System.out.println(UUID.randomUUID().toString());
    }
}

