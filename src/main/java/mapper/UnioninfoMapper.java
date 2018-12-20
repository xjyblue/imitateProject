package mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import pojo.Unioninfo;
import pojo.UnioninfoExample;

public interface UnioninfoMapper {
    int countByExample(UnioninfoExample example);

    int deleteByExample(UnioninfoExample example);

    int deleteByPrimaryKey(String unionid);

    int insert(Unioninfo record);

    int insertSelective(Unioninfo record);

    List<Unioninfo> selectByExample(UnioninfoExample example);

    Unioninfo selectByPrimaryKey(String unionid);

    int updateByExampleSelective(@Param("record") Unioninfo record, @Param("example") UnioninfoExample example);

    int updateByExample(@Param("record") Unioninfo record, @Param("example") UnioninfoExample example);

    int updateByPrimaryKeySelective(Unioninfo record);

    int updateByPrimaryKey(Unioninfo record);
    
    
}