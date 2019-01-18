package config.impl.excel;

import com.google.common.collect.Maps;
import config.interf.IResourceLoad;
import core.config.GrobalConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.buffservice.entity.Buff;
import service.skillservice.entity.UserSkill;
import utils.ExcelUtil;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName UserSkillResourceLoad
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/15 16:32
 * @Version 1.0
 **/
@Component
public class UserSkillResourceLoad implements IResourceLoad {
    @Autowired
    private BuffResourceLoad buffExcelParse;
    /**
     * 全局技能伤害
     */
    public final static Map<Integer, UserSkill> skillMap = Maps.newHashMap();

    @PostConstruct
    @Override
    public void load() {
        //        初始化人物技能表start
        try {
            FileInputStream userSkillfis = new FileInputStream(new File("src/main/resources/UserSkill.xls"));
            LinkedHashMap<String, String> userSkillalias = new LinkedHashMap<>();
            userSkillalias.put("技能id", "skillId");
            userSkillalias.put("技能名称", "skillName");
            userSkillalias.put("技能攻击时间", "attackCd");
            userSkillalias.put("技能伤害", "damage");
            userSkillalias.put("技能消耗Mp", "skillMp");
            userSkillalias.put("技能附带buffId", "bufferMapId");
            userSkillalias.put("技能所属种族", "roleSkill");
            List<UserSkill> userSkillList = ExcelUtil.excel2Pojo(userSkillfis, UserSkill.class, userSkillalias);
            for (UserSkill userSkill : userSkillList) {
                Map<String, Integer> map = new HashMap<>(64);
                userSkill.setBuffMap(map);
                skillMap.put(userSkill.getSkillId(), userSkill);
                if (!userSkill.getBufferMapId().equals(GrobalConfig.NULL)) {
                    String[] temp = userSkill.getBufferMapId().split("-");
                    for (int i = 0; i < temp.length; i++) {
                        Buff buffTemp = BuffResourceLoad.buffMap.get(Integer.parseInt(temp[i]));
                        userSkill.getBuffMap().put(buffTemp.getTypeOf(), buffTemp.getBufferId());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        初始化技能表end
    }
}
