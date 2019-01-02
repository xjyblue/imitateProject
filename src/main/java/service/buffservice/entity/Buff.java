package service.buffservice.entity;

public class Buff {
//  buffId
    private Integer bufferId;
//  buff抵挡伤害量伤害量
    private String injurySecondValue;
//  buff增加伤害量
    private String addSecondValue;
//  buff回复的血量
    private String recoverValue;
//  buff的名称
    private Integer keepTime;
//  buff的名字
    private String name;
//  buff的种类
    private String typeOf;
//  buff的截止时间
    private Long endTime;

    public Buff(){
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getRecoverValue() {
        return recoverValue;
    }

    public void setRecoverValue(String recoverValue) {
        this.recoverValue = recoverValue;
    }

    public String getTypeOf() {
        return typeOf;
    }

    public void setTypeOf(String typeOf) {
        this.typeOf = typeOf;
    }

    public Integer getBufferId() {
        return bufferId;
    }

    public void setBufferId(Integer bufferId) {
        this.bufferId = bufferId;
    }

    public String getInjurySecondValue() {
        return injurySecondValue;
    }

    public void setInjurySecondValue(String injurySecondValue) {
        this.injurySecondValue = injurySecondValue;
    }

    public String getAddSecondValue() {
        return addSecondValue;
    }

    public void setAddSecondValue(String addSecondValue) {
        this.addSecondValue = addSecondValue;
    }

    public Integer getKeepTime() {
        return keepTime;
    }

    public void setKeepTime(Integer keepTime) {
        this.keepTime = keepTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
