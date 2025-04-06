package rt.server.utils;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import rt.server.battles.BattleModel;
import rt.server.battles.BattleProcessor;
import rt.server.logger.Logger;

public class BattleDeleter {
    private static HashMap<String, Timer> battlesForRemove = new HashMap<String, Timer>();

    public static void addBattleForRemove(BattleModel battle) {
        if (battle == null) {
            return;
        }
        Timer timer = new Timer("BattleDeleter::Timer for battle: " + battle.getBattleEntity().battleId);
        timer.schedule((TimerTask)new RemoveBattleTask(battle), 50000L);
        battlesForRemove.put(battle.getBattleEntity().battleId, timer);
    }


    public static void cancelRemoving(BattleModel model) {
        Timer timer = (Timer)battlesForRemove.get(model.getBattleEntity().battleId);
        if (timer != null) {
            timer.cancel();
            battlesForRemove.remove(model.getBattleEntity().battleId);
        }
    }

    private static void removeEmptyBattle(BattleModel battle) {
        Logger.log(Logger.INFO, "Battle " + battle.getBattleEntity().battleId + " has been deleted by inactivity.");
        BattleProcessor.removeBattle(battle);
    }

    static class RemoveBattleTask extends TimerTask {
        private BattleModel battle;

        public RemoveBattleTask(BattleModel battle) {
            this.battle = battle;
        }

        public void run() {
            if (this.battle != null) {
                BattleDeleter.removeEmptyBattle(this.battle);
            }

        }
    }
}
