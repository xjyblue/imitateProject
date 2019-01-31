package mapper;

import org.apache.ibatis.annotations.Param;
import service.teamservice.entity.Team;

/**
 * @ClassName TeamMapper
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/30 10:17
 * @Version 1.0
 **/
public interface TeamMapper {

    int insert(@Param("team") Team team);

}
