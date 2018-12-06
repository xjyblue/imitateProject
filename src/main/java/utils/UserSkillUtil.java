package utils;

import io.netty.channel.Channel;
import memory.NettyMemory;
import skill.UserSkill;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/11/26 9:45
 */
public class UserSkillUtil {
    public static UserSkill getUserSkillByKey(Channel channel, String key) {
        if(!NettyMemory.userskillrelationMap.get(channel).containsKey(key)){
            return null;
        }
        return NettyMemory.SkillMap.get(NettyMemory.userskillrelationMap.get(channel).get(key).getSkillid());
    }

    public static List<UserSkill> getUserSkillByUserRole(int roleid){
        List<UserSkill> list = new ArrayList<>();
        for(Map.Entry<Integer,UserSkill>entry: NettyMemory.SkillMap.entrySet()){
            if(roleid==entry.getValue().getRoleSkill()){
                list.add(entry.getValue());
            }
        }
        return list;
    }
}
