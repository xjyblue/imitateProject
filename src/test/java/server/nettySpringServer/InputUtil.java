package server.nettySpringServer;

import service.petservice.service.entity.PetConfig;
import service.petservice.service.entity.PetSkillConfig;
import utils.ExcelUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @ClassName InputUtil
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/10 9:36
 * @Version 1.0
 **/
public class InputUtil {
    public static void main(String[] args) throws Exception {
        //将生成的excel转换成文件，还可以用作文件下载
        File file = new File("src/main/resources/PetSkillConfig.xls");
        FileOutputStream petSkillConfigfos = new FileOutputStream(file);

        //对象集合
        List<PetSkillConfig> petSkillConfigList = new ArrayList<>();
        PetSkillConfig petSkillConfig = new PetSkillConfig();
        petSkillConfig.setId("1");
        petSkillConfig.setSkillName("旭旭轻吻");
        petSkillConfig.setPetId("1");
        petSkillConfig.setDamage(50);
        petSkillConfigList.add(petSkillConfig);


        //设置属性别名（列名）
        LinkedHashMap<String, String> petSkillConfigAlias = new LinkedHashMap<String, String>();
        petSkillConfigAlias.put("skillName", "技能名");
        petSkillConfigAlias.put("id", "技能的id");
        petSkillConfigAlias.put("petId", "技能所属宠物");
        petSkillConfigAlias.put("damage", "技能伤害");

        //标题
        String headLine = "petSkillConfig表";
        ExcelUtil.pojo2Excel(petSkillConfigList, petSkillConfigfos, petSkillConfigAlias, headLine);
    }
}
