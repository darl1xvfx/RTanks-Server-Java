package rt.server.battles.spawn;

import java.util.Timer;
import java.util.TimerTask;
import rt.server.battles.BattleController;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;
import rt.server.utils.JSON;

public class TankSpawnService {
    public static void spawn(BattleController controller, boolean isRespawn) {
        if (controller == null) {
            return;
        }
        if (controller.battle == null) {
            return;
        }
        PrepareSpawnTask task = new PrepareSpawnTask();
        task.battleController = controller;
        new Timer().schedule(task, isRespawn ? 2500L : 1L);
    }
}

class PrepareSpawnTask extends TimerTask {
    
	public BattleController battleController;
	
	@Override
	public void run() {
        if (battleController == null) {
            return;
        }
        if (battleController.tank == null) {
            return;
        }
        if (battleController.battle == null) {
            return;
        }
        new Command(Commands.PrepareToSpawn, battleController.client.user.username, battleController.tank.concatPosition() + "@" + battleController.tank.position.rot).send(battleController.client);
        SpawnTask spawnTask = new SpawnTask();
        spawnTask.battleController = battleController;
        new Timer().schedule(spawnTask, 5000L);
	}
}

class SpawnTask extends TimerTask {
    
	public BattleController battleController;
	
	@Override
	public void run() {
        if (battleController == null) {
            return;
        }
        if (battleController.tank == null) {
            return;
        }
        if (battleController.battle == null) {
            return;
        }
		if (battleController.battle.battleFinish) { 
			return;
		}
        battleController.tank.state = "newcome";
        if (battleController.changedEquipment) {
            this.battleController.changeEquipment();
        }
        this.battleController.tank.setHealth((int) battleController.tank.hull.hp);
		this.battleController.battle.send2Battle(new Command(Commands.Spawn, JSON.parseSpawnCommand(battleController)));
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				battleController.tank.state = "active";
				battleController.battle.send2Battle(new Command(Commands.ActivateTank, battleController.client.user.username));
			}
			
		}, 3000L);
	}
}
