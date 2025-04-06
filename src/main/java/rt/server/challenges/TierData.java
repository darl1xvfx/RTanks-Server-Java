package rt.server.challenges;

import rt.server.challenges.items.TierItem;

public class TierData {
    public TierItem freeItem;
    public TierItem battlePassItem;
    public int stars;
    
    public TierData(TierItem fi, TierItem bpi, int st) {
    	this.freeItem = fi;
    	this.battlePassItem = bpi;
    	this.stars = st;
    }
}
