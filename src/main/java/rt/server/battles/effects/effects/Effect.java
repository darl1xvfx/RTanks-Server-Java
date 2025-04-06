package rt.server.battles.effects.effects;

import rt.server.battles.BattleController;
import rt.server.math.Vector3;

public interface Effect {
    public void activate(BattleController controller, boolean fromInventory, Vector3 pos);
    public void deactivate();
    public String getEffectType();
    public int getID();
    public int getDurationTime();
}
