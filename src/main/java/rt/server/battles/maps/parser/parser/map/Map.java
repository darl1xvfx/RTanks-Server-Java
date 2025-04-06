package rt.server.battles.maps.parser.parser.map;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import rt.server.battles.maps.parser.parser.Vector3d;
import rt.server.battles.maps.parser.parser.map.bonus.BonusRegion;
import rt.server.battles.maps.parser.parser.map.keypoints.CPKeypoint;
import rt.server.battles.maps.parser.parser.map.spawn.SpawnPosition;

@XmlRootElement(name = "map")
public class Map
{
    private SpawnPoints spawnPoints;
    private BonusRegions bonusRegions;
    private FlagsPositions flagPositions;
    private DOMKeypoints points;
    
    public SpawnPoints getSpawnPoints() {
        return this.spawnPoints;
    }
    
    @XmlElement(name = "spawn-points")
    public void setSpawnPoints(final SpawnPoints spawnPoints) {
        this.spawnPoints = spawnPoints;
    }
    
    public BonusRegions getBonusRegions() {
        return this.bonusRegions;
    }
    
    @XmlElement(name = "bonus-regions")
    public void setBonusRegions(final BonusRegions bonusRegions) {
        this.bonusRegions = bonusRegions;
    }
    
    public FlagsPositions getFlagPositions() {
        return this.flagPositions;
    }
    
    @XmlElement(name = "ctf-flags")
    public void setFlagPositions(final FlagsPositions flagPositions) {
        this.flagPositions = flagPositions;
    }
    
    public Vector3d getPositionBlueFlag() {
        return (this.getFlagPositions() != null) ? this.getFlagPositions().getBlueFlag() : null;
    }
    
    public Vector3d getPositionRedFlag() {
        return (this.getFlagPositions() != null) ? this.getFlagPositions().getRedFlag() : null;
    }
    
    public List<SpawnPosition> getSpawnPositions() {
        return this.spawnPoints.getSpawnPositions();
    }
    
    public List<BonusRegion> getBonusesRegion() {
        return this.bonusRegions.getBonusRegions();
    }
    @XmlElement(name = "dom-keypoints")
	public DOMKeypoints getPoints() {
		return points;
	}
    public void setPoints(DOMKeypoints points) {
		this.points = points;
	}
    public List<CPKeypoint> getDOMKeypoints(){
		return getPoints().getPoints();
	}
}

@XmlRootElement(name = "dom-keypoints")
class DOMKeypoints{
	private List<CPKeypoint> points = new ArrayList<>();
	
	@XmlElement(name = "dom-keypoint")
	public List<CPKeypoint> getPoints() {

		return points;
	}
	
	public void setBonusRegions(List<CPKeypoint> points) {
		
		this.points = points;
	}
}