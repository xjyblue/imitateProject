package test;

import buffer.Buff;
import component.Equipment;
import memory.NettyMemory;
import skill.UserSkill;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class InputUtil {
    public static void main(String[]args) throws Exception {
        //将生成的excel转换成文件，还可以用作文件下载
        File file = new File("C:\\Users\\xiaojianyu\\IdeaProjects\\imitateProject\\src\\main\\resources\\Equipment.xls");
        FileOutputStream fos = new FileOutputStream(file);
        //对象集合
        List<Equipment> pojoList=new ArrayList<Equipment>();
        Equipment equipment = new Equipment(1004,"新手刀",10,300);
        pojoList.add(equipment);


        //设置属性别名（列名）
        LinkedHashMap<String, String> alias = new LinkedHashMap<String, String>();
        alias.put("id", "武器id");
        alias.put("name","武器名称");
        alias.put("durability", "武器耐久度");
        alias.put("addValue", "武器增加伤害");

        //标题
        String headLine="Equipment表";
        ExcelUtil.pojo2Excel(pojoList, fos, alias,headLine);
    }
}
