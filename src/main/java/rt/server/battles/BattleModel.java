package rt.server.battles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;
import rt.server.battles.bonus.BonusRegion;
import rt.server.battles.bonus.SuperGoldModel;
import rt.server.services.FastHashMap;
import rt.server.GameServer;
import rt.server.ServerProperties;
import rt.server.battles.bonus.BattleBonus;
import rt.server.battles.bonus.BonusModel;
import rt.server.battles.bonus.regions.BonusRegionsModel;
import rt.server.battles.bonus.taken.BonusTakeModel;
import rt.server.battles.chat.BattleChatModel;
import rt.server.battles.cp.ControlPointsModel;
import rt.server.battles.ctf.CTFModel;
import rt.server.battles.ctf.flags.FlagState;
import rt.server.battles.effects.EffectsVisualizationModel;
import rt.server.battles.maps.parser.Map;
import rt.server.battles.maps.parser.MapsLoader;
import rt.server.battles.mines.BattleMinesModel;
import rt.server.battles.spawn.SpawnManager;
import rt.server.battles.spawn.TankSpawnService;
import rt.server.localization.LocalizedString;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;
import rt.server.services.quartz.TimeType;
import rt.server.services.quartz.impl.QuartzServiceImpl;
import rt.server.utils.BattleDeleter;
import rt.server.utils.JSON;
import rt.server.utils.SkyboxUtils;

public class BattleModel {

	public HashMap<String, BattleBonus> bonuses = new HashMap<String, BattleBonus>();
	public FastHashMap<String, BattleController> users;
	public HashMap<String, BonusRegion> usedGoldRegions = new HashMap<String, BonusRegion>();
	public int unusedGoldBoxes = 0;
	private long endBattleTime = 0L;
	public boolean battleFinish = false;
	private BattleEntity battleEntity;
	public BonusRegionsModel bonusRegionsModel;
	public BonusModel bonusModel;
	public BonusTakeModel bonusTakeModel;
	public BattleFundModel battleFundModel;
	public BattleChatModel battleChatModel;
	public BattleMinesModel battleMinesModel;
	public DamageController damageController;
	public EffectsVisualizationModel effectsVisualization;
	public CTFModel ctfModel;
	public ControlPointsModel domModel;
	public int incration;
	public Map map;

	public BattleModel(BattleEntity battleEntity) {
		battleEntity.battle = this;
		this.battleEntity = battleEntity;
		this.incration = 0;
		this.map = MapsLoader.maps.get(battleEntity.mapid);
		this.bonusRegionsModel = new BonusRegionsModel(this, this.map);
		this.bonusModel = new BonusModel(this, this.map);
		this.bonusTakeModel = new BonusTakeModel(this);
		this.battleFundModel = new BattleFundModel(this);
		this.battleChatModel = new BattleChatModel(this);
		this.battleMinesModel = new BattleMinesModel(this);
		this.damageController = new DamageController(this);
		this.effectsVisualization = new EffectsVisualizationModel(this);
		if (ServerProperties.SUPERGOLDS_ENABLED) {
			SuperGoldModel.init();
		}
		if (!this.battleEntity.withoutBonuses) {
		    this.bonusModel.start();
		}
		if (this.battleEntity.type.equals("CTF")) {
            this.ctfModel = new CTFModel(this);
        }
        if (this.battleEntity.type.equals("CP")) {
            this.domModel = new ControlPointsModel(this);
        }
		if (this.battleEntity.timeLimit > 0) {
			this.startTimeBattle();
		}
		this.users = new FastHashMap<>();
		this.battleEntity.skybox = SkyboxUtils.getSkyboxByMap(this.map);
	}

    private void startTimeBattle() {
        this.endBattleTime = System.currentTimeMillis() + (long)(this.battleEntity.timeCurrent * 1000);
        GameServer.send2Lobbys(new Command(Commands.StartTimeBattle, this.battleEntity.battleId));
        QuartzServiceImpl.inject().addJob("TimeBattle " + this.hashCode() + " battle=" + this.battleEntity.battleId, this.getName(), e -> this.finishBattle(), TimeType.SEC, this.battleEntity.timeCurrent);
    }

	private void restartBattle(boolean b) {
        this.battleFinish = false;
        this.bonuses.clear();
        this.battleFundModel.clearFund();
        this.usedGoldRegions.clear();
        this.unusedGoldBoxes = 0;
        for (BattleController player : this.users.values()) {
            if (player == null) continue;
            player.userStat.deaths = 0;
            player.userStat.kills = 0;
            player.userStat.score = 0;
            player.updateStat();
            player.tank.activeEffects.clear();
            this.respawnTank(player);
        }
        long currentTimeMillis = System.currentTimeMillis();
        int prepareTimeLeft = (int)((currentTimeMillis + (long)(this.battleEntity.timeCurrent * 1000) - currentTimeMillis) / 1000L);
        this.send2Battle(new Command(Commands.RestartBattle, String.valueOf(prepareTimeLeft)));
        if (this.battleEntity.timeLimit > 0) {
            this.startTimeBattle();
        }
	}

	public BattleEntity getBattleEntity() {
		return this.battleEntity;
	}

	public void initTank(BattleController battleController) {
		new Command(Commands.InitGUIModel, JSON.parseBattleGUIObject(this)).send(battleController.client);
		battleController.tank.position = SpawnManager.getSpawnState(this.map, this.battleEntity.type);
		battleController.userInited = true;
        if (this.battleEntity.type.equals("CTF")) {
            new Command(Commands.InitCTFModel, JSON.parseCTFModelData(this)).send(battleController.client);
        }
        if (this.battleEntity.type.equals("CP")) {
            this.domModel.sendInitData(battleController);
        }
		this.initBonuses(battleController);
		if (!this.battleEntity.withoutBonuses) {
		    new Command(Commands.InitBonusRegions, JSON.parseInitBonusRegionsData(this.bonusRegionsModel.regions)).send(battleController.client);
		}
		this.battleMinesModel.init(battleController);
		this.initAndSpawnAnotherTanks(battleController);
		battleController.inventoryModel.init();
		this.send2Battle(new Command(Commands.InitTank, JSON.parseInitTank(battleController, true, this.incration, true)));
		this.send2Battle(new Command(Commands.UpdateUserInfo, JSON.parseUserInfo(battleController.client.user)));
		battleController.updateStat();
		battleController.client.layoutService.endLayoutSwitch(battleController.client.currentLayout, "battle");
		new Command(Commands.InitDelayMount, JSON.parseInitDelayMount(battleController.client.delayMountService)).send(battleController.client);
		this.effectsVisualization.sendInitData(battleController);
		TankSpawnService.spawn(battleController, false);
	}

	public void finishBattle() {
		if (this.battleEntity.timeLimit > 0) {
			QuartzServiceImpl.inject().deleteJob("TimeBattle " + this.hashCode() + " battle=" + this.battleEntity.battleId, BattleModel.class.getName());
			this.battleFinish = true;
	        if (this.battleEntity.type.equals("CTF")) {
	            if (this.ctfModel.getBlueFlag().state == FlagState.TAKEN_BY && this.ctfModel.getBlueFlag().owner != null) {
	                this.ctfModel.getBlueFlag().owner.flag = null;
	                this.ctfModel.getBlueFlag().owner = null;
	            }
	            if (this.ctfModel.getRedFlag().state == FlagState.TAKEN_BY && this.ctfModel.getRedFlag().owner != null) {
	                this.ctfModel.getRedFlag().owner.flag = null;
	                this.ctfModel.getRedFlag().owner = null;
	            }
	        }
	        if (this.battleEntity.type.equals("CP")) {
	        	this.domModel.restartBattle();
	        }
			this.send2Battle(new Command(Commands.FinishBattle, JSON.parseFishishBattle(this.users, 10000)));
			this.bonusRegionsModel.removeAllGoldRegionsByBonuses();
			GameServer.send2Lobbys(new Command(Commands.StopTimeBatlte, this.battleEntity.battleId));
            QuartzServiceImpl.inject().addJob("FinishBattleBattle " + this.hashCode() + " battle=" + this.battleEntity.battleId, BattleModel.class.getName(), e -> {
                this.restartBattle(false);
            }, TimeType.MS, 10000L);
		}
	}

    private void initBonuses(BattleController user) {
    	ArrayList<JSONObject> bonusesObjects = new ArrayList<JSONObject>();
        for (BattleBonus bonus : bonuses.values()) {
        	bonusesObjects.add(JSON.parseSpawnBonusData(bonus));
        }
    	new Command(Commands.InitBonuses, bonusesObjects).send(user.client);
	}

	private void initAndSpawnAnotherTanks(BattleController battleController) {
		for (BattleController control : users.values()) {
			if (control != battleController && control.userInited) {
				new Command(Commands.InitTank, JSON.parseInitTank(control, false, this.incration, false)).send(battleController.client);
				control.updateStat();
				if (control.tank.state.equals("newcome")) {
				    new Command(Commands.Spawn, JSON.parseSpawnCommand(control)).send(battleController.client);
				}
			    if (control.tank.state.equals("active")) {
			    	new Command(Commands.Spawn, JSON.parseSpawnCommand(control)).send(battleController.client);
			        new Command(Commands.ActivateTank, control.client.user.username).send(battleController.client);
			    }
			}
		}
	}

	public void killTank(BattleController owner, BattleController killer, String turretName) {
		owner.tank.health = 0;
	    owner.userStat.deaths++;
	    owner.updateStat();
		new Command(Commands.LocalUserKilled).send(owner.client);
		owner.tank.state = "suicide";
		owner.tank.activeEffects.clear();
		this.battleMinesModel.playerDied(owner);
        if (this.ctfModel != null && owner.flag != null) {
            this.ctfModel.dropFlag(owner, owner.tank.position);
        }
        owner.tank.position = SpawnManager.getSpawnState(this.map, this.battleEntity.type);
		if (killer == null) {
			this.send2Battle(new Command(Commands.KillTank, owner.client.user.username, "suicide"));
		} else {
			if (killer.tank.id == owner.tank.id) {
				this.send2Battle(new Command(Commands.KillTank, owner.client.user.username, "suicide"));
			} else {
			    this.send2Battle(new Command(Commands.KillTank, owner.client.user.username, "killed", killer.client.user.username, turretName));
			    this.battleFundModel.addFund(100);
			    killer.client.addScore(200);
			    killer.userStat.kills++;
			    killer.updateStat();
			}
		}
		this.respawnTank(owner);
	}

	public void respawnTank(BattleController battleController) {
		if (this.battleFinish) {
			return;
		}
		TankSpawnService.spawn(battleController, true);
	}

	public void superKillTank(BattleController user) {
		for (BattleController userr : this.users.values()) {
			if (userr.tank.state.equals("suicide")) {
				return;
			}
			if (userr.tank.state.equals("newcome")) {
				return;
			}
	        if (!((user.tank.position.distanceTo(userr.tank.position)) > 1000)) {
	        	this.killTank(userr, user, user.client.user.equipment.getTurretName());
	        }
		}
	}

	public void userLog(String username, String action) {
		this.send2Battle(new Command(Commands.UserLog, username, action));
	}

	public void userLogLocalized(String username, LocalizedString action) {
		if (users.size() != 0) {
			for (BattleController control : users.values()) {
				if (control.userInited) {
					new Command(Commands.UserLog, username, action.get(control.client.locale)).send(control.client);
				}
			}
		}
	}

	public List<BattleController> getUsersByTeam(String tm) {
		List<BattleController> ctrls = new ArrayList<>();
		for (BattleController ctrl : users.values()) {
			if (ctrl.playerTeamType.equals(tm)) {
				ctrls.add(ctrl);
			}
		}
		return ctrls;
	}

	public void send2Battle(Command command) {
		if (users.size() != 0) {
			for (BattleController control : users.values()) {
				if (control.userInited) {
					command.send(control.client);
				}
			}
		}
	}

	public void send2Battle(BattleController player, Command command) {
		if (users.size() != 0) {
			for (BattleController control : users.values()) {
				if (control != player) {
					if (control.userInited) {
						command.send(control.client);
					}
				}
			}
		}
	}

    public int getTimeLeft() {
        return (int)((this.endBattleTime - System.currentTimeMillis()) / 1000L);
    }

    public void moveTank(BattleController controller) {
        this.send2Battle(new Command(Commands.Move, JSON.parseMoveCommand(controller)));
    }

	public void onFire(BattleController owner, String data) {
		this.send2Battle(new Command(Commands.Fire, owner.client.user.username, data));
		owner.tank.weapon.onFire(owner, data);
	}

	public void battleMessage(String action) {
		this.send2Battle(new Command(Commands.BattleMessage, action));
	}

	public void removeUser(BattleController controller, boolean cache) {
        this.battleMinesModel.playerDied(controller);
        this.users.remove(controller.client.user.username, controller);
        this.send2Battle(new Command(Commands.RemoveUser, controller.tank.id));
        if (controller.suspicious) {
        	GameServer.send2Lobbys(new Command(Commands.UpdateSuspiciousBattle, this.battleEntity.battleId, Boolean.toString(this.isSuspicious())));
        }
        if (users.size() <= 0) {
        	BattleDeleter.addBattleForRemove(this);
        }
	}

	public boolean isSuspicious() {
		for (BattleController player : users.values()) {
			if (player.suspicious) {
				return true;
			}
		}
		return false;
	}

    public void sendToBlueTeamPlayers(Command command) {
        if (this.users == null) {
            return;
            }
        if (this.users.size() != 0) {
            for (BattleController player : this.users.values()) {
                if (!player.playerTeamType.equals("BLUE") || !player.userInited)
                    continue;  command.send(player.client);
                }
            }
        }

    public void sendToRedTeamPlayers(Command command) {
        if (this.users == null) {
            return;
            }
        if (this.users.size() != 0) {
            for (BattleController player : this.users.values()) {
                if (!player.playerTeamType.equals("RED") || !player.userInited)
                    continue;  command.send(player.client);
                }
            }
        }

	public String getName() {
		return BattleModel.class.getName();
	}
}
