package config.impl.db;

import com.google.common.collect.Maps;
import config.interf.IResourceLoad;
import mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pojo.User;
import pojo.UserExample;
import service.emailservice.entity.Mail;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName EmailDbLoad
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/15 16:55
 * @Version 1.0
 **/
@Component
public class EmailDbLoad implements IResourceLoad {
    /**
     * 缓存邮件信息
     */
    public final static Map<String, ConcurrentHashMap<String, Mail>> userEmailMap = Maps.newHashMap();

    @Autowired
    private UserMapper userMapper;

    @PostConstruct
    @Override
    public void load() {
        //      从数据库初始化所有用户的邮件系统
        UserExample userExample = new UserExample();
        List<User> list = userMapper.selectByExample(userExample);
        for (User user : list) {
            userEmailMap.put(user.getUsername(), new ConcurrentHashMap<String, Mail>(64));
        }
    }
}
