package mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import pojo.Applyunioninfo;
import pojo.ApplyunioninfoExample;

public interface ApplyunioninfoMapper {
    int countByExample(ApplyunioninfoExample example);

    int deleteByExample(ApplyunioninfoExample example);

    int deleteByPrimaryKey(String applyid);

    int insert(Applyunioninfo record);

    int insertSelective(Applyunioninfo record);

    List<Applyunioninfo> selectByExample(ApplyunioninfoExample example);

    Applyunioninfo selectByPrimaryKey(String applyid);

    int updateByExampleSelective(@Param("record") Applyunioninfo record, @Param("example") ApplyunioninfoExample example);

    int updateByExample(@Param("record") Applyunioninfo record, @Param("example") ApplyunioninfoExample example);

    int updateByPrimaryKeySelective(Applyunioninfo record);

    int updateByPrimaryKey(Applyunioninfo record);

    int selectByUserIdAndUnionId(@Param("username") String username, @Param("unionId") String unionId);

    List<Applyunioninfo> selectByApplyinfoByUnionId(@Param("unionid") String unionid);
}