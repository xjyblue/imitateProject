package utils;

import io.netty.channel.Channel;
import core.order.Order;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @ClassName ReflectMethodUtil
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class ReflectMethodUtil {
    /**
     * 反射
     * @param o
     * @param ch
     * @param msg
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static void reflectAnnotation(Object o, Channel ch, String msg) throws InvocationTargetException, IllegalAccessException {
        Class<?> t = o.getClass();
        Method[] method = t.getMethods();
        boolean flag = true;
        Method mT = null;
        for (Method m : method) {
            boolean isEmpty = m.isAnnotationPresent(Order.class);
            if (isEmpty) {
                Order order = m.getAnnotation(Order.class);
                String orderMsg = order.orderMsg();

                if ("*".equals(orderMsg)) {
                    mT = m;
                    continue;
                }

                String[] orderArr = orderMsg.split(",");
                for (String temp : orderArr) {
                    if (!"".equals(temp)) {
                        if (msg.startsWith(temp)) {
                            m.invoke(o, ch, msg);
                            flag = false;
                        }
                    }
                }
            }
        }
        if (flag && mT != null) {
            mT.invoke(o, ch, msg);
        }
    }

}
