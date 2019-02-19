package service.loginservice.service;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import core.annotation.order.OrderRegion;
import core.config.GrobalConfig;
import core.config.OrderConfig;
import core.packet.ServerPacket;
import login.entity.LoginUserTask;
import login.thread.LoginThreadPool;
import core.config.MessageConfig;
import core.channel.ChannelStatus;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import core.annotation.order.Order;
import org.springframework.stereotype.Component;
import utils.MessageUtil;

/**
 * @ClassName LoginService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
@Slf4j
@OrderRegion
public class LoginService {
    /**
     * 登录逻辑
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = OrderConfig.LOGIN_ORDER, status = {ChannelStatus.LOGIN})
    public void login(Channel channel, String msg) {
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.THREE) {
            ServerPacket.NormalResp.Builder builder = ServerPacket.NormalResp.newBuilder();
            builder.setData(MessageConfig.ERROR_ORDER);
            MessageUtil.sendMessage(channel, builder.build());
            return;
        }

//      一致性hash算法的计算,根据登录名把登录任务分配到不同的线程中去处理
        int bucket = Hashing.consistentHash(Hashing.sha512().hashString(temp[1], Charsets.UTF_8), GrobalConfig.LOGIN_THEAD_NUM);
        LoginUserTask loginUserTask = new LoginUserTask(temp[1], temp[2], channel);
        LoginThreadPool.LOGIN_THREAD_TASK_MAP.get(bucket).getLoginUserTaskQueue().add(loginUserTask);

    }
}
