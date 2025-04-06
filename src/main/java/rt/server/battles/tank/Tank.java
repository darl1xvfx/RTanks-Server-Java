package rt.server.battles.tank;

import java.util.ArrayList;
import java.util.Iterator;
import org.json.simple.JSONObject;
import rt.server.battles.BattleController;
import rt.server.battles.effects.effects.Effect;
import rt.server.battles.weapons.WeaponHandler;
import rt.server.math.Vector3;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;

public class Tank {
	
	public Vector3 position = new Vector3(0.0f, 0.0f, 0.0f);
    public Vector3 orientation = new Vector3(0.0f, 0.0f, 0.0f);
    public Vector3 linVel;
    public Vector3 angVel;
    public double turretDir;
    public int controllBits;
    public String id;
	public WeaponHandler weapon;
	public int health;
	private BattleController control;
	public ArrayList<Effect> activeEffects;
	public TankHull hull;
	public String state = "suicide";
	
	public Tank(BattleController c, WeaponHandler weapon, TankHull hull) {
		this.control = c;
		this.hull = hull;
		this.weapon = weapon;
		this.activeEffects = new ArrayList<>();
	}
	
    public void addHealth(int health) {
    	this.health += health;
    	if (this.health <= 0) {
    		this.health = 0;
    	}
    	this.control.battle.send2Battle(new Command(Commands.ChangeHealth, this.id, this.health));
    }
    
    public void setHealth(int health) {
    	this.health = health;
    	if (this.health <= 0) {
    		this.health = 0;
    	}
    	this.control.battle.send2Battle(new Command(Commands.ChangeHealth, this.id, this.health));
    }
	
	public JSONObject getSoundsObject() {
		JSONObject sounds = new JSONObject();
		sounds.put("engineIdleSoundId", 73466);
		sounds.put("engineMovingSoundId", 63282);
		sounds.put("turretRotationSoundId", 2863);
		sounds.put("engineStartMovingSoundId", 20098);
		return sounds;
	}
	
    public String concatPosition() {
    	String cs = position.x + "@" + position.y + "@" + position.z;
    	return cs;
    }
    
    public boolean isUsedEffect(String type) {
        Iterator var3 = this.activeEffects.iterator();

        while(var3.hasNext()) {
           Effect effect = (Effect)var3.next();
           if (effect.getEffectType().equals(type)) {
              return true;
           }
        }

        return false;
     }
    
    public boolean removeUsedEffect(String type) {
        Iterator var3 = this.activeEffects.iterator();

        while(var3.hasNext()) {
           Effect effect = (Effect)var3.next();
           if (effect.getEffectType().equals(type)) {
        	  effect.deactivate();
              this.activeEffects.remove(effect);
              return true;
           }
        }
        return false;
     }
}
