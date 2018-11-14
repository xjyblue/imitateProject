package test;

import buffer.Buff;
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
        File file = new File("C:\\Users\\xiaojianyu\\IdeaProjects\\imitateProject\\src\\main\\resources\\UserSkill.xls");
        FileOutputStream fos = new FileOutputStream(file);
        //对象集合
        List<UserSkill> pojoList=new ArrayList<UserSkill>();

        UserSkill userSkill = new UserSkill();
        userSkill.setSkillId(1);
        userSkill.setSkillName("烈火攻击");
        userSkill.setAttackCd(10000l);
        userSkill.setDamage("10000");
        userSkill.setSkillMp("600");

        pojoList.add(userSkill);

        userSkill = new UserSkill();
        userSkill.setSkillId(2);
        userSkill.setSkillName("喷水攻击");
        userSkill.setAttackCd(2000l);
        userSkill.setDamage("120");
        userSkill.setSkillMp("1000");
        pojoList.add(userSkill);

        //设置属性别名（列名）
        LinkedHashMap<String, String> alias = new LinkedHashMap<String, String>();
        alias.put("skillId", "技能id");
        alias.put("skillName","技能名称");
        alias.put("attackCd", "攻击Cd");
        alias.put("damage", "伤害");
        alias.put("keepTime", "持续时间");
        alias.put("skillMp", "技能消耗mp值");

        //标题
        String headLine="Userskill表";
        ExcelUtil.pojo2Excel(pojoList, fos, alias,headLine);
    }
}
