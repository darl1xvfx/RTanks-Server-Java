package rt.server.battles.bonus;

import rt.server.math.*;

public class BonusRegion
{
    public Vector3 max;
    public Vector3 min;
    public String[] types;
    
    public BonusRegion(final Vector3 max, final Vector3 min, final String[] types) {
        this.max = max;
        this.min = min;
        this.types = types;
    }
}
