package utils;

import core.annotation.Region;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.xml.ws.Service;
import java.util.Map;

/**
 * @ClassName ff
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/10 20:38
 * @Version 1.0
 **/
@Component
public class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 根容器为Spring容器
        if (event.getApplicationContext().getParent() == null) {
            Map<String, Object> beans = event.getApplicationContext().getBeansWithAnnotation(Region.class);
            for (Object bean : beans.values()) {
                System.err.println(bean == null ? "null" : bean.getClass().getName());
            }
            System.err.println("=====ContextRefreshedEvent=====" + event.getSource().getClass().getName());
        }
    }
}
