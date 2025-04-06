package rt.server.battles.spawn;

import java.util.*;
import rt.server.battles.maps.parser.Map;
import rt.server.math.*;

public class SpawnManager
{
    private static Random rand;
    
    static {
        SpawnManager.rand = new Random();
    }
    
    public static Vector3 getSpawnState(final Map map, final String forTeam) {
        Vector3 pos = null;
        try {
            if (forTeam.equals("BLUE")) {
                pos = map.spawnPositonsBlue.get(SpawnManager.rand.nextInt(map.spawnPositonsBlue.size()));
            }
            else if (forTeam.equals("RED")) {
                pos = map.spawnPositonsRed.get(SpawnManager.rand.nextInt(map.spawnPositonsRed.size()));
            }
            else {
                pos = map.spawnPositonsDM.get(SpawnManager.rand.nextInt(map.spawnPositonsDM.size()));
            }
            if (pos == null) {
                pos = map.spawnPositonsDM.get(SpawnManager.rand.nextInt(map.spawnPositonsDM.size()));
            }
        }
        catch (Exception ex) {
            pos = map.spawnPositonsDM.get(SpawnManager.rand.nextInt(map.spawnPositonsDM.size()));
        }
        return pos;
    }
}
