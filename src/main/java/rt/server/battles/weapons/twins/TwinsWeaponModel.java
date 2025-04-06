package rt.server.battles.weapons.twins;

import org.json.simple.JSONObject;

import rt.server.battles.BattleController;
import rt.server.battles.weapons.WeaponHandler;
import rt.server.platform.Model;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;

public class TwinsWeaponModel implements WeaponHandler {

	@Override
	public void onFire(BattleController battle, String data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopFire(BattleController battle) {
		// TODO Auto-generated method stub

	}

	@Override
	public JSONObject getSFX(int modification) {
		JSONObject sfxData = new JSONObject();
		sfxData.put("explosion", 201449);
		sfxData.put("explosionSound", 945273);
		sfxData.put("shotSound", 945273);
		sfxData.put("muzzle", 201459);
		sfxData.put("shot", 558241);
		return sfxData;
	}

	@Override
	public Model getModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startFire(BattleController battle, String data) {
		battle.battle.send2Battle(new Command(Commands.StartFireTwins, battle.client.user.username, data));
		
	}

}
