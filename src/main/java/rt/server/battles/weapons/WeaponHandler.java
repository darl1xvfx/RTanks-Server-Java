package rt.server.battles.weapons;

import org.json.simple.JSONObject;
import rt.server.battles.BattleController;
import rt.server.platform.Model;

public interface WeaponHandler {
    void startFire(BattleController battle, String data);
    void onFire(BattleController battle, String data);
    void stopFire(BattleController battle);
    JSONObject getSFX(int modification);
    Model getModel();
}
