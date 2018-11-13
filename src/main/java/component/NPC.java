package component;

import java.util.List;

public class NPC {
	private Integer id;

	private String status;

	private String name;

	private List<String> talks ;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<String> getTalks() {
		return talks;
	}

	public void setTalks(List<String> talks) {
		this.talks = talks;
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
