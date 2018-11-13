package mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import pojo.Weaponequipmentbar;
import pojo.WeaponequipmentbarExample;

public interface WeaponequipmentbarMapper {
    int countByExample(WeaponequipmentbarExample example);

    int deleteByExample(WeaponequipmentbarExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Weaponequipmentbar record);

    int insertSelective(Weaponequipmentbar record);

    List<Weaponequipmentbar> selectByExample(WeaponequipmentbarExample example);

    Weaponequipmentbar selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") Weaponequipmentbar record, @Param("example") WeaponequipmentbarExample example);

    int updateByExample(@Param("record") Weaponequipmentbar record, @Param("example") WeaponequipmentbarExample example);

    int updateByPrimaryKeySelective(Weaponequipmentbar record);

    int updateByPrimaryKey(Weaponequipmentbar record);
}