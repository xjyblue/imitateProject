package service.petservice.service.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName PetConfig
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/9 10:35
 * @Version 1.0
 **/
public class PetConfig {
    /**
     * 宠物的id
     */
    private String id;
    /**
     * 宠物的名字
     */
    private String name;
    /**
     * 宠物的技能读入
     */
    private String skills;
    /**
     * 宠物技能取出
     */
    private Map<String,PetSkillConfig> petSkillConfigMap = new HashMap<>();

    public Map<String, PetSkillConfig> getPetSkillConfigMap() {
        return petSkillConfigMap;
    }

    public void setPetSkillConfigMap(Map<String, PetSkillConfig> petSkillConfigMap) {
        this.petSkillConfigMap = petSkillConfigMap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
