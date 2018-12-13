package test;

import achievement.Achievement;
import component.NPC;
import level.Level;
import org.apache.poi.ss.formula.functions.Npv;
import role.Role;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class InputUtil {
    public static void main(String[]args) throws Exception {
        //将生成的excel转换成文件，还可以用作文件下载
        File file = new File("C:\\Users\\xiaojianyu\\IdeaProjects\\imitateProject\\src\\main\\resources\\NPC.xls");
        FileOutputStream fos = new FileOutputStream(file);

        //对象集合
        List<NPC> pojoList=new ArrayList<NPC>();

        NPC npc = new NPC();
        npc.setId(1);
        npc.setTalk("我是塞里亚，欢迎来到起始之地");
        npc.setName("赛利亚");
        npc.setStatus("1");
        npc.setAreaId(0);
        pojoList.add(npc);

        //设置属性别名（列名）
        LinkedHashMap<String, String> alias = new LinkedHashMap<String, String>();
        alias.put("id", "NPC的id");
        alias.put("status","NPC的状态");
        alias.put("name","NPC的名字");
        alias.put("talk","NPC的话");
        alias.put("areaId","NPC所在的地点");
        //标题
        String headLine="NPC表";
        ExcelUtil.pojo2Excel(pojoList, fos, alias,headLine);
    }
}
