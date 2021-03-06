package service.petservice.service.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName Pet
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/10 12:22
 * @Version 1.0
 **/
public class Pet {
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
    private List<PetSkillConfig> skillList = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<PetSkillConfig> getSkillList() {
        return skillList;
    }

    public void setSkillList(List<PetSkillConfig> skillList) {
        this.skillList = skillList;
    }
}
