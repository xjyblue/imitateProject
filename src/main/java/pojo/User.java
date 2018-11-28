package pojo;

import buff.Buff;
import config.BuffConfig;
import memory.NettyMemory;
import sun.misc.Unsafe;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class User {
    private String username;

    private String password;

    private String status;

    private String pos;

    private volatile String mp;

    private volatile String hp;

    private String teamId;

    private List<Userbag> userBag;

    private List<Weaponequipmentbar> weaponequipmentbars;

    private Map<String,Integer> buffMap;

    private String money;

//  保证原子性
    public synchronized void addMoney(BigInteger add){
        BigInteger userMoney = new BigInteger(this.getMoney());
        userMoney = userMoney.add(add);
        this.money = userMoney.toString();
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

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

    public String getMp() {
        return mp;
    }

    public synchronized void setMp(String mp) {
        this.mp = mp == null ? null : mp.trim();
    }

    public synchronized String getHp() {
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

    public synchronized void subHp(String changeNum) {
        BigInteger userHp = new BigInteger(this.getHp());
        BigInteger subHp = new BigInteger(changeNum);
        userHp = userHp.subtract(subHp);
        this.setHp(userHp.toString());
    }

    public void addHp(String changeNum) {
        BigInteger userHp = new BigInteger(this.getHp());
        BigInteger subHp = new BigInteger(changeNum);
        userHp = userHp.add(subHp);
        this.setHp(userHp.toString());
    }
}