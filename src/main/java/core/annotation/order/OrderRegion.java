package core.annotation.order;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @ClassName OrderRegion
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/10 20:36
 * @Version 1.0
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})//定义注解的作用目标**作用范围字段、枚举的常量/方法
@Documented//说明该注解将被包含在javadoc中
@Component
public @interface OrderRegion {

}
