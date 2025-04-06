package rt.server.battles.weapons.flamethrower;

import java.util.ArrayDeque;
import java.util.Queue;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import rt.server.battles.BattleController;
import rt.server.battles.weapons.WeaponHandler;
import rt.server.platform.Model;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;

public class FlamethrowerWeaponModel implements WeaponHandler {
	
	private Queue<Long> tickTimes = new ArrayDeque<>();

	@Override
	public void onFire(BattleController battle, String data) {
	        try {
	            JSONObject parser = (JSONObject)(new JSONParser()).parse(data);
	            JSONArray arrayTanks = (JSONArray)parser.get("targetsIds");

	            if (arrayTanks.size() == 0) {
	                return;
	            }

	            for(int i = 0; i < arrayTanks.size(); ++i) {
	                BattleController target = battle.battle.users.get((String)arrayTanks.get(i));
	                if (target != null) {
	                    float distance = (float)((int)(target.tank.position.distanceTo(battle.tank.position) / 100.0));
	                    if (distance > 24) {
	                        return;
	                    }
	                    long currentTime = System.currentTimeMillis();
	                    this.tickTimes.add(currentTime);

	                    while (!this.tickTimes.isEmpty() && currentTime - this.tickTimes.peek() > 5000) {
	                        this.tickTimes.poll();
	                    }

	                    if (this.tickTimes.size() > 14) {
	                        return;
	                    }

	                    this.onTarget(new BattleController[]{target}, battle, 0);
	                }
	            }
	        } catch (Exception var7) {
	            var7.printStackTrace();
	        }
	    }

	    public void onTarget(BattleController[] targetsTanks, BattleController player, int distance) {
	        if (targetsTanks[0] == null) {
	            return;
	        }
	        player.battle.damageController.damageTank(targetsTanks[0], player, 100, true, player.client.user.equipment.getTurretName());
	        BattleController victim = targetsTanks[0];
	        if (victim != null && victim.tank != null) {
	            boolean canFlame = true;
	            if (!player.battle.getBattleEntity().equals("DM")) {
	                canFlame = !player.playerTeamType.equals(victim.playerTeamType);
	            }
	            try {
	                if (canFlame) {
	                   /* if (victim.tank.flameEffect == null) {
	                        victim.tank.flameEffect = new FlamethrowerEffectModel(this.entity.coolingSpeed, victim.tank, player.battle, targetsTanks[0], player, 100);
	                        victim.tank.flameEffect.setStartSpecFromTank();
	                    }
	                    victim.tank.flameEffect.update();*/
	                }
	            }  catch (NullPointerException e) {}
	        }
	    }

	@Override
	public void stopFire(BattleController battle) {
		// TODO Auto-generated method stub

	}

	@Override
	public JSONObject getSFX(int modification) {
		JSONObject sfxData = new JSONObject();
		sfxData.put("shotSound", 181945);
		sfxData.put("flamethrower_muzzle", 721058);
		sfxData.put("flamethrower_fire", 855990);
		return sfxData;
	}

	@Override
	public Model getModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startFire(BattleController battle, String data) {
		battle.battle.send2Battle(new Command(Commands.StartFire, battle.client.user.username));
		
	}

}
