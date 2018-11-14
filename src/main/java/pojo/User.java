package pojo;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class User {
    private String username;

    private String password;

    private String status;

    private String pos;

    private String mp;

    private String hp;

    private List<Userbag> userBag;

    private List<Weaponequipmentbar> weaponequipmentbars;

    private Map<String,Integer> buffMap;

    public Map<String, Integer> getBufferMap() {
        return buffMap;
    }

    public void setBufferMap(Map<String, Integer> bufferMap) {
        this.buffMap = bufferMap;
    }

    public List<Weaponequipmentbar> getWeaponequipmentbars() {
        return weaponequipmentbars;
    }

    public void setWeaponequipmentbars(List<Weaponequipmentbar> weaponequipmentbars) {
        this.weaponequipmentbars = weaponequipmentbars;
    }

    public List<Userbag> getUserBag() {
        return userBag;
    }

    public void setUserBag(List<Userbag> userBag) {
        this.userBag = userBag;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getPos() {
        return pos;
    }

    public  void setPos(String pos) {
        this.pos = pos == null ? null : pos.trim();
    }

    public synchronized String getMp() {
        return mp;
    }

    public  synchronized  void setMp(String mp) {
        this.mp = mp == null ? null : mp.trim();
    }

    public String getHp() {
        return hp;
    }

    public void setHp(String hp) {
        this.hp = hp == null ? null : hp.trim();
    }

    public synchronized void addMp(String changeNum){
        BigInteger userMp = new BigInteger(this.getMp());
        BigInteger addMp = new BigInteger(changeNum);
        userMp = userMp.add(addMp);
        this.setMp(userMp.toString());
    }

    public synchronized void subMp(String changeNum){
        BigInteger userMp = new BigInteger(this.getMp());
        BigInteger subMp = new BigInteger(changeNum);
        userMp = userMp.subtract(subMp);
        this.setMp(userMp.toString());
    }

}