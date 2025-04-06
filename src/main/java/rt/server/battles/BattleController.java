package rt.server.battles;

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
    	List<JSONObject> skyboxes = new ArrayList<JSONObject>();
    	this.initDatas();
    	skyboxes.add(ResourceUtils.parseResourceToDependency(false, false, (int)(long)this.battle.getBattleEntity().skybox.get("top"), 10, 1));
    	skyboxes.add(ResourceUtils.parseResourceToDependency(false, false, (int)(long)this.battle.getBattleEntity().skybox.get("left"), 10, 1));
    	skyboxes.add(ResourceUtils.parseResourceToDependency(false, false, (int)(long)this.battle.getBattleEntity().skybox.get("bottom"), 10, 1));
    	skyboxes.add(ResourceUtils.parseResourceToDependency(false, false, (int)(long)this.battle.getBattleEntity().skybox.get("back"), 10, 1));
    	skyboxes.add(ResourceUtils.parseResourceToDependency(false, false, (int)(long)this.battle.getBattleEntity().skybox.get("right"), 10, 1));
    	skyboxes.add(ResourceUtils.parseResourceToDependency(false, false, (int)(long)this.battle.getBattleEntity().skybox.get("front"), 10, 1));
    	this.client.dependencyUtils.loadDependencyFromString("[{\"lazy\":false,\"alpha\":false,\"id\":95549,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":95559,\"type\":10,\"version\":9},{\"lazy\":false,\"alpha\":false,\"id\":95549,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":95559,\"type\":10,\"version\":9},{\"lazy\":false,\"alpha\":false,\"id\":95549,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":926352,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":388817,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":857360,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":553309,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"multiframe\":{\"numFrames\":9,\"fps\":25,\"width\":512,\"height\":476},\"id\":122455,\"type\":11,\"version\":1},{\"lazy\":false,\"id\":73466,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":63282,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":20098,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":2863,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":88303,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":32617,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":97468,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":82299,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":53912,\"type\":4,\"version\":4},{\"lazy\":false,\"alpha\":false,\"id\":96428,\"type\":10,\"version\":1},{\"lazy\":false,\"id\":38338,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":660652,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":603946,\"type\":4,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":215799,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":141042,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":586218,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":409645,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":916614,\"type\":10,\"version\":1},{\"lazy\":false,\"id\":181945,\"type\":4,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":855990,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":131023,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":965783,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":162026,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":721058,\"type\":10,\"version\":1},{\"lazy\":false,\"id\":508233,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":593235,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":343201,\"type\":4,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":583511,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":37183,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":605083,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":234984,\"type\":10,\"version\":1},{\"lazy\":false,\"id\":945273,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":945273,\"type\":4,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":201459,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":201449,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":558241,\"type\":10,\"version\":1},{\"lazy\":false,\"id\":458307,\"type\":4,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":458308,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":458309,\"type\":10,\"version\":1},{\"lazy\":false,\"id\":451710,\"type\":4,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":451711,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":451712,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":451713,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":451714,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":451715,\"type\":10,\"version\":1},{\"lazy\":false,\"id\":653372,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":653373,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":653374,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":653375,\"type\":4,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":514190,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":514192,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":514191,\"type\":10,\"version\":1},{\"lazy\":false,\"id\":230566,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":230565,\"type\":4,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":508549,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":508550,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":508551,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":508553,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":508552,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":214552,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":214553,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":214554,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":214555,\"type\":10,\"version\":1},{\"lazy\":false,\"id\":579360,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":579359,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":579361,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":962220,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":962221,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":962222,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":962223,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":962224,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":962225,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":962226,\"type\":4,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":791470,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":791471,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":791472,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":791473,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":791474,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":791475,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":791476,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":791477,\"type\":10,\"version\":1},{\"lazy\":false,\"id\":962340,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":962341,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":962342,\"type\":4,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":64863,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":826482,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":82431,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":82432,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":82433,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":82434,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"multiframe\":{\"numFrames\":16,\"fps\":16,\"width\":128,\"height\":128},\"id\":262362,\"type\":11,\"version\":1},{\"lazy\":false,\"alpha\":true,\"multiframe\":{\"numFrames\":7,\"fps\":25,\"width\":128,\"height\":128},\"id\":262363,\"type\":11,\"version\":1},{\"lazy\":false,\"alpha\":true,\"multiframe\":{\"numFrames\":7,\"fps\":25,\"width\":128,\"height\":128},\"id\":262364,\"type\":11,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":272805,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":257970,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":261418,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":654177,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":7758,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":217990,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":322272,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":737975,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":714496,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":914536,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":235520,\"type\":10,\"version\":1},{\"lazy\":false,\"id\":988711,\"type\":17,\"version\":1},{\"lazy\":false,\"id\":485066,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":587770,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":682388,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":515570,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":322199,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":882467,\"type\":4,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":977449,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":168750,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":772423,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":471863,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":749059,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":977635,\"type\":10,\"version\":1},{\"lazy\":false,\"id\":579403,\"type\":8,\"version\":1},{\"lazy\":false,\"id\":587424,\"type\":8,\"version\":1},{\"lazy\":false,\"id\":784193,\"type\":8,\"version\":1},{\"lazy\":false,\"id\":761998,\"type\":8,\"version\":1},{\"lazy\":false,\"id\":674975,\"type\":8,\"version\":1},{\"lazy\":false,\"id\":280765,\"type\":8,\"version\":1},{\"lazy\":false,\"id\":787349,\"type\":8,\"version\":1},{\"lazy\":false,\"id\":191098,\"type\":8,\"version\":1}]", () -> this.client.dependencyUtils.loadDependencyFromString("[{\"lazy\":false,\"alpha\":false,\"id\":95549,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":95549,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":59837,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":95549,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":926352,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":388817,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":857360,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":553309,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"multiframe\":{\"numFrames\":9,\"fps\":25,\"width\":512,\"height\":476},\"id\":122455,\"type\":11,\"version\":1},{\"lazy\":false,\"id\":73466,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":63282,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":20098,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":2863,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":88303,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":32617,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":97468,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":82299,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":53912,\"type\":4,\"version\":4},{\"lazy\":false,\"alpha\":false,\"id\":96428,\"type\":10,\"version\":10},{\"lazy\":false,\"id\":38338,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":660652,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":603946,\"type\":4,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":215799,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":141042,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":586218,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":409645,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":916614,\"type\":10,\"version\":1},{\"lazy\":false,\"id\":181945,\"type\":4,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":855990,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":131023,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":965783,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":162026,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":721058,\"type\":10,\"version\":1},{\"lazy\":false,\"id\":508233,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":593235,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":343201,\"type\":4,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":583511,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":37183,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":605083,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":234984,\"type\":10,\"version\":1},{\"lazy\":false,\"id\":945273,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":945273,\"type\":4,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":201459,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":201449,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":558241,\"type\":10,\"version\":1},{\"lazy\":false,\"id\":458307,\"type\":4,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":458308,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":458309,\"type\":10,\"version\":1},{\"lazy\":false,\"id\":451710,\"type\":4,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":451711,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":451712,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":451713,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":451714,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":451715,\"type\":10,\"version\":1},{\"lazy\":false,\"id\":653372,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":653373,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":653374,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":653375,\"type\":4,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":514190,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":514192,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":514191,\"type\":10,\"version\":1},{\"lazy\":false,\"id\":230566,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":230565,\"type\":4,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":508549,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":508550,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":508551,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":508553,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":508552,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":214552,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":214553,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":214554,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":214555,\"type\":10,\"version\":1},{\"lazy\":false,\"id\":579360,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":579359,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":579361,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":962220,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":962221,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":962222,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":962223,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":962224,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":962225,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":962226,\"type\":4,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":791470,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":791471,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":791472,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":791473,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":791474,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":791475,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":791476,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":791477,\"type\":10,\"version\":1},{\"lazy\":false,\"id\":962340,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":962341,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":962342,\"type\":4,\"version\":1},{\"lazy\":false,\"alpha\":false,\"id\":64863,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":826482,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":82431,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":82432,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":82433,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":82434,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"multiframe\":{\"numFrames\":16,\"fps\":16,\"width\":128,\"height\":128},\"id\":262362,\"type\":11,\"version\":1},{\"lazy\":false,\"alpha\":true,\"multiframe\":{\"numFrames\":7,\"fps\":25,\"width\":128,\"height\":128},\"id\":262363,\"type\":11,\"version\":1},{\"lazy\":false,\"alpha\":true,\"multiframe\":{\"numFrames\":7,\"fps\":25,\"width\":128,\"height\":128},\"id\":262364,\"type\":11,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":5680,\"type\":10,\"version\":1},{\"lazy\":false,\"alpha\":true,\"id\":722218,\"type\":10,\"version\":1},{\"lazy\":false,\"id\":337668,\"type\":17,\"version\":1},{\"lazy\":false,\"id\":403950,\"type\":17,\"version\":1},{\"lazy\":false,\"id\":158214,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":177070,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":289695,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":437905,\"type\":4,\"version\":1},{\"lazy\":false,\"id\":20888,\"type\":8,\"version\":1},{\"lazy\":false,\"id\":627579,\"type\":8,\"version\":1},{\"lazy\":false,\"id\":812273,\"type\":8,\"version\":1},{\"lazy\":false,\"id\":313706,\"type\":8,\"version\":1},{\"lazy\":false,\"id\":998304,\"type\":8,\"version\":1},{\"lazy\":false,\"id\":62968,\"type\":8,\"version\":1},{\"lazy\":false,\"id\":783489,\"type\":8,\"version\":1},{\"lazy\":false,\"id\":67882,\"type\":8,\"version\":1},{\"lazy\":false,\"id\":495668,\"type\":8,\"version\":1},{\"lazy\":false,\"id\":890438,\"type\":8,\"version\":1},{\"lazy\":false,\"id\":514476,\"type\":8,\"version\":1},{\"lazy\":false,\"id\":940135,\"type\":8,\"version\":1}]", () -> this.client.dependencyUtils.loadDependencyFromString(skyboxes.toString(), () -> {
            this.client.dependencyUtils.loadDependencyFromString("[{\"lazy\":false,\"id\":3814,\"type\":17,\"version\":1},{\"lazy\":false,\"id\":356834,\"type\":17,\"version\":1},{\"lazy\":false,\"id\":444997,\"type\":17,\"version\":1},{\"lazy\":false,\"id\":554722,\"type\":17,\"version\":1},{\"lazy\":false,\"id\":378527,\"type\":7,\"version\":3},{\"lazy\":false,\"id\":894795,\"type\":7,\"version\":1}]", () -> {
                this.client.dependencyUtils.loadDependencyFromString(MapResourceUtils.getResources(this.battle.map).toString(), () -> {
                    initBattleModel();
                });
            });
        })));
    }

    private void initDatas() {
    	new Command(Commands.InitShotsData, Resource.fileToString("battles/shots_data.json")).send(client);
    	new Command(Commands.InitBcshData, Resource.fileToString("battles/bcsh_data.json")).send(client);
    	new Command(Commands.InitSfxData, Resource.fileToString("battles/sfx_data.json")).send(client);
    }

	public void initBattleModel() {
		new Command(Commands.InitBattleModel, JSON.parseInitBattleModel(this.battle, false)).send(client);
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
