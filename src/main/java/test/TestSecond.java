package test;

import achievement.Achievement;
import buff.Buff;
import component.Equipment;
import component.Monster;
import component.NPC;
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
        FileInputStream npcfis = new FileInputStream(new File("C:\\Users\\xiaojianyu\\IdeaProjects\\imitateProject\\src\\main\\resources\\NPC.xls"));
        LinkedHashMap<String, String> npcalias = new LinkedHashMap<>();
        npcalias.put("NPC的id","id");
        npcalias.put("NPC的状态","status");
        npcalias.put("NPC的名字","name");
        npcalias.put("NPC的话","talk");
        npcalias.put("NPC所在的地点","areaId");
        List<NPC> levelList = ExcelUtil.excel2Pojo(npcfis,NPC.class, npcalias);

        System.out.println(UUID.randomUUID().toString());
    }
}

