package component;

import component.parent.PGood;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/12/25 15:25
 */
public class CollectGood extends PGood {
    private Integer id;

    public CollectGood() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
