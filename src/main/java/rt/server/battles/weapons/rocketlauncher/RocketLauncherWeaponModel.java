package rt.server.battles.weapons.rocketlauncher;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import rt.server.battles.BattleController;
import rt.server.battles.weapons.WeaponHandler;
import rt.server.platform.Model;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;

public class RocketLauncherWeaponModel implements WeaponHandler {

	@Override
	public void onFire(BattleController battle, String data) {
	}

	@Override
	public JSONObject getSFX(int modification) {
		JSONObject sfxData = new JSONObject();
		sfxData.put("rightShotSounds", 767236);
		sfxData.put("explosionMarkTexture", 546456);
		sfxData.put("rocketFlameTexture", 546457);
		sfxData.put("aimingCompleteSound", 767231);
		sfxData.put("rocketSmokeTexture", 546458);
		sfxData.put("leftHitSounds", 767233);
		sfxData.put("rightHitSounds", 767235);
		sfxData.put("leftShotSounds", 767234);
		sfxData.put("rocketFlightSound", 767237);
		sfxData.put("targetLostSound", 767238);
		sfxData.put("explosionTexture", 546460);
		sfxData.put("aimingSound", 767232);
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
