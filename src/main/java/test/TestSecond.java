package test;

import component.Equipment;

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
        FileInputStream fis = new FileInputStream(new File("C:\\Users\\xiaojianyu\\IdeaProjects\\imitateProject\\src\\main\\resources\\Equipment.xls"));
        //指定每列对应的类属性
        LinkedHashMap<String, String> alias = new LinkedHashMap<>();
        alias.put("武器id","id");
        alias.put("武器名称","name");
        alias.put("武器耐久度","durability");
        alias.put("武器增加伤害","addValue");

        //转换成指定类型的对象数组
        List<Equipment> pojoList = ExcelUtil.excel2Pojo(fis, Equipment.class, alias);
        System.out.println("kk");
    }
}

