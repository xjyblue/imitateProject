package mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import pojo.Teamapplyinfo;
import pojo.TeamapplyinfoExample;

public interface TeamapplyinfoMapper {
    int countByExample(TeamapplyinfoExample example);

    int deleteByExample(TeamapplyinfoExample example);

    int deleteByPrimaryKey(String id);

    int insert(Teamapplyinfo record);

    int insertSelective(Teamapplyinfo record);

    List<Teamapplyinfo> selectByExample(TeamapplyinfoExample example);

    Teamapplyinfo selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") Teamapplyinfo record, @Param("example") TeamapplyinfoExample example);

    int updateByExample(@Param("record") Teamapplyinfo record, @Param("example") TeamapplyinfoExample example);

    int updateByPrimaryKeySelective(Teamapplyinfo record);

    int updateByPrimaryKey(Teamapplyinfo record);
}