package test;

import component.Monster;
import skill.MonsterSkill;
import skill.UserSkill;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class InputUtil {
    public static void main(String[]args) throws Exception {
        //将生成的excel转换成文件，还可以用作文件下载
        File file = new File("C:\\Users\\xiaojianyu\\IdeaProjects\\imitateProject\\src\\main\\resources\\Monster.xls");
        FileOutputStream fos = new FileOutputStream(file);

        //对象集合
        List<Monster> pojoList=new ArrayList<Monster>();
        Monster monster = new Monster();
        monster.setId(1);
        monster.setStatus("1");
        monster.setName("七天连锁酒店王");
        monster.setType(Monster.TYPEOFBOSS);
        monster.setValueOfLife("10000");
        monster.setStatus("1");
        monster.setSkillIds("0");

        pojoList.add(monster);
//        NettyMemory.SkillMap.put(userSkill.getSkillId(), userSkill);


//        NettyMemory.SkillMap.put(userSkill.getSkillId(), userSkill);

        //设置属性别名（列名）
        LinkedHashMap<String, String> alias = new LinkedHashMap<String, String>();
        alias.put("id", "怪物id");
        alias.put("name","怪物名称");
        alias.put("type", "怪物类别");
        alias.put("valueOfLife", "怪物生命值");
        alias.put("status", "怪物状态");
        alias.put("skillIds", "怪物技能");
        //标题
        String headLine="MonsterSkill表";
        ExcelUtil.pojo2Excel(pojoList, fos, alias,headLine);
    }
}
