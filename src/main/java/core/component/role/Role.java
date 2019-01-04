package core.component.role;

/**
 * @ClassName Role
 * @Description TODO
 * @Author xiaojianyu
 * @Date 2019/1/4 11:11
 * @Version 1.0
 **/
public class Role {
    /**
     * 职业id
     */
    private Integer roleId;
    /**
     * 职业名称
     */
    private String name;
    /**
     * 职业拥有的技能 技能按照 XX-XX的格式隔开
     */
    private String skills;
    /**
     * 职业所拥有的防御力
     */
    private String defense;

    public Role() {
    }

    public Role(Integer roleId, String name) {
        this.roleId = roleId;
        this.name = name;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getDefense() {
        return defense;
    }

    public void setDefense(String defense) {
        this.defense = defense;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
