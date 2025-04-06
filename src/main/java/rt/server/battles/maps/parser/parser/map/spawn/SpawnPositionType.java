package rt.server.battles.maps.parser.parser.map.spawn;

public class SpawnPositionType
{
    public static final SpawnPositionType BLUE;
    public static final SpawnPositionType RED;
    public static final SpawnPositionType NONE;
    
    static {
        BLUE = new SpawnPositionType();
        RED = new SpawnPositionType();
        NONE = new SpawnPositionType();
    }
    
    public static SpawnPositionType getType(final String value) {
        if (value.equals("blue")) {
            return SpawnPositionType.BLUE;
        }
        if (value.equals("red")) {
            return SpawnPositionType.RED;
        }
        if (value.equals("dm")) {
            return SpawnPositionType.NONE;
        }
        return SpawnPositionType.NONE;
    }
}
