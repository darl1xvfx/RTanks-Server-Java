package rt.server.battles.effects;

public class InventoryItem {
    public int itemEffectTime;
    public int count;
    public int slotId;
    public String id;
    public int itemRestSec;
    
    public InventoryItem(int itemEffectTime, int count, int slotId, String id, int itemRestSec) {
    	this.itemEffectTime = itemEffectTime;
    	this.count = count;
    	this.slotId = slotId;
    	this.id = id;
    	this.itemRestSec = itemRestSec;
    }
}
