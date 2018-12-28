package context;

import io.netty.channel.Channel;
import order.Order;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/12/24 9:43
 */
public class ProjectUtil {

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

                if (orderMsg.equals("*")) {
                    mT = m;
                    continue;
                }

                String[] orderArr = orderMsg.split(",");
                for (String temp : orderArr) {
                    if (!temp.equals("")) {
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
