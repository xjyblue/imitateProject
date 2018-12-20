package utils;

import io.netty.channel.Channel;
import context.ProjectContext;
import skill.UserSkill;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description ：nettySpringServer
 * Created by server on 2018/11/26 9:45
 */
public class UserSkillUtil {
    public static UserSkill getUserSkillByKey(Channel channel, String key) {
        if(!ProjectContext.userskillrelationMap.get(channel).containsKey(key)){
            return null;
        }
        return ProjectContext.skillMap.get(ProjectContext.userskillrelationMap.get(channel).get(key).getSkillid());
    }

    public static List<UserSkill> getUserSkillByUserRole(int roleid){
        List<UserSkill> list = new ArrayList<>();
        for(Map.Entry<Integer,UserSkill>entry: ProjectContext.skillMap.entrySet()){
            if(roleid==entry.getValue().getRoleSkill()){
                list.add(entry.getValue());
            }
        }
        return list;
    }
}
