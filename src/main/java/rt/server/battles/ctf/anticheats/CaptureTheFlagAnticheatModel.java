/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package rt.server.battles.ctf.anticheats;

import rt.server.battles.ctf.flags.FlagServer;
import rt.server.battles.ctf.flags.FlagState;
import rt.server.battles.BattleController;
import rt.server.battles.BattleModel;
import java.util.HashMap;

public class CaptureTheFlagAnticheatModel {
    private final HashMap<BattleController, Data> datas = new HashMap<BattleController, Data>();
    private final BattleModel bfModel;

    public CaptureTheFlagAnticheatModel(BattleModel bfModel) {
        this.bfModel = bfModel;
    }

    public boolean onTakeFlag(BattleController taker, FlagServer flag) {
        Data data = this.datas.get(taker);
        if (data == null) {
            data = new Data();
            this.datas.put(taker, data);
        }
        data.lastTimeTakeFlag = System.currentTimeMillis();
        data.prevState = flag.state;
        return false;
    }

    public boolean onDeliveredFlag(BattleController taker) {
        Data data = this.datas.get(taker);
        long time = System.currentTimeMillis() - data.lastTimeTakeFlag;
        if (time <= 4000L && data.prevState == FlagState.BASE) {
        	taker.onDisconnect();
            return true;
        }
        if (time <= 4000L && data.prevState == FlagState.DROPED) {
            taker.onDisconnect();
            return true;
        }
        return false;
    }

    static class Data {
        long lastTimeTakeFlag;
        FlagState prevState;

        Data() {
        }
    }
}

