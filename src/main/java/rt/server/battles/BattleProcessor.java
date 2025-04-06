package rt.server.battles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import rt.server.GameServer;
import rt.server.logger.Logger;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;

public class BattleProcessor {
	public static HashMap<String, BattleModel> battles;
	
	public static void init() {
		Logger.log(Logger.INFO, "Init BattleProccessor...");
		battles = new HashMap<String, BattleModel>();
	}
	
	public static void addBattle(BattleModel battle) {
		battles.put(battle.getBattleEntity().battleId, battle);
	}
	
	public static String generateId() {
	    Random random = new Random();
	    long randomValue = random.nextLong() & Long.MAX_VALUE;
	    String hexString = Long.toHexString(randomValue);
	    return String.format("%1$" + 16 + "s", hexString).replace(' ', '0');
	}

	public static BattleModel getBattleById(String battleId) {
        return battles.get(battleId);
	}
	
	public static List<BattleModel> getBattles() {
		List<BattleModel> battless = new ArrayList<>();
		for (BattleModel bm : battles.values()) {
			 battless.add(bm);
		}
		return battless;
	}

	public static void removeBattle(BattleModel battle) {
		if (battles.containsKey(battle.getBattleEntity().battleId)) {
			battles.remove(battle.getBattleEntity().battleId);
			GameServer.send2Lobbys(new Command(Commands.RemoveBattle, battle.getBattleEntity().battleId));
		}	
	}
}
