package mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import pojo.Userbag;
import pojo.UserbagExample;

public interface UserbagMapper {
    int countByExample(UserbagExample example);

    int deleteByExample(UserbagExample example);

    int deleteByPrimaryKey(String id);

    int insert(Userbag record);

    int insertSelective(Userbag record);

    List<Userbag> selectByExample(UserbagExample example);

    Userbag selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") Userbag record, @Param("example") UserbagExample example);

    int updateByExample(@Param("record") Userbag record, @Param("example") UserbagExample example);

    int updateByPrimaryKeySelective(Userbag record);

    int updateByPrimaryKey(Userbag record);

    List<Userbag> selectUserbagByWarehourseId(@Param("unionwarehourseid") String unionwarehourseid);
}