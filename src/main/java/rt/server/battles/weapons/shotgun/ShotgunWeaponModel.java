package rt.server.battles.weapons.shotgun;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import rt.server.battles.BattleController;
import rt.server.battles.weapons.WeaponHandler;
import rt.server.logger.Logger;
import rt.server.platform.Model;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;

public class ShotgunWeaponModel implements WeaponHandler {

	@Override
	public void onFire(BattleController battle, String data) {

	}
	
    public void onTarget(BattleController[] targetsTanks, int distance, BattleController tank) {
    }

	@Override
	public JSONObject getSFX(int modification) {
		JSONObject sfxData = new JSONObject();
		sfxData.put("shotgunSparkleTexture", 64863);
		sfxData.put("shotgunExplosionMarkTexture2", 82432);
		sfxData.put("shotgunExplosionMarkTexture1", 82431);
		sfxData.put("shotgunExplosionMarkTexture4", 82434);
		sfxData.put("shotgunMagazineReloadSound", 962340);
		sfxData.put("shotgunExplosionMarkTexture3", 82433);
		sfxData.put("shotgunSmokeTexture", 262362);
		sfxData.put("shotgunShotAcrossTexture", 262364);
		sfxData.put("shotgunPelletTrailTexture", 826482);
		sfxData.put("shotgunShotAlongTexture", 262363);
		sfxData.put("shotgunShotSound", 962342);
		sfxData.put("shotgunReloadSound", 962341);
		return sfxData;	
	}

	@Override
	public Model getModel() {
		return new ShotgunAiming();
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
