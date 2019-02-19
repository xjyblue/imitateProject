package config.impl.reflect;

import com.google.common.collect.Maps;
import config.interf.IResourceLoad;
import core.annotation.good.GoodGet;
import core.annotation.good.GoodRegion;
import core.annotation.order.OrderRegion;
import core.config.GrobalConfig;
import core.reflect.son.GoodRewardInvokeMethod;
import core.reflect.son.OrderInvokeMethod;
import core.annotation.order.Order;
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
    public final static Map<String, OrderInvokeMethod> methodMap = Maps.newHashMap();

    /**
     * 物品种类-方法
     */
    public final static Map<String, GoodRewardInvokeMethod> goodRewardInvokeMethodMap = Maps.newHashMap();

    /**
     * 指令-状态
     */
    public final static Map<String, Set<String>> orderStatusMap = Maps.newHashMap();

    @PostConstruct
    @Override
    public void load() {
        scanRegionAnno();
        scanGoodRegion();
    }

    private void scanGoodRegion() {
        Map<String, Object> map = SpringContextUtil.getApplicationContext().getBeansWithAnnotation(GoodRegion.class);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            scanGoodGetAnno(entry.getValue());
        }
    }

    private void scanGoodGetAnno(Object o) {
        Class<?> t = o.getClass();
        Method[] method = t.getMethods();
        for (Method m : method) {
            boolean isEmpty = m.isAnnotationPresent(GoodGet.class);
            if(isEmpty){
                GoodGet goodGet = m.getAnnotation(GoodGet.class);
                GoodRewardInvokeMethod goodRewardInvokeMethod = new GoodRewardInvokeMethod();
                goodRewardInvokeMethod.setType(goodGet.type());
                goodRewardInvokeMethod.setMethod(m);
                goodRewardInvokeMethod.setObject(o);
                goodRewardInvokeMethodMap.put(goodGet.type(),goodRewardInvokeMethod);
            }
        }
    }

    /**
     * 找到命令领域注解
     */
    public void scanRegionAnno() {
        Map<String, Object> map = SpringContextUtil.getApplicationContext().getBeansWithAnnotation(OrderRegion.class);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            scanOrderAnno(entry.getValue());
        }
    }

    /**
     * 存放反射
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
                OrderInvokeMethod orderInvokeMethod = new OrderInvokeMethod();
                orderInvokeMethod.setObject(o);
                orderInvokeMethod.setMethod(m);
                if (!order.ifRandomkey()) {
//                  加载命令方法
                    orderInvokeMethod.setOrder(orderMsg);
                    methodMap.put(orderInvokeMethod.getOrder(), orderInvokeMethod);
                    orderStatusMap.put(orderInvokeMethod.getOrder(), new HashSet<String>());
//                  加载命令状态
                    for (String statusT : status) {
                        orderStatusMap.get(orderInvokeMethod.getOrder()).add(statusT);
                    }
                } else {
//                  加载特殊键位
                    int count = 1;
                    while (count < GrobalConfig.TEN) {
                        orderInvokeMethod.setOrder(count + "");
                        methodMap.put(orderInvokeMethod.getOrder(), orderInvokeMethod);
                        orderStatusMap.put(orderInvokeMethod.getOrder(), new HashSet<String>());
                        for (String statusT : status) {
                            orderStatusMap.get(orderInvokeMethod.getOrder()).add(statusT);
                        }
                        count++;
                    }
                }
            }
        }
    }

}
