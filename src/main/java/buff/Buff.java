package buff;

public class Buff {

    private Integer bufferId;

    private String type;

    private String injurySecondValue;

    private String addSecondValue;

    private String recoverValue;

    private Integer keepTime;

    private String name;

    private String typeOf;

    public Buff(){

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
