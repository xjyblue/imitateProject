package component.scene.parent;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/12/29 12:04
 */
public abstract class PScene {

    public PScene() {
//      子类自己的init方法
        preConstruct();
    }

    public abstract void preConstruct();
}
