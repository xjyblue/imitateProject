package core;


import config.impl.reflect.ReflectMethodLoad;
import core.reflect.InvokeMethod;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import utils.ChannelUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @ClassName ServiceDistributor
 * @Description 服务分发器
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/

@Component
public class ServiceDistributor {
    /**
     * 通过注解，调用相对应的方法
     * @param ch
     * @param msg
     * @throws IOException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public void distributeService(Channel ch, String msg) throws IOException, InvocationTargetException, IllegalAccessException {
        String[] temp = msg.split("=");
        if (ReflectMethodLoad.methodMap.containsKey(temp[0])) {
            String chStatus = ChannelUtil.channelStatus.get(ch);
            if (ReflectMethodLoad.orderStatusMap.get(temp[0]).contains(chStatus)) {
                InvokeMethod invokeMethod = ReflectMethodLoad.methodMap.get(temp[0]);
                invokeMethod.getMethod().invoke(invokeMethod.getObject(), ch, msg);
            }
        }
    }

}
