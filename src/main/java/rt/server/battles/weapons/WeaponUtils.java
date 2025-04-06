package rt.server.battles.weapons;

import java.util.HashMap;

import rt.server.battles.weapons.flamethrower.FlamethrowerWeaponModel;
import rt.server.battles.weapons.frezee.FreezeWeaponModel;
import rt.server.battles.weapons.isida.IsisWeaponModel;
import rt.server.battles.weapons.machinegun.MachineGunWeaponModel;
import rt.server.battles.weapons.railgun.RailgunWeaponModel;
import rt.server.battles.weapons.ricochet.RicochetWeaponModel;
import rt.server.battles.weapons.shaft.ShaftWeaponModel;
import rt.server.battles.weapons.shotgun.ShotgunWeaponModel;
import rt.server.battles.weapons.smoky.SmokyWeaponModel;
import rt.server.battles.weapons.thunder.ThunderWeaponModel;
import rt.server.battles.weapons.twins.TwinsWeaponModel;

public class WeaponUtils {
    private static HashMap<String, WeaponHandler> weapons = new HashMap<>();
    
    public static void init() {
    	weapons.put("smoky", new SmokyWeaponModel());
    	weapons.put("vulcan", new MachineGunWeaponModel());
    	weapons.put("shotgun", new ShotgunWeaponModel());
    	weapons.put("railgun", new RailgunWeaponModel());
    	weapons.put("isida", new IsisWeaponModel());
    	weapons.put("ricochet", new RicochetWeaponModel());
    	weapons.put("thunder", new ThunderWeaponModel());
    	weapons.put("shaft", new ShaftWeaponModel());
    	weapons.put("twins", new TwinsWeaponModel());
    	weapons.put("frezee", new FreezeWeaponModel());
    	weapons.put("flamethrower", new FlamethrowerWeaponModel());
    }
    
    public static WeaponHandler getWeaponByName(String name) {
    	return weapons.get(name);
    }
}
