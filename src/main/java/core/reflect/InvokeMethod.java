package core.reflect;

import java.lang.reflect.Method;

/**
 * @ClassName InvokeMethod
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/11 11:58
 * @Version 1.0
 **/
public class InvokeMethod {

    private Method method;

    private Object object;

    private String order;

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
