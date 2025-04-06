package rt.server.battles.weapons.isida;

import java.util.ArrayDeque;
import java.util.Queue;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import rt.server.battles.BattleController;
import rt.server.battles.weapons.WeaponHandler;
import rt.server.platform.Model;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;

public class IsisWeaponModel implements WeaponHandler {
    private double accumulatedPointsForHealing = 0.0;
    private Queue<Long> tickTimes = new ArrayDeque<>();
	@Override
	public void onFire(BattleController battle, String data) {
        JSONObject parser = null;

        try {
            parser = (JSONObject)(new JSONParser()).parse(data);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        long currentTime = System.currentTimeMillis();
        this.tickTimes.add(currentTime);

        float damage = 103f;//RandomUtils.getRandom(this.entity.damage_min, this.entity.damage_min) / 2.0F;
        /*if (damage > 103) {
            //this.bfModel.cheatDetected(this.player, this.getClass());
            return;`
        }*/

        while (!this.tickTimes.isEmpty() && currentTime - this.tickTimes.peek() > 5000) {
            this.tickTimes.poll();
        }

        /*if (this.tickTimes.size() > 15) {
            //this.bfModel.cheatDetected(this.player, this.getClass());
            return;
        }*/

        String victimId = (String)parser.get("victimId");
        if (victimId != null && !victimId.isEmpty()) {
            BattleController target = battle.battle.users.get(victimId);
            if (target != null) {
                if (!((float)((int)(target.tank.position.distanceTo(battle.tank.position) / 100.0)) > 15.0)) {
                    this.onTarget(new BattleController[]{target}, battle, (int)(long)parser.get("distance"));
                }
            }
        }	
	}
	
    private void calculateHealedScore(BattleController player) {
        if (this.accumulatedPointsForHealing > 0.0) {
            player.client.addScore((int)this.accumulatedPointsForHealing);
        }

        player.userStat.score += (int)this.accumulatedPointsForHealing;
        player.updateStat();
        this.accumulatedPointsForHealing = 0.0;
    }

    private void addScoreForHealing(float healedPoint, BattleController player, BattleController patient) {
        int patientRating = patient.client.user.rang;
        int healerRating = player.client.user.rang;
        double scorePoints = Math.atan((double)((patientRating - healerRating) / (healerRating + 1) + 1)) / Math.PI * 90.0 * (double)healedPoint / 100.0;
        this.accumulatedPointsForHealing += scorePoints;
    }

    public void onTarget(BattleController[] targetsTanks, BattleController player, int distance) {
        if (distance != 1500) {
            //this.bfModel.cheatDetected(this.player, this.getClass());
        }

        float damage = 103f;
        if (!player.battle.getBattleEntity().type.equals("DM") && player.playerTeamType.equals(targetsTanks[0].playerTeamType)) {
            if (player.battle.damageController.healPlayer(player, targetsTanks[0], damage)) {
                this.addScoreForHealing(damage, player, targetsTanks[0]);
            }
        } else {
            player.battle.damageController.damageTank(targetsTanks[0], player, damage, false, player.client.user.equipment.getTurretName());
            player.battle.damageController.healPlayer(player, player, damage / 2.0F);
        }

    }
    
	@Override
	public JSONObject getSFX(int modification) {
		JSONObject sfxData = new JSONObject();
		sfxData.put("damage", 214553);
		sfxData.put("damage_shaft", 214555);
		sfxData.put("heal", 214552);
		sfxData.put("heal_shaft", 214554);
		sfxData.put("idleSound", 579361);
		sfxData.put("damagingSound", 579360);
		sfxData.put("healingSound", 579359);
		return sfxData;
	}
	@Override
	public Model getModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stopFire(BattleController c) {
		// TODO Auto-generated method stub
		this.calculateHealedScore(c);
	}

	@Override
	public void startFire(BattleController battle, String data) {
		battle.battle.send2Battle(new Command(Commands.StartFire, battle.client.user.username));
		
	}
}
