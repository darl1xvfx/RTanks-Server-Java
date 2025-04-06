package rt.server.battles.maps.parser.parser.map.keypoints;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import rt.server.battles.maps.parser.parser.Vector3d;

public class CPKeypoint {

	private String pointId;
	private String free;
	private Vector3d position;
	
	
	
	public CPKeypoint() {
	}
	
	@XmlAttribute(name = "name")
	public String getPointId() {
		return pointId;
	}
	public void setPointId(String pointId) {
		this.pointId = pointId;
	}
	@XmlElement(name = "position")
	public Vector3d getPosition() {
		return position;
	}
	public void setPosition(Vector3d position) {
		this.position = position;
	}

	@XmlAttribute(name = "free")
	public String getFree() {
		return free;
	}

	public void setFree(String free) {
		this.free = free;
	}
	
	@Override
	public String toString() {
		return "Point id: "+pointId+" position: "+position;
	}
	
}
