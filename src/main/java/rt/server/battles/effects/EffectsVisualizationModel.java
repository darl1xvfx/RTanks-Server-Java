package rt.server.battles.effects;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import rt.server.battles.BattleController;
import rt.server.battles.BattleModel;
import rt.server.battles.effects.effects.Effect;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;
import java.util.Iterator;

public class EffectsVisualizationModel {
    private BattleModel bfModel;

    public EffectsVisualizationModel(BattleModel bfModel) {
        this.bfModel = bfModel;
    }

    public void sendInitData(BattleController player) {
        JSONObject _obj = new JSONObject();
        JSONArray array = new JSONArray();
        Iterator var4 = this.bfModel.users.values().iterator();

        while(true) {
            BattleController _player;
            do {
                if (!var4.hasNext()) {
                    _obj.put("effects", array);
                    new Command(Commands.InitEffects, _obj).send(player.client);
                    return;
                }

                _player = (BattleController)var4.next();
            } while(player == _player);

            synchronized(_player.tank.activeEffects) {
                Iterator var7 = _player.tank.activeEffects.iterator();

                while(var7.hasNext()) {
                    Effect effect = (Effect)var7.next();
                    JSONObject obj = new JSONObject();
                    obj.put("userID", _player.client.user.username);
                    obj.put("itemIndex", effect.getID());
                    obj.put("durationTime", 60000);
                    array.add(obj);
                }
            }
        }
    }
}
