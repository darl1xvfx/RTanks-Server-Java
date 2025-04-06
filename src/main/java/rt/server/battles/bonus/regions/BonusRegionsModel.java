package rt.server.battles.bonus.regions;

import java.util.ArrayList;
import java.util.List;

import rt.server.battles.bonus.BattleBonus;
import rt.server.battles.bonus.BonusRegion;
import rt.server.battles.BattleModel;
import rt.server.battles.maps.parser.Map;
import rt.server.math.Vector3;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;
import rt.server.utils.JSON;

public class BonusRegionsModel {
    public List<BonusRegionData> regions = new ArrayList<BonusRegionData>();
    private BattleModel bm;
    
    public BonusRegionsModel(BattleModel bm, Map map) {
    	this.bm = bm;
    	map.healthsRegions.forEach(healthRegion -> regions.add(new BonusRegionData(this.getPos(healthRegion), new Vector3(0.0f, 0.0f, 0.0f), "MEDKIT")));
    	map.armorsRegions.forEach(armrRegion -> regions.add(new BonusRegionData(this.getPos(armrRegion), new Vector3(0.0f, 0.0f, 0.0f), "ARMORUP")));
    	map.damagesRegions.forEach(dmgRegion -> regions.add(new BonusRegionData(this.getPos(dmgRegion), new Vector3(0.0f, 0.0f, 0.0f), "DAMAGEUP")));
    	map.nitrosRegions.forEach(nitroRegion -> regions.add(new BonusRegionData(this.getPos(nitroRegion), new Vector3(0.0f, 0.0f, 0.0f), "NITRO")));
    }
    
    public void createRegion(BonusRegionData data) {
    	List<BonusRegionData> regionss = new ArrayList<BonusRegionData>();
    	regionss.add(data);
    	regions.add(data);
    	this.bm.send2Battle(new Command(Commands.InitBonusRegions, JSON.parseInitBonusRegionsData(regionss)));
    }
    
    public void removeRegion(BonusRegionData data) {
    	List<BonusRegionData> regionss = new ArrayList<BonusRegionData>();
    	regionss.add(data);
    	regions.remove(data);
    	this.bm.send2Battle(new Command(Commands.RemoveBonusRegions, JSON.parseInitBonusRegionsData(regionss)));
    }
    
    public void removeAllGoldRegionsByBonuses() {
    	List<BonusRegionData> regionss = new ArrayList<BonusRegionData>();
    	for (BattleBonus b : this.bm.bonuses.values()) {
    		if (b.id.startsWith("gold") || b.id.startsWith("container")) {
    			if (b.bonusRegionData != null) {
    				regionss.add(b.bonusRegionData);
    			}
    		}
    	}
    	this.bm.send2Battle(new Command(Commands.RemoveBonusRegions, JSON.parseInitBonusRegionsData(regionss)));
    }
    
    public Vector3 getPos(BonusRegion region) {
    	float x = (region.min.x + region.max.x) / 2;
    	float y = (region.min.y + region.max.y) / 2;
    	float z = (region.min.z + region.max.z) / 2;
    	return new Vector3(x, y, z);
    }
}
