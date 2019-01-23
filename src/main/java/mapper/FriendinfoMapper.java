package mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import pojo.Friendinfo;
import pojo.FriendinfoExample;

public interface FriendinfoMapper {
    int countByExample(FriendinfoExample example);

    int deleteByExample(FriendinfoExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Friendinfo record);

    int insertSelective(Friendinfo record);

    List<Friendinfo> selectByExample(FriendinfoExample example);

    Friendinfo selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Friendinfo record, @Param("example") FriendinfoExample example);

    int updateByExample(@Param("record") Friendinfo record, @Param("example") FriendinfoExample example);

    int updateByPrimaryKeySelective(Friendinfo record);

    int updateByPrimaryKey(Friendinfo record);

    void deleteByUserName(@Param("userfrom") String userfrom,@Param("userto") String userto);
}