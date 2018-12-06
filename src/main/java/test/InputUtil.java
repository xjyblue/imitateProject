package test;

import level.Level;
import role.Role;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class InputUtil {
    public static void main(String[]args) throws Exception {
        //将生成的excel转换成文件，还可以用作文件下载
        File file = new File("C:\\Users\\xiaojianyu\\IdeaProjects\\imitateProject\\src\\main\\resources\\Level.xls");
        FileOutputStream fos = new FileOutputStream(file);

        //对象集合
        List<Level> pojoList=new ArrayList<Level>();
        Level level  = new Level();
        level.setLevel(1);
//        level.setExperience(10);

        pojoList.add(level);

        //设置属性别名（列名）
        LinkedHashMap<String, String> alias = new LinkedHashMap<String, String>();
        alias.put("level", "等级");
        alias.put("experience","等级所需要的经验");
        //标题
        String headLine="等级经验表";
        ExcelUtil.pojo2Excel(pojoList, fos, alias,headLine);
    }
}
