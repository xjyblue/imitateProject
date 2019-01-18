package config.impl.thread;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import config.interf.IResourceLoad;
import org.springframework.stereotype.Component;
import service.auctionservice.period.AuctionPeriodTask;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @ClassName ThreadPeriodTaskLoad
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/15 17:11
 * @Version 1.0
 **/
@Component
public class ThreadPeriodTaskLoad implements IResourceLoad {

    /**
     * 辅助定时任务关闭的线程的futuremap
     */
    public final static Map<String, Future> futureMap = Maps.newConcurrentMap();
    /**
     * 初始化普通线程池工厂
     */
    public final static ThreadFactory NAME_THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("thread-call-scene-runner-%d").build();
    /**
     * 公共周期线程池，清除过期任务，只用于公共场合
     */
    public final static ExecutorService PERIOD_THREAD_POOL = new ThreadPoolExecutor(4, 4, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), NAME_THREAD_FACTORY);
    /**
     * 副本boss线程池
     */
    public final static ScheduledExecutorService BOSS_AREA_THREAD_POOL = new ScheduledThreadPoolExecutor(5, NAME_THREAD_FACTORY);
    /**
     * 初始化地图线程池
     **/
    public final static ExecutorService SCENE_THREAD_POOL = new ThreadPoolExecutor(4, 4, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), NAME_THREAD_FACTORY);

    @PostConstruct
    @Override
    public void load() {
        PERIOD_THREAD_POOL.execute(new AuctionPeriodTask());
    }
}
