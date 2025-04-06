package rt.server.battles.effects.effects;

import java.util.Timer;
import java.util.TimerTask;
import rt.server.battles.BattleController;
import rt.server.math.Vector3;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;
import rt.server.logger.Logger;
import rt.server.utils.JSON;

public class NitroEffect extends TimerTask implements Effect {
	private BattleController player;
	private boolean fromInventory;
	private boolean deactivated;
	private double originalSpeed;
	private static final double MULTIPLIER = 1.3;

	@Override
	public void activate(BattleController controller, boolean fromInventory, Vector3 pos) {
		this.fromInventory = fromInventory;
		this.player = controller;
		if (this.player == null || this.player.tank == null) { Logger.log(Logger.ERROR, "NitroEffect.activate - Player or tank is null"); return;}
		this.player.tank.activeEffects.add(this);
		this.originalSpeed = this.player.tank.hull.speed;
		activateEffect();
		new Timer().schedule(this, this.fromInventory ? 60000L : 40000L);
	}

	private void activateEffect() {
		if (this.deactivated) return;
		double newSpeed = this.originalSpeed * MULTIPLIER;
		JSON.parseTankSpecificationChange(this.player, newSpeed, this.player.tank.hull.turnSpeed, 2.6, 2.6);
	}

	@Override
	public void deactivate() {
		if (this.player == null || this.player.tank == null || this.deactivated) return;
		this.deactivated = true;
		JSON.parseTankSpecificationChange(this.player, this.originalSpeed, this.player.tank.hull.turnSpeed, 2.6, 2.6);
		this.player.tank.activeEffects.remove(this);
		this.player.battle.send2Battle(new Command(Commands.DisableEffect, this.player.client.user.username, String.valueOf(this.getID())));
	}

	@Override
	public String getEffectType() {
		return "nitro";
	}

	@Override
	public int getID() {
		return 4;
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