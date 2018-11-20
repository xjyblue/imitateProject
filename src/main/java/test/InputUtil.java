package test;

import skill.UserSkill;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class InputUtil {
    public static void main(String[]args) throws Exception {
        //将生成的excel转换成文件，还可以用作文件下载
        File file = new File("C:\\Users\\xiaojianyu\\IdeaProjects\\imitateProject\\src\\main\\resources\\UserSkill.xls");
        FileOutputStream fos = new FileOutputStream(file);

        //对象集合
        List<UserSkill> pojoList=new ArrayList<UserSkill>();
        UserSkill userSkill = new UserSkill();
        userSkill.setSkillId(1);
        userSkill.setSkillName("烈火攻击");
        userSkill.setAttackCd(10000l);
        userSkill.setDamage("10000");
        userSkill.setSkillMp("1000");
        pojoList.add(userSkill);
//        NettyMemory.SkillMap.put(userSkill.getSkillId(), userSkill);

        userSkill = new UserSkill();
        userSkill.setSkillId(2);
        userSkill.setSkillName("喷水攻击");
        userSkill.setAttackCd(2000l);
        userSkill.setDamage("120");
        userSkill.setSkillMp("500");
        pojoList.add(userSkill);
//        NettyMemory.SkillMap.put(userSkill.getSkillId(), userSkill);

        //设置属性别名（列名）
        LinkedHashMap<String, String> alias = new LinkedHashMap<String, String>();
        alias.put("skillId", "技能id");

        alias.put("skillName","技能名称");

        alias.put("attackCd", "技能攻击时间");

        alias.put("damage", "技能伤害");

        alias.put("skillMp", "技能消耗Mp");

        //标题
        String headLine="UserSkill表";
        ExcelUtil.pojo2Excel(pojoList, fos, alias,headLine);
    }
}
