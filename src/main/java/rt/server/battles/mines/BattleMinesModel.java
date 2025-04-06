package rt.server.battles.mines;

import rt.server.battles.BattleController;
import rt.server.battles.BattleModel;
import rt.server.math.Vector3;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;
import rt.server.services.resource.Resource;
import rt.server.utils.JSON;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.ArrayList;

public class BattleMinesModel {
	
	private HashMap<BattleController, ArrayList<ServerMine>> mines;
	private BattleModel battle;
    private int _incrationId;
    private int minDamage;
    private int maxDamage;
    private int activationTime;
    private JSONObject minesObject;
	
    public BattleMinesModel(BattleModel model) {
    	this.battle = model;
    	this.mines = new HashMap<BattleController, ArrayList<ServerMine>>();
    	try {
			this.minesObject = (JSONObject)new JSONParser().parse(Resource.fileToString("battles/mines.json"));
			this.minDamage = (int)(long)this.minesObject.get("minDamage");
			this.maxDamage = (int)(long)this.minesObject.get("maxDamage");
			this.activationTime = (int)(long)this.minesObject.get("activationTimeMsec");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void init(BattleController ctrl) {
    	new Command(Commands.InitMineModel, this.minesObject).send(ctrl.client);
    	new Command(Commands.InitMines, JSON.parseInitMinesComand(this.mines)).send(ctrl.client);
    }
    
    public void tryPutMine(BattleController player, Vector3 pos) {
        ServerMine mine = new ServerMine();
        mine.setId(player.tank.id + "_" + this._incrationId);
        mine.setOwner(player);
        mine.setPosition(pos);
        ArrayList<ServerMine> userMines = this.mines.get(player);
        if (userMines == null) {
            userMines = new ArrayList(List.of((Object)mine));
            this.mines.put(player, userMines);
        } else {
            userMines.add(mine);
        }
        new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				activateMine(mine);
			} 
        }, this.activationTime);
        this.putMine(mine);
        ++this._incrationId;
    }

    public void playerDied(BattleController player) {
        ArrayList<ServerMine> _mines = this.mines.get(player);
        if (_mines != null) {
            _mines.clear();
            this.battle.send2Battle(new Command(Commands.RemoveMins, player.tank.id));
        }
    }
    
    private void putMine(ServerMine mine) {
        this.battle.send2Battle(new Command(Commands.PutMine, JSON.parsePutMineComand(mine)));
    }

    private void activateMine(ServerMine mine) {
        this.battle.send2Battle(new Command(Commands.ActivateMine, mine.getId()));
    }

    public void hitMine(BattleController whoHiter, String mineId) {
        BattleController mineOwner = null;
        block0: for (ArrayList<ServerMine> serverMines : this.mines.values()) {
            for (int i = 0; i < serverMines.size(); ++i) {
                ServerMine _mine = serverMines.get(i);
                if (!_mine.getId().equals(mineId)) continue;
                mineOwner = _mine.getOwner();
                serverMines.remove(i);
                continue block0;
            }
        }
        this.battle.send2Battle(new Command(Commands.HitMine, mineId, whoHiter.tank.id));
        if (mineOwner != null) {
            this.battle.damageController.damageTank(whoHiter, mineOwner, /*RandomUtils.getRandom(minDamage, maxDamage)*/250f, false, "mine");
        }
    }
}
