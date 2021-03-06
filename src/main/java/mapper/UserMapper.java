package mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.scheduling.annotation.Async;
import pojo.User;
import pojo.UserExample;

public interface UserMapper {
    int countByExample(UserExample example);

    int deleteByExample(UserExample example);

    int deleteByPrimaryKey(String username);

    int insert(User record);
    
    void insertSelective(User record);

    List<User> selectByExample(UserExample example);

    User selectByPrimaryKey(String username);

    int updateByExampleSelective(@Param("record") User record, @Param("example") UserExample example);

    int updateByExample(@Param("record") User record, @Param("example") UserExample example);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User getUser(@Param("username") String username, @Param("password") String password);

    List<User> selectByUnionId(@Param("unionid") String unionid);
}