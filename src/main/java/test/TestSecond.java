package test;

import buff.Buff;
import component.Equipment;
import memory.NettyMemory;

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
        FileInputStream fis = new FileInputStream(new File("C:\\Users\\xiaojianyu\\IdeaProjects\\imitateProject\\src\\main\\resources\\Buff.xls"));
        LinkedHashMap<String, String> alias = new LinkedHashMap<>();
        alias.put("buff名称", "name");
        alias.put("每秒造成伤害","addSecondValue");
        alias.put("buff类别", "type");
        alias.put("buffId", "bufferId");
        alias.put("持续时间", "keepTime");
        alias.put("每秒减免伤害", "injurySecondValue");
        List<Buff> buffList = ExcelUtil.excel2Pojo(fis,Buff.class, alias);
        for(Buff buff:buffList){
            NettyMemory.buffMap.put(buff.getBufferId(),buff);
        }
//        System.out.println(UUID.randomUUID().toString());
    }
}

