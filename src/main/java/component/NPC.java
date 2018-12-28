package component;

public class NPC {

	private Integer id;

	private String status;

	private String name;

	private String talk;

	private Integer areaId;

	private String getTarget;

	private String getGoods;

	public NPC(){

	}

	public String getGetTarget() {
		return getTarget;
	}

	public void setGetTarget(String getTarget) {
		this.getTarget = getTarget;
	}

	public String getGetGoods() {
		return getGoods;
	}

	public void setGetGoods(String getGoods) {
		this.getGoods = getGoods;
	}

	public Integer getAreaId() {
		return areaId;
	}

	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}

	public String getTalk() {
		return talk;
	}

	public void setTalk(String talk) {
		this.talk = talk;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public NPC(String status, String name) {
		super();
		this.status = status;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
}
