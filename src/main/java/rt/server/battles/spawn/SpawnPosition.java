package rt.server.battles.spawn;

import rt.server.math.*;

public class SpawnPosition
{
    public Vector3 position;
    public Vector3 orintation;
    
    public SpawnPosition(final Vector3 position, final Vector3 orintation) {
        this.position = position;
        this.orintation = orintation;
    }
}
