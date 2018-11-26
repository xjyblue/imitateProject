package skill;

import io.netty.channel.Channel;
import memory.NettyMemory;

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
}
