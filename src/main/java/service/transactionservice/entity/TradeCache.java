package service.transactionservice.entity;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @ClassName TradeCache
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/22 16:48
 * @Version 1.0
 **/
public class TradeCache {
    /**
     * 交易单的建立
     */
    public final static Map<String, Trade> tradeMap = Maps.newConcurrentMap();
}
