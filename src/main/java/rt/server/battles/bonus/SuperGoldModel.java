package rt.server.battles.bonus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import rt.server.ServerProperties;
import rt.server.battles.BattleController;
import rt.server.localization.LocalizedString;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;

public class SuperGoldModel {
	
	private static List<String> messages;
	
	public static void init() {
		messages = new ArrayList<>(Arrays.asList(
		"SUPERGOLD_1000_CRYSTALS",
		"SUPERGOLD_DROPGOLD_X2",
		"SUPERGOLD_5000_CRYSTALS",
		"SUPERGOLD_10000_CRYSTALS",
		"SUPERGOLD_50000_CRYSTALS",
		"SUPERGOLD_FULL_INVENTORY",
		"SUPERGOLD_GODMODE_ON",
		"SUPERGOLD_PREMIUM_1_DAY",
		"SUPERGOLD_PREMIUM_2_DAY",
		"SUPERGOLD_PREMIUM_3_DAY",
		"SUPERGOLD_MONSTER_KILL"
		));
	}
	
    public static String getRandomGoldPrize() {
        String message = "";
        double[] probabilities = {0.15, 0.10, 0.08, 0.03, 0.01, 0.12, 0.10, 0.12, 0.10, 0.05, 0.14};

        Random random = new Random();
        double chance = random.nextDouble();

        double cumulativeProbability = 0;
        for (int i = 0; i < probabilities.length; i++) {
            cumulativeProbability += probabilities[i];
            if (chance < cumulativeProbability) {
                message = messages.get(i);
                break;
            }
        }
        return message;
    }
    
	public static void battleMessageLocalized(BattleController owner, LocalizedString action) {
		if (owner.battle.users.size() != 0) {
			for (BattleController control : owner.battle.users.values()) {
				if (control.userInited) {
					new Command(Commands.BattleMessage, action.get(control.client.locale).replaceAll("%USERNAME%", owner.client.user.username)).send(control.client);
				}
			}	
		}
	}
    
    public static void checkPrizes(BattleController control, String prize) {
    	if(messages.contains(prize)) {
    		switch (prize) {
    		    case "SUPERGOLD_1000_CRYSTALS":
    		    	control.client.addCrystals(ServerProperties.GOLD_CRYSTALS_COUNT);
    		    	return;
    		    case "SUPERGOLD_DROPGOLD_X2":
    		    	control.client.addCrystals(ServerProperties.GOLD_CRYSTALS_COUNT);
    		    	control.battle.bonusModel.spawnContainer();
    		    	control.battle.bonusModel.spawnContainer();
    		    	return;
    		    case "SUPERGOLD_10000_CRYSTALS":
    		    	control.client.addCrystals(ServerProperties.GOLD_CRYSTALS_COUNT * 10);
    		    	return;
    		    case "SUPERGOLD_50000_CRYSTALS":
    		    	control.client.addCrystals(ServerProperties.GOLD_CRYSTALS_COUNT * 50);
    		    	return;
    		    case "SUPERGOLD_5000_CRYSTALS":
    		    	control.client.addCrystals(ServerProperties.GOLD_CRYSTALS_COUNT * 5);
    		    	return;
    		    case "SUPERGOLD_FULL_INVENTORY":
    		    	return;
    		    case "SUPERGOLD_GODMODE_ON":
    		    	return;
    		    case "SUPERGOLD_PREMIUM_1_DAY":
    		    	control.client.addPremium(1);
    		    	return;
    		    case "SUPERGOLD_PREMIUM_2_DAY":
    		    	control.client.addPremium(2);
    		    	return;
    		    case "SUPERGOLD_PREMIUM_3_DAY":
    		    	control.client.addPremium(3);
    		    	return;
    		    case "SUPERGOLD_MONSTER_KILL":
    		    	control.battle.superKillTank(control);
    		    	return;
    		}
    	}
    }
}
