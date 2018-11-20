package task;

import event.EventStatus;
import buff.Buff;
import component.Monster;
import io.netty.channel.Channel;
import memory.NettyMemory;
import pojo.User;

import java.math.BigInteger;
import java.util.Map;

public class AttackBufferTask implements Runnable {
    Map<Channel, User> map = NettyMemory.session2UserIds;
    public void run() {
        for(Map.Entry<Channel, User> entry : map.entrySet()) {
            User user = entry.getValue();
            Channel channel = entry.getKey();
            if(NettyMemory.eventStatus.get(channel).equals(EventStatus.ATTACK)){
                Map<String,Integer> bufferMap = user.getBufferMap();
                for(Map.Entry<String,Integer> entrySecond :bufferMap.entrySet()){
                    Long endTime = null;
                    if(entrySecond.getKey().equals("poisoningBuff")&&entrySecond.getValue()!=2000){
                        endTime = NettyMemory.buffEndTime.get(user).get("poisoningBuff");
                        if(System.currentTimeMillis()>endTime){
                            Monster monster = NettyMemory.monsterMap.get(user).get(0);
                            Buff buff = NettyMemory.buffMap.get(entrySecond.getValue());
                            monster.subLife(new BigInteger(buff.getAddSecondValue()));
                        }else {
                            user.getBufferMap().put("poisoningBuff",2000);
                        }
                    }else if(entrySecond.getKey().equals("defenseBuff")){

                    }
                }
            }
        }
    }
}
