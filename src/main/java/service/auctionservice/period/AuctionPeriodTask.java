package service.auctionservice.period;

import core.base.parent.BaseThread;
import core.context.ProjectContext;
import pojo.User;
import pojo.Userbag;
import service.auctionservice.entity.AuctionItem;
import service.caculationservice.service.MoneyCaculationService;
import service.caculationservice.service.UserbagCaculationService;
import service.userbagservice.service.UserbagService;
import service.userservice.service.UserService;
import utils.SpringContextUtil;

import javax.swing.text.html.HTMLDocument;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName AuctionPeriodTask
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/9 15:29
 * @Version 1.0
 **/
public class AuctionPeriodTask extends BaseThread implements Runnable {

    private UserbagService userbagService;

    private UserbagCaculationService userbagCaculationService;

    private UserService userService;

    private MoneyCaculationService moneyCaculationService;

    @Override
    public void preConstruct() {
        this.userbagService = SpringContextUtil.getBean("userbagService");
        this.userbagCaculationService = userbagCaculationService = SpringContextUtil.getBean("userbagCaculationService");
        this.userService = SpringContextUtil.getBean("userService");
        this.moneyCaculationService = SpringContextUtil.getBean("moneyCaculationService");
    }

    @Override
    public void run() {
        while (true) {
            try {
                refreshAuctionItems();
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void refreshAuctionItems() {
        Iterator<Map.Entry<String, AuctionItem>> iterator = ProjectContext.auctionItemMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, AuctionItem> entry = iterator.next();
            AuctionItem auctionItem = entry.getValue();
            try {
                auctionItem.getAuctionItemLock().lock();
                if (auctionItem.isEnd()) {
                    return;
                }
//              过期处理
                if (System.currentTimeMillis() > auctionItem.getEndTime()) {
                    Userbag userbag = auctionItem.getUserbag();
                    User saleUser = userService.getUserByNameFromSession(auctionItem.getFromUsername());
                    if (auctionItem.getBuyUsername() == null) {
//                      无人参与竞拍
                        userbagCaculationService.addUserBagForUser(saleUser, userbag);
                    } else {
                        User buyUser = userService.getUserByNameFromSession(auctionItem.getBuyUsername());
//                      某人参与了竞拍
//                      卖家加钱
                        moneyCaculationService.addMoneyToUser(saleUser, auctionItem.getBuyMoney().toString());
//                      买家加物品
                        userbagCaculationService.addUserBagForUser(buyUser, userbag);
                    }
                    auctionItem.setEnd(true);
//              移除记录
                    iterator.remove();
                }
            } finally {
                auctionItem.getAuctionItemLock().unlock();
            }
        }
    }
}
