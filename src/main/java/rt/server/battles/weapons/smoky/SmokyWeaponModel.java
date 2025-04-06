package rt.server.battles.weapons.smoky;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import rt.server.battles.BattleController;
import rt.server.battles.weapons.WeaponHandler;
import rt.server.services.calculators.DamageCalculator;
import rt.server.platform.Model;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;

public class SmokyWeaponModel implements WeaponHandler {

	@Override
	public void onFire(BattleController battle, String data) {
		// TODO Auto-generated method stub
		try {
			   JSONObject obj = (JSONObject) new JSONParser().parse(data);
			   String victimId = (String) obj.get("victimId");
	           battle.battle.damageController.damageTank(battle.battle.users.get(victimId), battle, 100, false, battle.client.user.equipment.getTurretName());
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	@Override
	public void stopFire(BattleController battle) {
		// TODO Auto-generated method stub

	}

	@Override
	public JSONObject getSFX(int modification) {
		JSONObject sfxData = new JSONObject();
		sfxData.put("explosion", 141042);
		sfxData.put("explosionSound", 603946);
		sfxData.put("shotSound", 660652);
		sfxData.put("shot", 215799);
		return sfxData;		
	}

	@Override
	public Model getModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startFire(BattleController battle, String data) {
	}

}
