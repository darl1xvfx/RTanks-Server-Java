package rt.server.battles.bonus;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import rt.server.battles.BattleModel;
import rt.server.battles.bonus.regions.BonusRegionData;
import rt.server.battles.maps.parser.Map;
import rt.server.math.Vector3;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;
import rt.server.utils.JSON;

public class BonusModel {
	
	public BattleModel battle;
	public Map map;
	private Random random = new Random();
	
	public BonusModel(BattleModel bm, Map map) {
		this.battle = bm;
		this.map = map;
	}
	
	public void start() {
	    Timer timer = new Timer();
	    
	    for (int i = 0; i < this.map.healthsRegions.size(); i++) {
	        final BonusRegion region = this.map.healthsRegions.get(i);
	        timer.schedule(new TimerTask() {
	            @Override
	            public void run() {
	                spawnBonus("health", region);
	            }
	        }, 35200);
	    }
	    
	    for (int i = 0; i < this.map.armorsRegions.size(); i++) {
	        final BonusRegion region = this.map.armorsRegions.get(i);
	        timer.schedule(new TimerTask() {
	            @Override
	            public void run() {
	                spawnBonus("armor", region);
	            }
	        }, 27310);
	    }
	    
	    for (int i = 0; i < this.map.damagesRegions.size(); i++) {
	        final BonusRegion region = this.map.damagesRegions.get(i);
	        timer.schedule(new TimerTask() {
	            @Override
	            public void run() {
	                spawnBonus("damage", region);
	            }
	        }, 49430);
	    }
	    
	    for (int i = 0; i < this.map.nitrosRegions.size(); i++) {
	        final BonusRegion region = this.map.nitrosRegions.get(i);
	        timer.schedule(new TimerTask() {
	            @Override
	            public void run() {
	                spawnBonus("nitro", region);
	            }
	        }, 56890);
	    }
	}

    
    private Vector3 getRandomSpawnPostiton(BonusRegion region) {
        Vector3 position = new Vector3(0.0F, 0.0F, 0.0F);
        Random rand = new Random();
        position.x = region.min.x + (region.max.x - region.min.x) * rand.nextFloat();
        position.y = region.min.y + (region.max.y - region.min.y) * rand.nextFloat();
        position.z = region.max.z;
        return position;
     }
    
    public void spawnGold() {
    	this.spawnBonus("gold", this.map.goldsRegions.get(this.random.nextInt(this.map.goldsRegions.size())));
    }
    
    public void spawnContainer() {
    	this.spawnBonus("container", this.map.goldsRegions.get(this.random.nextInt(this.map.goldsRegions.size())));
    }
    
    public void spawnBonusAfterTaken(String id, Vector3 pos) {
        id = (id + "_" + BonusIdManager.getUniqueId());
        BattleBonus bonus = new BattleBonus(id, pos);
        this.battle.bonuses.put(id, bonus);
            
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                battle.send2Battle(new Command(Commands.SpawnBonus, JSON.parseSpawnBonusData(bonus)));        
            }
        }, 30000);
    }
    
    public void spawnBonus(String id, BonusRegion pos) {
        id = (id + "_" + BonusIdManager.getUniqueId());
        BattleBonus bonus = new BattleBonus(id, this.getRandomSpawnPostiton(pos));
        
        if (id.startsWith("gold") || id.startsWith("container")) {
            if (this.battle.usedGoldRegions.size() >= this.map.goldsRegions.size()) {
            	this.battle.unusedGoldBoxes++;
                return;
            }
            
            while (this.battle.usedGoldRegions.containsValue(pos)) {
                pos = this.map.goldsRegions.get(this.random.nextInt(this.map.goldsRegions.size()));
            }
            
            bonus.pos = this.getRandomSpawnPostiton(pos);
            BonusRegionData brd = new BonusRegionData(this.battle.bonusRegionsModel.getPos(pos), new Vector3(0.0f, 0.0f, 0.0f), "CRYSTAL_100");
            this.battle.send2Battle(new Command(Commands.SpawnGold));
            this.battle.bonusRegionsModel.createRegion(brd);
            this.battle.usedGoldRegions.put(id, pos);
            bonus.bonusRegionData = brd;
            
        }
        this.battle.bonuses.put(id, bonus);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                battle.send2Battle(new Command(Commands.SpawnBonus, JSON.parseSpawnBonusData(bonus)));        
            }
        }, id.startsWith("gold") || id.startsWith("container") ? 2000 : 1);
    }
    
    public static String getBonusIdByName(String name) {
    	switch (name) {
 	       case "health":
		       return "681826";
	       case "armor":
		       return "12498";
	       case "damage":
		       return "957960";
	       case "nitro":
		       return "673121";
    	   case "gold":
    		   return "884923";
    	   case "container":
    		   return "884924";
    	}
    	return "";
    }
}
