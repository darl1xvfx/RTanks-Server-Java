package rt.server.battles.mines;

import rt.server.battles.BattleController;
import rt.server.math.Vector3;

public class ServerMine {
    private String id;
    private Vector3 position;
    private BattleController owner;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Vector3 getPosition() {
        return this.position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public BattleController getOwner() {
        return this.owner;
    }

    public void setOwner(BattleController owner) {
        this.owner = owner;
    }
}

