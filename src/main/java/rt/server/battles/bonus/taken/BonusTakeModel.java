package rt.server.battles.bonus.taken;

import rt.server.ServerProperties;
import rt.server.battles.BattleController;
import rt.server.battles.BattleModel;
import rt.server.battles.bonus.BattleBonus;
import rt.server.battles.bonus.SuperGoldModel;
import rt.server.challenges.QuestEntity;
import rt.server.challenges.QuestManager;
import rt.server.localization.LocalizationLoader;
import rt.server.logger.Logger;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;

public class BonusTakeModel {
    private BattleModel bm;
    
    public BonusTakeModel(BattleModel bm) {
    	this.bm = bm;
    }
    
    public void onTake(BattleController user, String id) { //TODO(TitanoMachina) подбор дропа ебаный рот нахуй
       String idd = id.split("_")[0];
       BattleBonus bonus = (BattleBonus)this.bm.bonuses.get(id);
       if (bonus == null) {
    	   Logger.log(Logger.ERROR, "Пошёл нахуй, кста");
    	   return;
       }
	   if (!idd.equals("gold") && !idd.equals("container")) {
		   this.bm.bonusModel.spawnBonusAfterTaken(idd, bonus.pos);
	   }
	   switch (idd) {
	       case "nitro":
	    	    user.inventoryModel.onActivateItem("nitro", false, null);
	    	    QuestManager.getInstance().updateQuestsProgress("take_nitro_box", 1, user.client.user);
		        break;
	       case "health":
	    	    user.inventoryModel.onActivateItem("health", false, null);
	    	    QuestManager.getInstance().updateQuestsProgress("take_repair_kit_box", 1, user.client.user);
	    	    break;
	       case "armor":
	    	    user.inventoryModel.onActivateItem("armor", false, null);
	    	    QuestManager.getInstance().updateQuestsProgress("take_double_armour_box", 1, user.client.user);
	    	    break;
	       case "damage":
	    	    user.inventoryModel.onActivateItem("double_damage", false, null);
	    	    QuestManager.getInstance().updateQuestsProgress("take_double_damage_box", 1, user.client.user);
	    	    break;
	       case "gold":
	    	    if (bonus.bonusRegionData != null) {
	    	    	this.bm.bonusRegionsModel.removeRegion(bonus.bonusRegionData);
	    	    };
	    	    if (ServerProperties.SUPERGOLDS_ENABLED) {
	    	    	String goldPrize = SuperGoldModel.getRandomGoldPrize();
	    	    	SuperGoldModel.checkPrizes(user, goldPrize);
	    	    	SuperGoldModel.battleMessageLocalized(user, LocalizationLoader.getString(goldPrize));
	    	    	this.bm.userLogLocalized(user.client.user.username, LocalizationLoader.getString("SUPERGOLD_CAUGHT"));
	    	    } else {
		    	    this.bm.userLog(user.client.user.username, "gold box");
			    	user.client.addCrystals(ServerProperties.GOLD_CRYSTALS_COUNT);
			    	QuestManager.getInstance().updateQuestsProgress("take_gold_box", 1, user.client.user);
	    	    }
	    	    this.bm.usedGoldRegions.remove(id);
	    	    if (this.bm.unusedGoldBoxes > 0) {
	    	    	this.bm.bonusModel.spawnGold();
	    	    	this.bm.unusedGoldBoxes--;
	    	    }
	    	    break;
	       case "container":
	    	    if (bonus.bonusRegionData != null) {
	    	    	this.bm.bonusRegionsModel.removeRegion(bonus.bonusRegionData);
	    	    };
	    	    this.bm.userLog(user.client.user.username, "gold box");
	    	    this.bm.usedGoldRegions.remove(id);
	    	    if (this.bm.unusedGoldBoxes > 0) {
	    	    	this.bm.bonusModel.spawnGold();
	    	    	this.bm.unusedGoldBoxes--;
	    	    }
	    	    break;
	   }
       this.bm.send2Battle(new Command(Commands.TakeBonusBy, id));
	   this.bm.bonuses.remove(id);
    }
}
