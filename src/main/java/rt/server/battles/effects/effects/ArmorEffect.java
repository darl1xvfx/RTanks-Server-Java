package rt.server.battles.effects.effects;

import java.util.Timer;
import java.util.TimerTask;
import rt.server.battles.BattleController;
import rt.server.math.Vector3;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;

public class ArmorEffect extends TimerTask implements Effect {
	
    private BattleController player;
    private boolean fromInventory;
    private boolean deactivated;

	@Override
	public void activate(BattleController controller, boolean fromInventory, Vector3 pos) {
        this.fromInventory = fromInventory;
        this.player = controller;
        this.player.tank.activeEffects.add(this);
		new Timer().schedule(this, this.fromInventory ? 60000L : 40000L);
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
		this.player.tank.activeEffects.remove(this);
		this.player.battle.send2Battle(new Command(Commands.DisableEffect, this.player.client.user.username, this.getID()));
	}

	@Override
	public String getEffectType() {
		return "armor";
	}

	@Override
	public int getID() {
		return 2;
	}

	@Override
	public int getDurationTime() {
		return 45000;
	}

	@Override
	public void run() {
        if (!this.deactivated) {
            this.deactivate();
        }	
	}
}
