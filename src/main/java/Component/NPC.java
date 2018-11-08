package Component;

import java.util.List;

public class NPC {
	private String status;

	private String name;

	private List<String> talks ;

	public List<String> getTalk() {
		return talks;
	}

	public void setTalk(List<String> talks) {
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
