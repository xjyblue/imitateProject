package mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import pojo.Friendapplyinfo;
import pojo.FriendapplyinfoExample;

public interface FriendapplyinfoMapper {
    int countByExample(FriendapplyinfoExample example);

    int deleteByExample(FriendapplyinfoExample example);

    int deleteByPrimaryKey(String id);

    int insert(Friendapplyinfo record);

    int insertSelective(Friendapplyinfo record);

    List<Friendapplyinfo> selectByExample(FriendapplyinfoExample example);

    Friendapplyinfo selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") Friendapplyinfo record, @Param("example") FriendapplyinfoExample example);

    int updateByExample(@Param("record") Friendapplyinfo record, @Param("example") FriendapplyinfoExample example);

    int updateByPrimaryKeySelective(Friendapplyinfo record);

    int updateByPrimaryKey(Friendapplyinfo record);
}