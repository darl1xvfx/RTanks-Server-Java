package rt.server.battles.effects.effects;

import rt.server.battles.BattleController;
import rt.server.services.calculators.DamageCalculator;
import rt.server.math.Vector3;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;

public class HealthEffect extends Thread implements Effect {
	
    private BattleController player;
    private boolean deactivated;

	@Override
	public void activate(BattleController controller, boolean fromInventory, Vector3 pos) {
        this.player = controller;
        this.player.tank.activeEffects.add(this);
        this.start();
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
		return "health";
	}

	@Override
	public int getID() {
		return 1;
	}

	@Override
	public int getDurationTime() {
		return 3000;
	}

	@Override
	public void run() {
        try {
            if (this.player.tank.health == 10000) {
                Thread.sleep(2500L);
                if (!this.deactivated) {
                    this.deactivate();
                }
            }

            while(this.player.tank.health < 10000) {
                if (this.player.tank.health + DamageCalculator.calculateHealth(player.tank, 50.0F) >= 10000) {
                    int d = 10000 - this.player.tank.health;
                    this.player.battle.damageController.healPlayer(player, player, d);
                    if (!this.deactivated) {
                        this.deactivate();
                    }
                } else {
                    this.player.battle.damageController.healPlayer(player, player, 50.0F);
                    Thread.sleep(25L);
                }
            }
        } catch (InterruptedException var2) {
            var2.printStackTrace();
        }
	}
}
