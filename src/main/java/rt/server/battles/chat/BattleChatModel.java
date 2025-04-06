package rt.server.battles.chat;

import rt.server.battles.BattleController;
import rt.server.battles.BattleModel;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;
import rt.server.utils.JSON;

public class BattleChatModel {
	
	private BattleModel bm;
	
	public BattleChatModel(BattleModel battle) {
		this.bm = battle;
	}
	
    public void onData(BattleController owner, String message, boolean team) {
    	if (message.startsWith("/")) {
            String temp = message.replace('/', ' ').trim();
            String[] arguments = temp.split(" ");
            switch (arguments[0]) {
                case "addfund":
                	this.bm.battleFundModel.addFund(Integer.parseInt(arguments[1]));
                	return;
                case "addscore":
                	owner.client.addScore(Integer.parseInt(arguments[1]));
                	return;
                case "addcry":
                	owner.client.addCrystals(Integer.parseInt(arguments[1]));
                	return;
                case "spawngold":
                	if (arguments.length > 1 && arguments[1] != null) {
                		for (int i = 0; i < Integer.valueOf(arguments[1]); i++) {
                			this.bm.bonusModel.spawnGold();
                		}
                	} else {
                		this.bm.bonusModel.spawnGold();
                	}
                	return;
                case "spawncontainer":
                	if (arguments.length > 1 && arguments[1] != null) {
                		for (int i = 0; i < Integer.valueOf(arguments[1]); i++) {
                			this.bm.bonusModel.spawnContainer();
                		}
                	} else {
                		this.bm.bonusModel.spawnContainer();
                	}
                	return;
                case "finish":
                    this.bm.finishBattle();
                	return;
                case "vote":
                	BattleController suspic = owner.battle.users.get(arguments[1]);
                	if (suspic == null) {
                		return;
                	}
                	suspic.updateSuspicious(owner.client.user.username);
                	new Command(Commands.ConfirmVote).send(owner.client);
                	return;
                case "addperm":
                    owner.client.user.permissions = Integer.parseInt(arguments[1]);
                	return;
            }
    	} else {
    		if (team) {
    			if (owner.playerTeamType.equals("RED")) {
    				this.bm.sendToRedTeamPlayers(new Command(Commands.Chat, JSON.parseBattleChatMessage(owner, message, team)));
    			} else {
    				this.bm.sendToBlueTeamPlayers(new Command(Commands.Chat, JSON.parseBattleChatMessage(owner, message, team)));
    			}
    		} else {
    			this.bm.send2Battle(new Command(Commands.Chat, JSON.parseBattleChatMessage(owner, message, team)));
    		}
    	}
    }
}
