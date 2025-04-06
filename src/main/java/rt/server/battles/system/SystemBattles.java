package rt.server.battles.system;

import java.util.ArrayList;
import java.util.List;

import rt.server.battles.BattleEntity;
import rt.server.battles.BattleModel;
import rt.server.battles.BattleProcessor;
import rt.server.utils.GraphicUtils;
import rt.server.utils.SkyboxUtils;

public class SystemBattles {
	
   public static List<String> battleIds = new ArrayList<>();

   public static void init() {
	   BattleEntity battleEntity = new BattleEntity(null, false, false, false, false, 0, 0, 0, 0, null, false, false, 0, 0, false, 0, 0, null, false, false, false, null);
	   battleEntity.battleId = BattleProcessor.generateId();
	   battleEntity.goldBoxesEnabled = true;
	   battleEntity.killsLimit = 25;
	   battleEntity.mapid = "map_sandbox";
	   battleEntity.maxPeople = 10;
	   battleEntity.maxRank = 4;
	   battleEntity.minRank = 1;
	   battleEntity.name = "[TEST] For Newbies DM";
	   battleEntity.reArmorEnabled = true;
	   battleEntity.withoutBonuses = false;
	   battleEntity.withoutInventory = false;
	   battleEntity.graphics = GraphicUtils.getGraphicsByMap(battleEntity.mapid);
	   battleEntity.timeLimit = (battleEntity.timeCurrent = 900);
	   battleEntity.type = "DM";
	   BattleProcessor.addBattle(new BattleModel(battleEntity));
	   battleIds.add(battleEntity.battleId);
   }
}
