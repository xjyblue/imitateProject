package service.loginservice.service;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import core.annotation.Region;
import core.config.GrobalConfig;
import login.entity.LoginUserTask;
import login.thread.LoginThreadPool;
import core.config.MessageConfig;
import core.ServiceDistributor;
import core.channel.ChannelStatus;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import mapper.UserMapper;
import mapper.UserskillrelationMapper;
import core.annotation.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.buffservice.service.UserBuffService;
import service.userservice.service.UserService;
import utils.MessageUtil;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName LoginService
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
@Slf4j
@Region
public class LoginService {
    /**
     * 登录逻辑
     *
     * @param channel
     * @param msg
     */
    @Order(orderMsg = "login", status = {ChannelStatus.LOGIN})
    public void login(Channel channel, String msg) {
        String[] temp = msg.split("=");
        if (temp.length != GrobalConfig.THREE) {
            channel.writeAndFlush(MessageUtil.turnToPacket(MessageConfig.ERRORORDER));
            return;
        }

//      一致性hash算法的计算,根据登录名把登录任务分配到不同的线程中去处理
        int bucket = Hashing.consistentHash(Hashing.sha512().hashString(temp[1], Charsets.UTF_8), GrobalConfig.LOGIN_THEAD_NUM);
        LoginUserTask loginUserTask = new LoginUserTask(temp[1], temp[2], channel);
        LoginThreadPool.LOGIN_THREAD_TASK_MAP.get(bucket).getLoginUserTaskQueue().add(loginUserTask);

    }
}
