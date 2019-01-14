package core.context;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * @ClassName ThreadContext
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/14 15:32
 * @Version 1.0
 **/
public class ThreadContext {
    /**
     * 初始化普通线程池工厂
     */
    public final static ThreadFactory NAME_THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("thread-call-scene-runner-%d").build();
    /**
     * 初始化 地图线程池
     **/
    public final static ExecutorService SCENE_THREAD_POOL = new ThreadPoolExecutor(4, 4, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), NAME_THREAD_FACTORY);
    /**
     * 副本boss攻击线程池
     */
    public final static ScheduledExecutorService BOSS_AREA_THREAD_POOL = new ScheduledThreadPoolExecutor(5, NAME_THREAD_FACTORY);
    /**
     * 公共周期线程池，清除过期任务，只用于公共场合
     */
    public final static ExecutorService PERIOD_THREAD_POOL = new ThreadPoolExecutor(4, 4, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), NAME_THREAD_FACTORY);
}
