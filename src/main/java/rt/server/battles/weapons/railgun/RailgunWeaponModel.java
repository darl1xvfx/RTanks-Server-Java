package rt.server.battles.weapons.railgun;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import rt.server.battles.BattleController;
import rt.server.battles.weapons.WeaponHandler;
import rt.server.platform.Model;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;

public class RailgunWeaponModel implements WeaponHandler {
	
    private long lastShotTime = 0;
    private int shotCount = 0;
    private static final int MAX_SHOT_COUNT_PER_INTERVAL = 2;
    private static final long SHOT_INTERVAL = 6200;

	@Override
	public void onFire(BattleController battle, String data) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastShotTime >= SHOT_INTERVAL) {
            shotCount = 0;
            lastShotTime = currentTime;
        }

        shotCount++;

        if (shotCount > MAX_SHOT_COUNT_PER_INTERVAL) {
            return;
        }

        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(data);
            JSONArray tanks = (JSONArray) json.get("targets");
            /*if (!this.check((int) (long) json.get("reloadTime"))) {
                this.battle.cheatDetected(this.tank, this.getClass());
                return;
            }*/

            if (tanks == null) {
                return;
            }

            BattleController[] tanks_array = new BattleController[tanks.size()];

            for (int i = 0; i < tanks.size(); ++i) {
                tanks_array[i] = (BattleController) battle.battle.users.get(tanks.get(i));
            }

            this.onTarget(tanks_array, 0, battle);
        } catch (Exception var7) {
            var7.printStackTrace();
        }
	}
	
    public void onTarget(BattleController[] targetsTanks, int distance, BattleController tank) {
        if (targetsTanks.length != 0) {
            float damage = 500f;//RandomUtils.getRandom(this.entity.damage_min, this.entity.damage_max);

            /*if (damage > 332) {
                //this.battle.cheatDetected(this.tank, this.getClass());
                return;
            }*/

            for (int i = 0; i < targetsTanks.length; ++i) {
                tank.battle.damageController.damageTank(targetsTanks[i], tank, damage, false, tank.client.user.equipment.getTurretName());
                damage /= 2.0F;
            }
        }
    }

	@Override
	public JSONObject getSFX(int modification) {
		JSONObject sfxData = new JSONObject();
		sfxData.put("beam", 451711);
		sfxData.put("shotSound", 451710);
		sfxData.put("smoke", 451712);
		sfxData.put("charge_part1", 451713);
		sfxData.put("charge_part3", 451715);
		sfxData.put("charge_part2", 451714);
		return sfxData;	
	}

	@Override
	public Model getModel() {
		return null;
	}

	@Override
	public void stopFire(BattleController battle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startFire(BattleController battle, String data) {
		battle.battle.send2Battle(new Command(Commands.StartFire, battle.client.user.username));
	}

}
