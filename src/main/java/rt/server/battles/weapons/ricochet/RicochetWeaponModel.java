package rt.server.battles.weapons.ricochet;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import rt.server.battles.BattleController;
import rt.server.battles.weapons.WeaponHandler;
import rt.server.platform.Model;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;

public class RicochetWeaponModel implements WeaponHandler {
    private long lastFireTime = 0;
    private int shotCount = 0;
    private static final int MAX_SHOT_COUNT = 20;
    private static final long FIRE_INTERVAL = 5000;

	@Override
	public void onFire(BattleController battle, String data) {
        JSONObject parser = null;

        try {
            parser = (JSONObject)(new JSONParser()).parse(data);
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFireTime >= FIRE_INTERVAL) {
            shotCount = 0;
            lastFireTime = currentTime;
        }

        if (shotCount >= MAX_SHOT_COUNT) {
            return;
        }

        /*if (!this.check((int)(long)parser.get("reloadTime"))) {
            this.bfModel.cheatDetected(this.player, this.getClass());
        } else {*/
            boolean selfHit = (Boolean)parser.get("self_hit");
            if (!selfHit) {
                battle.battle.send2Battle(battle, new Command(Commands.FireRicochet, battle.tank.id, data));
            }

            int distance = this.getValueByObject(parser.get("distance"));
            BattleController victim = selfHit ? battle : battle.battle.users.get((String)parser.get("victimId"));
            if (victim != null) {
                this.onTarget(new BattleController[]{victim}, battle, distance);
            }

            shotCount++;
        //}
		
	}
	
    public void onTarget(BattleController[] targetsTanks, BattleController player, int distance) {
        BattleController victim = targetsTanks[0];
        float damage = 68;

        if (damage > 69) {
            return;
        }
        player.battle.damageController.damageTank(victim, player, damage, true, player.client.user.equipment.getTurretName());
    }
    
    private int getValueByObject(Object obj) {
        if (obj == null) {
            return 0;
        } else {
            try {
                return (int)Double.parseDouble(String.valueOf(obj));
            } catch (Exception var3) {
                return Integer.parseInt(String.valueOf(obj));
            }
        }
    }

	@Override
	public void stopFire(BattleController battle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public JSONObject getSFX(int modification) {
		JSONObject sfxData = new JSONObject();
		sfxData.put("trail", 583511);
		sfxData.put("explosion", 605083);
		sfxData.put("ricochetSound", 593235);
		sfxData.put("explosionSound", 343201);
		sfxData.put("shotSound", 508233);
		sfxData.put("muzzle", 37183);
		sfxData.put("shot", 234984);
		return sfxData;
	}

	@Override
	public Model getModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startFire(BattleController battle, String data) {
		battle.battle.send2Battle(new Command(Commands.StartFire, battle.client.user.username, data));
		
	}

}
