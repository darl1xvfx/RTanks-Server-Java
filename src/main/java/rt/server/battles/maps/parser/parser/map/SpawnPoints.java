package rt.server.battles.maps.parser.parser.map;

import java.util.*;
import rt.server.battles.maps.parser.parser.map.spawn.*;
import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "spawn-points")
class SpawnPoints
{
    private List<SpawnPosition> spawnPositions;
    
    public List<SpawnPosition> getSpawnPositions() {
        return this.spawnPositions;
    }
    
    @XmlElement(name = "spawn-point")
    public void setSpawnPositions(final List<SpawnPosition> spawnPositions) {
        this.spawnPositions = spawnPositions;
    }
}
