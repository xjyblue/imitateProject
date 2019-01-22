package service.auctionservice.entity;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @ClassName AuctionCache
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/22 16:50
 * @Version 1.0
 **/
public class AuctionCache {
    /**
     * 拍卖物品
     */
    public final static Map<String, AuctionItem> auctionItemMap = Maps.newConcurrentMap();
}
