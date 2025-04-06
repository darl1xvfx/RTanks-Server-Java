package rt.server.battles;

import java.util.Random;
import rt.server.services.protocol.commands.*;

public class BattleFundModel {
    public int fund;
    private BattleModel battle;
    private int goldFund;
    
    public BattleFundModel(BattleModel bm) {
    	this.fund = 0;
    	this.battle = bm;
    }
    
    public void addFund(int fund) {
    	if (fund > Integer.MAX_VALUE) {
    		return;
    	}
    	if (fund < Integer.MIN_VALUE) {
    		return;
    	}
    	this.fund += fund;
    	this.calcualteGoldFund(fund);
    	this.battle.send2Battle(new Command(Commands.UpdateFund, this.fund));
    }
    
    public void clearFund() {
    	this.fund = 0;
    	this.goldFund = 0;
    	this.battle.send2Battle(new Command(Commands.UpdateFund, this.fund));
    }
    
    public void calcualteGoldFund(int fund) {
        int onePercent = 70;
        int oneHundredPercent = onePercent * 100;
        this.goldFund += fund;
        int chance = this.goldFund / onePercent;
        Random random = new Random();
        int randomNumber = random.nextInt(oneHundredPercent) / onePercent;
        if (randomNumber < chance) {
            this.battle.bonusModel.spawnGold();
            this.goldFund = 0;
        }
    }
}
