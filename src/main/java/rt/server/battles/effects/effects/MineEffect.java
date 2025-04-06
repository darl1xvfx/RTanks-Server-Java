package rt.server.battles.effects.effects;

import java.util.Timer;
import java.util.TimerTask;
import rt.server.battles.BattleController;
import rt.server.math.Vector3;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;

public class MineEffect extends TimerTask implements Effect {
	
    private BattleController player;
    private boolean deactivated;

	@Override
	public void activate(BattleController controller, boolean fromInventory, Vector3 pos) {
        if (!fromInventory) {
            throw new IllegalArgumentException("Effect 'Mine' was not caused from inventory!");
        }
        this.player = controller;
        controller.battle.battleMinesModel.tryPutMine(controller, pos);
		new Timer().schedule(this, 3000L);
	}

	@Override
	public void deactivate() {
		if (this.player == null) {
			return;
		}
		if (this.player.tank == null) {
			return;
		}
		this.deactivated = true;
		this.player.battle.send2Battle(new Command(Commands.DisableEffect, this.player.client.user.username, this.getID()));
	}

	@Override
	public String getEffectType() {
		return "mine";
	}

	@Override
	public int getID() {
		return 5;
	}

	@Override
	public int getDurationTime() {
		return 1;
	}

	@Override
	public void run() {
        if (!this.deactivated) {
            this.deactivate();
        }	
	}
}
