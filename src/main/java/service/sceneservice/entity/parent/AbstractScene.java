package service.sceneservice.entity.parent;

/**
 * @ClassName AbstractScene
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public abstract class AbstractScene {
    /**
     * 父类构造
     */
    public AbstractScene() {
//      子类自己的init方法
        preConstruct();
    }

    /**
     * 子类实现，可以在做先于构造函数的事情
     */
    public abstract void preConstruct();
}
