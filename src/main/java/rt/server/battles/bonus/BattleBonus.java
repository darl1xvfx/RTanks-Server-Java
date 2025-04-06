package rt.server.battles.bonus;

import rt.server.battles.bonus.regions.BonusRegionData;
import rt.server.math.Vector3;

public class BattleBonus {
	public String id;
    public Vector3 pos;
	public BonusRegionData bonusRegionData;
    
    public BattleBonus(String id2, Vector3 pos2) {
		this.id = id2;
		this.pos = pos2;
	}

	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public String getName() {
		return this.id;
	}
}
