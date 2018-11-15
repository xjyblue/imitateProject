package task;

import component.MpMedicine;
import io.netty.channel.Channel;
import memory.NettyMemory;
import pojo.User;
import java.math.BigInteger;
import java.util.Map;

public class MpTask implements Runnable {

    private BigInteger add = new BigInteger("10");

    @Override
    public void run() {
        Map<Channel, User> map = NettyMemory.session2UserIds;
        for (Map.Entry<Channel, User> entry : map.entrySet()) {
//          自动回蓝
            BigInteger userMp = new BigInteger(entry.getValue().getMp());
            BigInteger maxMp = new BigInteger("10000");
            User user = entry.getValue();
            if (userMp.compareTo(maxMp) < 0) {
                if (user.getBufferMap().get("mpBuff").equals(1000)) {
                    userMp = userMp.add(add);
                    entry.getValue().setMp(userMp.toString());
                } else {
                    MpMedicine mpMedicine = NettyMemory.mpMedicineMap.get(user.getBufferMap().get("mpBuff"));
                    Long endTime = null;
                    if (!NettyMemory.buffEndTime.containsKey(user)) {
                        endTime = System.currentTimeMillis() + mpMedicine.getKeepTime() * 1000;
                        NettyMemory.buffEndTime.get(user).put("mpBuff",endTime);
                    } else {
                        endTime = NettyMemory.buffEndTime.get(user).get("mpBuff");
                    }
                    Long currentTime = System.currentTimeMillis();
                    if (endTime > currentTime) {
                        userMp = userMp.add(new BigInteger(mpMedicine.getSecondValue()));
                        if(userMp.compareTo(maxMp)>=0){
                            userMp = maxMp;
                        }
                        user.setMp(userMp.toString());
                    } else {
                        user.getBufferMap().put("mpBuff",1000);
                        userMp = userMp.add(new BigInteger("10"));
                        if(userMp.compareTo(maxMp)>=0){
                            userMp = maxMp;
                        }
                        user.setMp(userMp.toString());
                    }
                }
            }
            System.out.println(user.getUsername()+"--的蓝量--"+userMp.toString());
        }

    }
}
