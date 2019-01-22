package service.teamservice.entity;

import com.google.common.collect.Maps;
import service.auctionservice.entity.AuctionItem;

import java.util.Map;

/**
 * @ClassName TeamCache
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/22 16:49
 * @Version 1.0
 **/
public class TeamCache {
    /**
     * 记录玩家队伍
     */
    public final static Map<String, Team> teamMap = Maps.newConcurrentMap();
}
