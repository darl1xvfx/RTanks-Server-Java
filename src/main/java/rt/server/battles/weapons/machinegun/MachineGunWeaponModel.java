package rt.server.battles.weapons.machinegun;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import rt.server.battles.BattleController;
import rt.server.battles.weapons.WeaponHandler;
import rt.server.platform.Model;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;

public class MachineGunWeaponModel implements WeaponHandler {

	@Override
	public void onFire(BattleController battle, String data) {
		try {
		   JSONObject obj = (JSONObject) new JSONParser().parse(data);
		   String victimId = (String) obj.get("victimId");
		   int tickPeriod = (int)(long) obj.get("tickPeriod");
           battle.battle.damageController.damageTank(battle.battle.users.get(victimId), battle, 25, false, battle.client.user.equipment.getTurretName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public JSONObject getSFX(int modification) {
		JSONObject sfxData = new JSONObject();
		sfxData.put("smokeTexture", 791473);
		sfxData.put("shootEndSound", 962222);
		sfxData.put("crumbsTexture", 791477);
		sfxData.put("fireAlongTexture", 791471);
		sfxData.put("shootSound", 962223);
		sfxData.put("dustTexture", 791472);
		sfxData.put("tracerTexture", 791476);
		sfxData.put("tankHitSound", 962226);
		sfxData.put("chainStartSound", 962220);
		sfxData.put("turbineStartSound", 962221);
		sfxData.put("longFailSound", 962224);
		sfxData.put("sparklesTexture", 791474);
		sfxData.put("tankSparklesTexture", 791475);
		sfxData.put("hitSound", 962225);
		sfxData.put("fireAcrossTexture", 791470);
		return sfxData;
	}

	@Override
	public Model getModel() {
		// TODO Auto-generated method stub
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
