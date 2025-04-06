/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package rt.server.battles.ctf.flags;

import rt.server.battles.ctf.FlagReturnTimer;
import rt.server.math.Vector3;
import rt.server.battles.BattleController;

public class FlagServer {
    public String flagTeamType;
    public BattleController owner;
    public Vector3 position;
    public Vector3 basePosition;
    public FlagState state;
    public FlagReturnTimer returnTimer;
}

