package config.impl.excel;

import com.google.common.collect.Maps;
import config.interf.IResourceLoad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.petservice.service.entity.PetConfig;
import service.petservice.service.entity.PetSkillConfig;
import utils.ExcelUtil;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName PetResourceLoad
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/15 14:30
 * @Version 1.0
 **/
@Component
public class PetResourceLoad implements IResourceLoad {
    /**
     * 宠物配置
     */
    public final static Map<String, PetConfig> petConfigMap = Maps.newHashMap();

    @Autowired
    private PetSkillResourceLoad petSkillExcelParse;

    @PostConstruct
    @Override
    public void load() {
        try {
            FileInputStream petConfigfis = new FileInputStream(new File("src/main/resources/PetConfig.xls"));
            LinkedHashMap<String, String> petConfigAlias = new LinkedHashMap<>();
            petConfigAlias.put("宠物名", "name");
            petConfigAlias.put("宠物的id", "id");
            petConfigAlias.put("宠物的技能", "skills");
            List<PetConfig> petConfigList = ExcelUtil.excel2Pojo(petConfigfis, PetConfig.class, petConfigAlias);
            for (PetConfig petConfig : petConfigList) {
                for (Map.Entry<String, PetSkillConfig> entry : PetSkillResourceLoad.petSkillConfigMap.entrySet()) {
                    PetSkillConfig petSkillConfig = entry.getValue();
                    if (petSkillConfig.getPetId().equals(petConfig.getId())) {
                        petConfig.getPetSkillConfigMap().put(petSkillConfig.getId(), petSkillConfig);
                    }
                }
                petConfigMap.put(petConfig.getId(), petConfig);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
