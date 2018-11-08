package mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import pojo.Userskillrelation;
import pojo.UserskillrelationExample;

public interface UserskillrelationMapper {
    int countByExample(UserskillrelationExample example);

    int deleteByExample(UserskillrelationExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Userskillrelation record);

    int insertSelective(Userskillrelation record);

    List<Userskillrelation> selectByExample(UserskillrelationExample example);

    Userskillrelation selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Userskillrelation record, @Param("example") UserskillrelationExample example);

    int updateByExample(@Param("record") Userskillrelation record, @Param("example") UserskillrelationExample example);

    int updateByPrimaryKeySelective(Userskillrelation record);

    int updateByPrimaryKey(Userskillrelation record);
}