package Role;

/**
 * Description ：nettySpringServer
 * Created by xiaojianyu on 2018/11/30 15:02
 */
//职业类
public class Role {

//  职业id
    private Integer RoleId;
//  职业名称
    private String name;

    public Role(Integer roleId, String name) {
        RoleId = roleId;
        this.name = name;
    }

    public Integer getRoleId() {
        return RoleId;
    }

    public void setRoleId(Integer roleId) {
        RoleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
