package core.annotation.good;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @ClassName GoodRegion
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/2/16 16:48
 * @Version 1.0
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})//定义注解的作用目标**作用范围字段、枚举的常量/方法
@Documented//说明该注解将被包含在javadoc中
@Component
public @interface GoodRegion {
}
