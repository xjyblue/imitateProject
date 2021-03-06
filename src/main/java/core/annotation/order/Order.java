package core.annotation.order;

import java.lang.annotation.*;
import java.util.List;
import java.util.Set;

/**
 * @ClassName Order
 * @Description 注解式命令
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/

@Retention(RetentionPolicy.RUNTIME) // 注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Target({ElementType.METHOD})//定义注解的作用目标**作用范围字段、枚举的常量/方法
@Documented//说明该注解将被包含在javadoc中
public @interface Order {

    String orderMsg() default "";

    String desc() default "";

    boolean ifRandomkey() default false;

    String[] status();
}
