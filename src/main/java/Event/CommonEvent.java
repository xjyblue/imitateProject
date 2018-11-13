package Event;

import io.netty.channel.Channel;
import memory.NettyMemory;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.Userbag;
import utils.DelimiterUtils;
import component.MpMedicine;

import java.math.BigInteger;

@Component("commonEvent")
public class CommonEvent {

    public void common(Channel channel, String msg) {
        if (msg.equals("b")) {
            User user = NettyMemory.session2UserIds.get(channel);
            String bagResp = System.getProperty("line.separator")
                    + "按b-物品编号使用蓝药";
            for (Userbag userbag : user.getUserBag()) {
                MpMedicine mpMedicine = NettyMemory.mpMedicineMap.get(userbag.getWid());
                bagResp += System.getProperty("line.separator")
                        + "物品id" + mpMedicine.getId()
                        + "----药品恢复蓝量:" + mpMedicine.getReplyValue();
                if (!mpMedicine.isImmediate()) {
                    bagResp += "----每秒恢复" + mpMedicine.getSecondValue() + "----持续" + mpMedicine.getKeepTime() + "秒";
                } else {
                    bagResp += "----即时回复";
                }
                bagResp += "----数量:" + userbag.getNum();
            }
            channel.writeAndFlush(DelimiterUtils.addDelimiter(bagResp));
        }
        if (msg.startsWith("b-")) {
            String temp[] = msg.split("-");
            User user = NettyMemory.session2UserIds.get(channel);
            if (NettyMemory.mpMedicineMap.containsKey(Integer.parseInt(temp[1]))) {
                MpMedicine mpMedicine = NettyMemory.mpMedicineMap.get(Integer.parseInt(temp[1]));
                Userbag userbagNow = null;
                Userbag userbagRemove = null;
                for (Userbag userbag : user.getUserBag()) {
                    if (userbag.getWid() == Integer.parseInt(temp[1])) {
                        userbagNow = userbag;
                        userbag.setNum(userbag.getNum() - 1);
                        if (userbag.getNum() == 0) {
                            userbagRemove = userbag;
                        }
                    }
                }
                if (userbagRemove != null) {
                    user.getUserBag().remove(userbagRemove);
                }
                if (userbagNow == null) {
                    channel.writeAndFlush(DelimiterUtils.addDelimiter("背包中该物品为空"));
                    return;
                }
                if (mpMedicine.isImmediate()) {
                    BigInteger userMp = new BigInteger(user.getMp());
                    userMp = userMp.add(new BigInteger(mpMedicine.getReplyValue()));
                    BigInteger maxMp = new BigInteger("10000");
                    if (userMp.compareTo(maxMp) >= 0) {
                        user.setMp(maxMp.toString());
                    } else {
                        user.addMp(mpMedicine.getReplyValue());
                    }
                } else {
                    if (user.getMpBuffer() == mpMedicine.getId()) {
                        NettyMemory.mpEndTime.put(user, (System.currentTimeMillis() + mpMedicine.getKeepTime() * 1000));
                    }else{
                        if(NettyMemory.mpEndTime.containsKey(user)){
                            NettyMemory.mpEndTime.remove(user);
                        }
                    }
                    user.setMpBuffer(mpMedicine.getId());
                }
            } else {
                channel.writeAndFlush(DelimiterUtils.addDelimiter("该物品不存在"));
            }
        }

    }
}
