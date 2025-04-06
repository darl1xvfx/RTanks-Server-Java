package rt.server.battles.weapons.frezee;

import org.json.simple.JSONObject;

import rt.server.battles.BattleController;
import rt.server.battles.weapons.WeaponHandler;
import rt.server.platform.Model;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;

public class FreezeWeaponModel implements WeaponHandler {

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
		sfxData.put("frezee_freezeballs", 458308);
		sfxData.put("frezee_muzzle", 458309);
		sfxData.put("shotSound", 458307);
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
