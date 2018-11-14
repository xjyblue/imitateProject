package test;

import buffer.Buff;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * xiaojianyu
 */
public class TestSecond {
    public static void main(String[] argv) throws Exception {
        //指定输入文件
        FileInputStream fis = new FileInputStream(new File("C:\\Users\\xiaojianyu\\IdeaProjects\\imitateProject\\src\\main\\resources\\Buff.xls"));
        //指定每列对应的类属性
        LinkedHashMap<String, String> alias = new LinkedHashMap<>();
        alias.put("buff名称", "name");
        alias.put("每秒回复时间","addSecondValue");
        alias.put("buff类别", "type");
        alias.put("buffId", "bufferId");
        alias.put("持续时间", "keepTime");
        alias.put("每秒减免伤害", "injurySecondValue");
        //转换成指定类型的对象数组
        List<Buff> pojoList = ExcelUtil.excel2Pojo(fis, Buff.class, alias);
        System.out.println("kk");
    }
}

