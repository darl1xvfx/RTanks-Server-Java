package rt.server.battles;

import org.json.simple.JSONObject;
import rt.server.battles.maps.parser.MapsLoader; // Импортируем MapsLoader
import rt.server.utils.GraphicUtils;

public class BattleEntity {

	public String battleId;
	public boolean autoBalance;
	public boolean friendlyFire;
	public boolean fullCash;
	public boolean goldBoxesEnabled;
	public int killsLimit;
	public int maxPeople;
	public int maxRank;
	public int minRank;
	public String name;
	public boolean paidBattle;
	public boolean reArmorEnabled;
	public int scoreBlue;
	public int scoreRed;
	public boolean spectator;
	public int timeCurrent;
	public int timeLimit;
	public String type;
	public boolean userAlreadyPaid;
	public boolean withoutBonuses;
	public boolean withoutInventory;
	public String mapid;
	public BattleModel battle;
	public JSONObject graphics;
	public JSONObject skybox;

	public BattleEntity(String battleId, boolean autoBalance, boolean friendlyFire, boolean fullCash,
						boolean goldBoxesEnabled, int killsLimit, int maxPeople, int maxRank, int minRank,
						String name, boolean paidBattle, boolean reArmorEnabled, int scoreBlue, int scoreRed,
						boolean spectator, int timeCurrent, int timeLimit, String type, boolean userAlreadyPaid,
						boolean withoutBonuses, boolean withoutInventory, String map_id) {
		this.battleId = battleId;
		this.autoBalance = autoBalance;
		this.friendlyFire = friendlyFire;
		this.fullCash = fullCash;
		this.goldBoxesEnabled = goldBoxesEnabled;
		this.killsLimit = killsLimit;
		this.maxPeople = maxPeople;
		this.maxRank = maxRank;
		this.minRank = minRank;
		this.name = MapsLoader.maps.containsKey(map_id) ? MapsLoader.maps.get(map_id).name : map_id;;
		this.paidBattle = paidBattle;
		this.reArmorEnabled = reArmorEnabled;
		this.scoreBlue = scoreBlue;
		this.scoreRed = scoreRed;
		this.spectator = spectator;
		this.timeCurrent = timeCurrent;
		this.timeLimit = timeLimit;
		this.type = type;
		this.userAlreadyPaid = userAlreadyPaid;
		this.withoutBonuses = withoutBonuses;
		this.withoutInventory = withoutInventory;
		this.mapid = map_id;
		this.graphics = GraphicUtils.getGraphicsByMap(map_id);
	}
}