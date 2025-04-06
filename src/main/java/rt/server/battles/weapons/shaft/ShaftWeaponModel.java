package rt.server.battles.weapons.shaft;

import org.json.simple.JSONObject;

import rt.server.battles.BattleController;
import rt.server.battles.weapons.WeaponHandler;
import rt.server.battles.weapons.laser.Laser;
import rt.server.platform.Model;

public class ShaftWeaponModel implements WeaponHandler {

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
		sfxData.put("trail", 514190);
		sfxData.put("zoomSound", 653372);
		sfxData.put("explosionSound", 653375);
		sfxData.put("shotSound", 653374);
		sfxData.put("muzzle", 514191);
		sfxData.put("targetingSound", 653373);
		sfxData.put("shot", 514192);
		return sfxData;
	}

	@Override
	public Model getModel() {
		return new Laser();
	}

	@Override
	public void startFire(BattleController battle, String data) {
		// TODO Auto-generated method stub
		
	}

}
