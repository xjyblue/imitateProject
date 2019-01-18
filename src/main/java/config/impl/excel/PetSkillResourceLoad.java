package config.impl.excel;

import com.google.common.collect.Maps;
import config.interf.IResourceLoad;
import org.springframework.stereotype.Component;
import service.petservice.service.entity.PetSkillConfig;
import utils.ExcelUtil;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName PetResourceLoad
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/15 14:13
 * @Version 1.0
 **/
@Component
public class PetSkillResourceLoad implements IResourceLoad {

    /**
     * 宠物技能配置
     */
    public final static Map<String, PetSkillConfig> petSkillConfigMap = Maps.newHashMap();

    @PostConstruct
    @Override
    public void load() {
        try {
            FileInputStream petConfigfis = new FileInputStream(new File("src/main/resources/PetSkillConfig.xls"));
            LinkedHashMap<String, String> petSkillConfigAlias = new LinkedHashMap<>();
            petSkillConfigAlias.put("技能名", "skillName");
            petSkillConfigAlias.put("技能的id", "id");
            petSkillConfigAlias.put("技能所属宠物", "petId");
            petSkillConfigAlias.put("技能伤害", "damage");
            List<PetSkillConfig> petSkillConfigList = ExcelUtil.excel2Pojo(petConfigfis, PetSkillConfig.class, petSkillConfigAlias);
            for (PetSkillConfig petSkillConfig : petSkillConfigList) {
                petSkillConfigMap.put(petSkillConfig.getId(), petSkillConfig);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
