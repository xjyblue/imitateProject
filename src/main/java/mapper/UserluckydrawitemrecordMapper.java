package mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import pojo.Userluckydrawitemrecord;
import pojo.UserluckydrawitemrecordExample;

public interface UserluckydrawitemrecordMapper {
    int countByExample(UserluckydrawitemrecordExample example);

    int deleteByExample(UserluckydrawitemrecordExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Userluckydrawitemrecord record);

    int insertSelective(Userluckydrawitemrecord record);

    List<Userluckydrawitemrecord> selectByExample(UserluckydrawitemrecordExample example);

    Userluckydrawitemrecord selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Userluckydrawitemrecord record, @Param("example") UserluckydrawitemrecordExample example);

    int updateByExample(@Param("record") Userluckydrawitemrecord record, @Param("example") UserluckydrawitemrecordExample example);

    int updateByPrimaryKeySelective(Userluckydrawitemrecord record);

    int updateByPrimaryKey(Userluckydrawitemrecord record);
}