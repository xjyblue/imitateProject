package mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import pojo.Achievementprocess;
import pojo.AchievementprocessExample;
/**
 * Created on 2018/12/25 15:25
 *
 * @Author: xiaojianyu
 */
public interface AchievementprocessMapper {
    /**
     *..
     * @param example
     * @return
     */
    int countByExample(AchievementprocessExample example);

    int deleteByExample(AchievementprocessExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Achievementprocess record);

    int insertSelective(Achievementprocess record);

    List<Achievementprocess> selectByExample(AchievementprocessExample example);

    Achievementprocess selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Achievementprocess record, @Param("example") AchievementprocessExample example);

    int updateByExample(@Param("record") Achievementprocess record, @Param("example") AchievementprocessExample example);

    int updateByPrimaryKeySelective(Achievementprocess record);

    int updateByPrimaryKey(Achievementprocess record);

    Achievementprocess selectprocessByUsernameAndAchievementId(@Param("fromUser") String fromUser,@Param("achievementId") Integer achievementId);
}