package core.reflect.base;

import java.lang.reflect.Method;

/**
 * @ClassName BaseInvokeMethod
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/2/16 16:23
 * @Version 1.0
 **/
public class BaseInvokeMethod {

    private Method method;

    private Object object;

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
}
