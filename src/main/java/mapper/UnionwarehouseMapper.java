package mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import pojo.Unionwarehouse;
import pojo.UnionwarehouseExample;

public interface UnionwarehouseMapper {
    int countByExample(UnionwarehouseExample example);

    int deleteByExample(UnionwarehouseExample example);

    int insert(Unionwarehouse record);

    int insertSelective(Unionwarehouse record);

    List<Unionwarehouse> selectByExample(UnionwarehouseExample example);

    int updateByExampleSelective(@Param("record") Unionwarehouse record, @Param("example") UnionwarehouseExample example);

    int updateByExample(@Param("record") Unionwarehouse record, @Param("example") UnionwarehouseExample example);
}