package config.impl.excel;

import com.google.common.collect.Maps;
import config.interf.IResourceLoad;
import core.component.role.Role;
import org.springframework.stereotype.Component;
import utils.ExcelUtil;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName RoleResourceLoad
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/15 15:13
 * @Version 1.0
 **/
@Component
public class RoleResourceLoad implements IResourceLoad {

    /**
     * 职业的构建
     */
    public final static Map<Integer, Role> roleMap = Maps.newHashMap();

    @PostConstruct
    @Override
    public void load() {
        //      初始化角色start
        try {
            FileInputStream rolefis = new FileInputStream(new File("src/main/resources/Role.xls"));
            LinkedHashMap<String, String> rolealias = new LinkedHashMap<>();
            rolealias.put("角色id", "roleId");
            rolealias.put("角色名称", "name");
            rolealias.put("角色技能", "skills");
            rolealias.put("角色防御力", "defense");
            List<Role> roleList = ExcelUtil.excel2Pojo(rolefis, Role.class, rolealias);
            for (Role role : roleList) {
                roleMap.put(role.getRoleId(), role);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//      初始化角色end
    }
}
