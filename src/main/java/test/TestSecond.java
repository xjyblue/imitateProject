package test;

import buff.Buff;
import component.Equipment;
import component.Monster;
import memory.NettyMemory;
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
        FileInputStream fis = new FileInputStream(new File("C:\\Users\\xiaojianyu\\IdeaProjects\\imitateProject\\src\\main\\resources\\Monster.xls"));
        LinkedHashMap<String, String> alias = new LinkedHashMap<>();
        alias.put("怪物id","id");
        alias.put("怪物名称","name");
        alias.put("怪物类别","type");
        alias.put("怪物生命值","valueOfLife");
        alias.put("怪物状态","status");
        alias.put("怪物技能","skillIds");
        List<Monster> monsterList = ExcelUtil.excel2Pojo(fis, Monster.class, alias);

        System.out.println(UUID.randomUUID().toString());
    }
}

