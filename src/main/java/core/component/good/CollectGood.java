package core.component.good;

import core.component.good.parent.BaseGood;

/**
 * @ClassName CollectGood
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class CollectGood extends BaseGood {
    /**
     * id
     */
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
