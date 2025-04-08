package rt.server.battles;

import org.json.simple.JSONArray;
import rt.server.GameServer;
import rt.server.battles.ctf.flags.FlagServer;
import rt.server.battles.effects.InventoryModel;
import rt.server.battles.statistics.UserStatistics;
import rt.server.battles.tank.Tank;
import rt.server.battles.tank.TankHull;
import rt.server.battles.weapons.WeaponUtils;
import rt.server.client.ClientEntity;
import rt.server.logger.Logger;
import rt.server.math.Vector3;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;
import rt.server.services.resource.Resource;
import rt.server.utils.BattleDeleter;
import rt.server.utils.JSON;
import rt.server.utils.MapResourceUtils;
import rt.server.utils.ResourceUtils;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class BattleController {
	public BattleModel battle;
	public ClientEntity client;
	public Tank tank;
	public boolean userInited = false;
	public UserStatistics userStat;
	public InventoryModel inventoryModel;
	public String playerTeamType;
    public boolean suspicious = false;
    public ArrayList<String> voteUsers = new ArrayList<String>();
    public boolean changedEquipment;
	public FlagServer flag;

	private static final String DEPENDENCIES_FILE = "battles/battle_dependencies.json";

	public BattleController(BattleModel bm, ClientEntity c, String playerTT) {
		this.battle = bm;
		this.client = c;

		String hullName = client.user.equipment.getHullName();
		TankHull hull = TankHull.fromJson(hullName != null ? hullName : "default", client.user.equipment);

		this.tank = new Tank(
				this,
				WeaponUtils.getWeaponByName(client.user.equipment.getTurretName()),
				hull
		);

		this.tank.id = client.user.username;
		this.userStat = new UserStatistics(0, 0, 0);
		this.battle.users.put(client.user.username, this);
		this.inventoryModel = new InventoryModel(this);
		this.playerTeamType = playerTT;
		this.changedEquipment = false;
		this.battle.incration++;
		BattleDeleter.cancelRemoving(bm);
	}

	public void init() {
		Logger.log(Logger.INFO, "Starting battle initialization");
		try {
			this.initDatas();
			List<JSONObject> skyboxes = initializeSkyboxes();
			loadDependenciesFromJson(skyboxes);
		} catch (Exception e) {
			Logger.log(Logger.ERROR, "Battle initialization failed: " + e.getMessage());
			throw new RuntimeException("Failed to initialize battle", e);
		}
	}

	private List<JSONObject> initializeSkyboxes() {
		List<JSONObject> skyboxes = new ArrayList<>();
		String[] skyboxSides = {"top", "left", "bottom", "back", "right", "front"};

		Logger.log(Logger.INFO, "Initializing skyboxes");
		for (String side : skyboxSides) {
			try {
				int id = (int)(long)battle.getBattleEntity().skybox.get(side);
				JSONObject dependency = ResourceUtils.parseResourceToDependency(false, false, id, 10, 1);
				skyboxes.add(dependency);
				Logger.log(Logger.DEBUG, "Added skybox " + side + " with id: " + id);
			} catch (Exception e) {
				Logger.log(Logger.ERROR, "Failed to initialize skybox " + side + ": " + e.getMessage());
				throw new RuntimeException("Skybox initialization failed", e);
			}
		}
		return skyboxes;
	}

	private void loadDependenciesFromJson(List<JSONObject> skyboxes) {
		try {
			Logger.log(Logger.DEBUG, "Attempting to load dependencies from file: " + DEPENDENCIES_FILE);
			String jsonContent = Resource.fileToString(DEPENDENCIES_FILE);
			if (jsonContent == null || jsonContent.isEmpty()) {
				Logger.log(Logger.ERROR, "Dependencies file is empty or not found: " + DEPENDENCIES_FILE);
				throw new IllegalStateException("Dependencies file is empty or not found");
			}
			Logger.log(Logger.DEBUG, "Dependencies file content loaded successfully");

			JSONObject dependencies = (JSONObject) new JSONParser().parse(jsonContent);
			Logger.log(Logger.DEBUG, "Parsed JSON: " + dependencies.toJSONString());

			JSONArray mainDeps = (JSONArray) dependencies.get("main_dependencies");
			if (mainDeps == null || mainDeps.isEmpty()) {
				Logger.log(Logger.ERROR, "main_dependencies is null or empty");
				throw new IllegalStateException("main_dependencies not found or empty");
			}
			Logger.log(Logger.DEBUG, "Main dependencies: " + mainDeps.toJSONString());

			JSONArray additionalDeps = (JSONArray) dependencies.get("additional_dependencies");
			JSONArray finalDeps = (JSONArray) dependencies.get("final_dependencies");

			Logger.log(Logger.INFO, "Loading main dependencies");
			client.dependencyUtils.loadDependencyFromString(
					mainDeps.toJSONString(),
					() -> {
						Logger.log(Logger.INFO, "Main dependencies loaded successfully");
						loadAdditionalDependencies(additionalDeps, skyboxes);
					}
			);
			Logger.log(Logger.DEBUG, "loadDependencyFromString called for main dependencies");
		} catch (Exception e) {
			Logger.log(Logger.ERROR, "Failed to load dependencies from JSON: " + e.getMessage());
			throw new RuntimeException("Dependencies loading failed", e);
		}
	}

	private void loadAdditionalDependencies(JSONArray additionalDeps, List<JSONObject> skyboxes) {
		Logger.log(Logger.INFO, "Loading additional dependencies");
		try {
			client.dependencyUtils.loadDependencyFromString(
					additionalDeps.toJSONString(),
					() -> loadSkyboxDependencies(skyboxes)
			);
		} catch (Exception e) {
			Logger.log(Logger.ERROR, "Failed to load additional dependencies: " + e.getMessage());
			throw new RuntimeException("Additional dependencies loading failed", e);
		}
	}

	private void loadSkyboxDependencies(List<JSONObject> skyboxes) {
		Logger.log(Logger.INFO, "Loading skybox dependencies");
		try {
			client.dependencyUtils.loadDependencyFromString(
					JSONArray.toJSONString(skyboxes),
					this::loadFinalDependencies
			);
		} catch (Exception e) {
			Logger.log(Logger.ERROR, "Failed to load skybox dependencies: " + e.getMessage());
			throw new RuntimeException("Skybox dependencies loading failed", e);
		}
	}

	private void loadFinalDependencies() {
		try {
			String jsonContent = Resource.fileToString(DEPENDENCIES_FILE);
			JSONObject dependencies = (JSONObject) new JSONParser().parse(jsonContent);
			JSONArray finalDeps = (JSONArray) dependencies.get("final_dependencies");

			Logger.log(Logger.INFO, "Loading final dependencies");
			client.dependencyUtils.loadDependencyFromString(
					finalDeps.toJSONString(),
					this::loadMapResources
			);
		} catch (Exception e) {
			Logger.log(Logger.ERROR, "Failed to load final dependencies: " + e.getMessage());
			throw new RuntimeException("Final dependencies loading failed", e);
		}
	}

	private void loadMapResources() {
		Logger.log(Logger.INFO, "Loading map resources");
		try {
			client.dependencyUtils.loadDependencyFromString(
					MapResourceUtils.getResources(battle.map).toString(),
					this::initBattleModel
			);
		} catch (Exception e) {
			Logger.log(Logger.ERROR, "Failed to load map resources: " + e.getMessage());
			throw new RuntimeException("Map resources loading failed", e);
		}
	}

	private void initDatas() {
		Logger.log(Logger.INFO, "Initializing battle data");
		new Command(Commands.InitShotsData, Resource.fileToString("battles/shots_data.json")).send(client);
		new Command(Commands.InitBcshData, Resource.fileToString("battles/bcsh_data.json")).send(client);
		new Command(Commands.InitSfxData, Resource.fileToString("battles/sfx_data.json")).send(client);
	}

	public void initBattleModel() {
		Logger.log(Logger.INFO, "Initializing battle model");
		new Command(Commands.InitBattleModel, JSON.parseInitBattleModel(battle, false)).send(client);
		Logger.log(Logger.INFO, "Battle model initialized successfully");
	}

	public void createTank() {
		this.battle.initTank(this);
	}

	public void updateStat() {
		this.battle.send2Battle(new Command(Commands.UpdatePlayerStatistic, JSON.parseUserStat(this)));
	}

	public void onDestroy(boolean cache) {
		this.battle.removeUser(this, cache);
		this.battle = null;
		this.tank = null;
		this.client = null;
	}

	public void updateSuspicious(String user) {
	    if (voteUsers.contains(user)) {
	        return;
	    }

	    voteUsers.add(user);
	    int threshold = (int) (this.battle.users.size() * 0.25);
	    this.suspicious = (voteUsers.size() >= threshold);
	    if (this.suspicious) {
	        this.battle.send2Battle(new Command(Commands.ChangeSuspicious, client.user.username, this.suspicious));
	        GameServer.send2Lobbys(new Command(Commands.UpdateSuspiciousBattle, this.battle.getBattleEntity().battleId, Boolean.toString(this.suspicious)));
	    }
	}

    public void parseAndDropFlag(String json) {
        try {
            JSONObject _json = (JSONObject)new JSONParser().parse(json);
            this.battle.ctfModel.dropFlag(this, new Vector3((float)((Double)_json.get("x")).doubleValue(), (float)((Double)_json.get("y")).doubleValue(), (float)((Double)_json.get("z")).doubleValue()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public void onDisconnect() {
        this.onDestroy(true);
	}

	public void destroy() {
		// TODO Auto-generated method stub

	}

	public String getName() {
		return BattleController.class.getName();
	}

	public void changeEquipment() {
		if (this.changedEquipment) {
			try {
				this.tank.weapon = WeaponUtils.getWeaponByName(this.client.user.equipment.getTurretName());
				this.tank.hull = TankHull.fromJson(this.client.user.equipment.hullId.split("_")[0], this.client.user.equipment);
				this.battle.send2Battle(new Command(Commands.ResetConfigruation, JSON.parseInitTank(this, true, this.battle.incration, false)));
				this.changedEquipment = false;
			} catch (Exception e) {
				Logger.log(Logger.ERROR, "Не удалось сменить оборудование для " +
						this.client.user.username + ": " + e.getMessage());
				throw e;
			}
		}
	}
}
