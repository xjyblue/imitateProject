package login.thread;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import core.ServiceDistributor;
import core.config.GrobalConfig;
import login.entity.LoginThreadTask;
import login.entity.LoginUserTask;
import mapper.UserMapper;
import mapper.UserskillrelationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.buffservice.service.UserBuffService;
import service.loginservice.service.LoginService;
import service.userservice.service.UserService;

import javax.annotation.PostConstruct;
import javax.naming.ldap.PagedResultsControl;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @ClassName LoginThreadPool
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/16 10:28
 * @Version 1.0
 **/
@Component
public class LoginThreadPool {
    /**
     * 初始化线程池工厂名
     */
    public final static ThreadFactory NAME_THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("thread-call-login-runner-%d").build();
    /**
     * 初始化登陆线程池
     **/
    public final static ExecutorService LOGIN_THREAD_POOL = new ThreadPoolExecutor(GrobalConfig.LOGIN_THEAD_NUM, GrobalConfig.LOGIN_THEAD_NUM, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), NAME_THREAD_FACTORY);
    /**
     * 登陆线程map查找
     */
    public final static Map<Integer, LoginThreadTask> LOGIN_THREAD_TASK_MAP = Maps.newHashMap();

    /**
     * 注入loginThreadTask用到的单例
     */
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserskillrelationMapper userskillrelationMapper;
    @Autowired
    private ServiceDistributor serviceDistributor;
    @Autowired
    private UserService userService;
    @Autowired
    private UserBuffService userBuffService;

    /**
     * 初始化登陆线程
     */
    @PostConstruct
    public void initLoginThreadTask() {
        for (int i = 0; i < GrobalConfig.LOGIN_THEAD_NUM; i++) {
            LoginThreadTask loginThreadTask = new LoginThreadTask(i, userMapper, userskillrelationMapper, serviceDistributor, userService, userBuffService);
            LOGIN_THREAD_POOL.execute(loginThreadTask);
            LOGIN_THREAD_TASK_MAP.put(loginThreadTask.getKeyNum(), loginThreadTask);
        }
    }
}
