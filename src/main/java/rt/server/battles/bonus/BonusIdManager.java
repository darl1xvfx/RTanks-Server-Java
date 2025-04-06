package rt.server.battles.bonus;

public class BonusIdManager {
    public static int id = -1;
    
    public static int getUniqueId() {
    	return id++;
    }
}
