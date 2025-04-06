package rt.server.battles.bonus.regions;

import org.json.simple.JSONObject;
import rt.server.math.Vector3;

public class BonusRegionData extends JSONObject {
	public Vector3 position = new Vector3(0.0f, 0.0f, 0.0f);
	public Vector3 o = new Vector3(0.0f, 0.0f, 0.0f);
	public String type = "";
			   
	public BonusRegionData(Vector3 pos, Vector3 o, String type) {
		this.position = pos;
		this.o = o;
		this.type = type;
		this.put("x", this.position.x);
		this.put("y", this.position.y);
		this.put("z", this.position.z);
		this.put("ox", this.o.x);
		this.put("oy", this.o.y);
		this.put("oz", this.o.z);
		this.put("type", this.type);
    }
}
