package config.impl.reflect;

import com.google.common.collect.Maps;
import config.interf.IResourceLoad;
import core.annotation.Region;
import core.config.GrobalConfig;
import core.reflect.InvokeMethod;
import core.annotation.Order;
import org.springframework.stereotype.Component;
import utils.SpringContextUtil;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName ReflectMethodUtil
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
@Component
public class ReflectMethodLoad implements IResourceLoad {

    /**
     * 指令-方法
     */
    public final static Map<String, InvokeMethod> methodMap = Maps.newHashMap();
    /**
     * 指令-状态
     */
    public final static Map<String, Set<String>> orderStatusMap = Maps.newHashMap();

    @PostConstruct
    @Override
    public void load() {
        scanRegionAnno();
    }

    /**
     * 找到领域注解
     */
    public void scanRegionAnno() {
        Map<String, Object> map = SpringContextUtil.getApplicationContext().getBeansWithAnnotation(Region.class);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            scanOrderAnno(entry.getValue());
        }
    }

    /**
     * 反射
     *
     * @param o
     */
    public void scanOrderAnno(Object o) {
        Class<?> t = o.getClass();
        Method[] method = t.getMethods();
        for (Method m : method) {
            boolean isEmpty = m.isAnnotationPresent(Order.class);
            if (isEmpty) {
                Order order = m.getAnnotation(Order.class);
                String orderMsg = order.orderMsg();
                String[] status = order.status();
                InvokeMethod invokeMethod = new InvokeMethod();
                invokeMethod.setObject(o);
                invokeMethod.setMethod(m);
                if (!order.ifRandomkey()) {
//                  加载命令方法
                    invokeMethod.setOrder(orderMsg);
                    methodMap.put(invokeMethod.getOrder(), invokeMethod);
                    orderStatusMap.put(invokeMethod.getOrder(), new HashSet<String>());
//                  加载命令状态
                    for (String statusT : status) {
                        orderStatusMap.get(invokeMethod.getOrder()).add(statusT);
                    }
                } else {
//                  加载特殊键位
                    int count = 1;
                    while (count < GrobalConfig.TEN) {
                        invokeMethod.setOrder(count + "");
                        methodMap.put(invokeMethod.getOrder(), invokeMethod);
                        orderStatusMap.put(invokeMethod.getOrder(), new HashSet<String>());
                        for (String statusT : status) {
                            orderStatusMap.get(invokeMethod.getOrder()).add(statusT);
                        }
                        count++;
                    }
                }
            }
        }
    }
}
