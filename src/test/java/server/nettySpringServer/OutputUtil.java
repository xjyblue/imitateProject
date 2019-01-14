package server.nettySpringServer;

import service.petservice.service.entity.PetConfig;
import utils.ExcelUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

/**
 * @ClassName OutputUtil
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/10 9:52
 * @Version 1.0
 **/
public class OutputUtil {
    public static void main(String[] argv) throws Exception {
        //指定输入文件
        FileInputStream petConfigfis = new FileInputStream(new File("src/main/resources/PetConfig.xls"));
        LinkedHashMap<String, String> petConfigAlias = new LinkedHashMap<>();
        petConfigAlias.put("宠物名","name");
        petConfigAlias.put("宠物的id","id");
        petConfigAlias.put("宠物的技能","skills");
        List<PetConfig> petConfigList = ExcelUtil.excel2Pojo(petConfigfis, PetConfig.class,petConfigAlias);

        System.out.println(UUID.randomUUID().toString());
    }
}
