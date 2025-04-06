package rt.server.battles.weapons.thunder;

import org.json.simple.JSONObject;

import org.json.simple.parser.JSONParser;
import rt.server.battles.BattleController;
import rt.server.battles.weapons.WeaponHandler;
import rt.server.logger.Logger;
import rt.server.platform.Model;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;

public class ThunderWeaponModel implements WeaponHandler {

	@Override
	public void onFire(BattleController battle, String data) {
	}

	@Override
	public void stopFire(BattleController battle) {
		// TODO Auto-generated method stub

	}

	@Override
	public JSONObject getSFX(int modification) {
		JSONObject sfxData = new JSONObject();
		sfxData.put("explosion", 508552);
		sfxData.put("explosionSound", 230565);
		sfxData.put("shotSound", 230566);
		sfxData.put("shot", 508549);
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
