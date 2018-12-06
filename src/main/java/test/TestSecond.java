package test;

import buff.Buff;
import component.Equipment;
import component.Monster;
import level.Level;
import memory.NettyMemory;
import role.Role;
import skill.MonsterSkill;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

/**
 * xiaojianyu
 */
public class TestSecond {
    public static void main(String[] argv) throws Exception {
        //指定输入文件
        FileInputStream levelfis = new FileInputStream(new File("C:\\Users\\xiaojianyu\\IdeaProjects\\imitateProject\\src\\main\\resources\\Level.xls"));
        LinkedHashMap<String, String> levelalias = new LinkedHashMap<>();
        levelalias.put("等级","level");
        levelalias.put("等级所需要的经验","experience");
        List<Level> levelList = ExcelUtil.excel2Pojo(levelfis, Level.class, levelalias);

        System.out.println(UUID.randomUUID().toString());
    }
}

