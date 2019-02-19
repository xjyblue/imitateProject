package mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import pojo.Userluckydrawrecord;
import pojo.UserluckydrawrecordExample;

public interface UserluckydrawrecordMapper {
    int countByExample(UserluckydrawrecordExample example);

    int deleteByExample(UserluckydrawrecordExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Userluckydrawrecord record);

    int insertSelective(Userluckydrawrecord record);

    List<Userluckydrawrecord> selectByExample(UserluckydrawrecordExample example);

    Userluckydrawrecord selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Userluckydrawrecord record, @Param("example") UserluckydrawrecordExample example);

    int updateByExample(@Param("record") Userluckydrawrecord record, @Param("example") UserluckydrawrecordExample example);

    int updateByPrimaryKeySelective(Userluckydrawrecord record);

    int updateByPrimaryKey(Userluckydrawrecord record);

    Userluckydrawrecord selectByUserNameAndLuckyDrawRecordId(@Param("username") String username, @Param("luckyDrawId") Integer luckyDrawId);
}