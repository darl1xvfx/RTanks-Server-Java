package rt.server.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import rt.server.garage.mountable.HullItemsDataParser;
import rt.server.logger.Logger;
import rt.server.platform.Model;
import rt.server.services.FastHashMap;
import rt.server.services.OnlineService;
import rt.server.ServerProperties;
import rt.server.battles.BattleController;
import rt.server.battles.BattleEntity;
import rt.server.battles.BattleModel;
import rt.server.battles.bonus.BattleBonus;
import rt.server.battles.bonus.BonusModel;
import rt.server.battles.bonus.regions.BonusRegionData;
import rt.server.battles.ctf.CTFModel;
import rt.server.battles.ctf.flags.FlagServer;
import rt.server.battles.effects.InventoryItem;
import rt.server.battles.maps.parser.Map;
import rt.server.battles.maps.parser.MapsLoader;
import rt.server.battles.mines.ServerMine;
import rt.server.battles.tank.Tank;
import rt.server.challenges.QuestEntity;
import rt.server.challenges.QuestsParser;
import rt.server.challenges.TierData;
import rt.server.challenges.TiersParser;
import rt.server.challenges.items.TierItem;
import rt.server.client.ClientEntity;
import rt.server.friends.Friend;
import rt.server.friends.IncomingFriend;
import rt.server.friends.OutgoingFriend;
import rt.server.garage.DelayMountService;
import rt.server.garage.GarageItemParser;
import rt.server.garage.PropertyItem;
import rt.server.garage.lootbox.LootboxPrize;
import rt.server.garage.modification.ModificationInfo;
import rt.server.garage.mountable.MountableItemsDataParser;
import rt.server.garage.mountable.Resistance;
import rt.server.lobbychat.Channels;
import rt.server.localization.LocalizationLoader;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;
import rt.server.services.resource.Resource;
import rt.server.user.Quest;
import rt.server.user.User;

public class JSON {

	public static String parseShowSettingsData() {
		JSONObject obj = new JSONObject();
		obj.put("emailNotice", false);
		obj.put("isComfirmEmail", false);
		return obj.toJSONString();
	}

	public static JSONObject parseClanFlag(int resource, int id, String lang) {
		JSONObject obj = new JSONObject();
		obj.put("image", 95559);
		obj.put("id", 1);
		obj.put("lang", "ru");
		return obj;
	}

	public static String parseUserInfo(User user) {
		JSONObject obj = new JSONObject();
		obj.put("clan_data", null);
		obj.put("premium", user.premium > 0.000001);
		obj.put("nickname", user.username);
		obj.put("rank", user.rang);
		obj.put("online", OnlineService.inOnline(user.username));
		return obj.toJSONString();
	}

	public static String parseInitPanelData(User user) {
		JSONObject obj = new JSONObject();
		obj.put("name", user.username);
		obj.put("crystall", user.crystals);
		obj.put("email", null);
		obj.put("tester", false);
		obj.put("next_score", user.getNextScore());
		obj.put("place", 100000);
		obj.put("rang", user.rang);
		obj.put("rating", 100000);
		obj.put("score", user.score);
		return obj.toJSONString();
    }

	public static String parseInitMessages(ClientEntity client) {
		JSONObject obj = new JSONObject();
		org.json.JSONArray arr = new org.json.JSONArray();
		for (String key : Channels.channels.keySet()) {
			arr.put(key);
		}
        obj.put("typingSpeedAntifloodEnabled", true);
        obj.put("linksWhiteList", new ArrayList());
        obj.put("admin", false);
        obj.put("minChar", 60);
        obj.put("antifloodEnabled", true);
        obj.put("chatEnabled", true);
        obj.put("symbolCost", 176);
        obj.put("channels", arr);
        obj.put("enterCost", 880);
        obj.put("minWord", 5);
        obj.put("messages", Channels.channels.get("Общий RU"));
        obj.put("selfName", "");
        obj.put("chatModeratorLevel", client.user.permissions);
        obj.put("bufferSize", 60);
        return obj.toJSONString();
	}



	public static String parseBattleGUIObject(BattleModel battle) {
    	JSONObject obj = new JSONObject();
    	JSONArray users = new JSONArray();
    	obj.put("fund", battle.battleFundModel.fund);
    	obj.put("timeLimit", battle.getBattleEntity().timeLimit);
    	obj.put("name", battle.getBattleEntity().name);
    	obj.put("score_blue", battle.getBattleEntity().scoreBlue);
    	obj.put("score_red", battle.getBattleEntity().scoreRed);
    	obj.put("team", !battle.getBattleEntity().type.equals("DM"));
    	obj.put("currTime", battle.getTimeLeft());
    	obj.put("scoreLimit", battle.getBattleEntity().killsLimit);
    	for (BattleController user : battle.users.values()) {
            JSONObject usr = new JSONObject();
            usr.put("nickname", user.client.user.username);
            usr.put("rank", user.client.user.rang);
            usr.put("teamType", user.playerTeamType);
            users.add(usr);
    	}
    	obj.put("users", users);
    	return obj.toJSONString();
    }

	public static String parseGarageItemsData(List<JSONObject> items) {
		JSONObject obj = new JSONObject();
		obj.put("items", items);
		return obj.toJSONString();
	}
    public static String parseBattleMapList(boolean haveSub, List<BattleModel> battles) {
        JSONObject json = new JSONObject();
        JSONArray jarray = new JSONArray();
        JSONArray jbattles = new JSONArray();
        Iterator var3 = MapsLoader.maps.values().iterator();

        while(var3.hasNext()) {
            Map map = (Map)var3.next();
            JSONObject jmap = new JSONObject();
            jmap.put("id", map.id.replace(".xml", ""));
            jmap.put("name", map.name);
            jmap.put("gameName", "тип gameName");
            jmap.put("maxPeople", map.maxPlayers);
            jmap.put("maxRank", map.maxRank);
            jmap.put("minRank", map.minRank);
            jmap.put("themeName", map.themeId);
            jmap.put("skyboxId", map.skyboxId);
            jmap.put("previewId", map.previewId);
            jmap.put("ctf", map.ctf);
            jmap.put("tdm", map.tdm);
            jmap.put("dom", map.dom);
            jarray.add(jmap);
        }
	    JSONObject modesObj = new JSONObject();
	    try {
			modesObj.put("modes", parseJsonArray("modes.json", "modes", new JSONParser()));
		} catch (Exception e) {}
        json.put("items", jarray);
        var3 = battles.iterator();

        while(var3.hasNext()) {
        	BattleModel battle = (BattleModel)var3.next();
            jbattles.add(parseBattleInfo(battle));
        }
        json.put("battles", jbattles);
        json.put("haveSubscribe", haveSub);
        return json.toJSONString() + ";" + modesObj.toJSONString();
    }

	public static List<JSONObject> parseJsonArray(String filePath, String key, JSONParser parser) throws IOException, ParseException {
	    List<JSONObject> result = new ArrayList<>();
	    String jsonContent = Resource.fileToString(filePath);
	    JSONObject jsonObject = (JSONObject) parser.parse(jsonContent);
	    JSONArray jsonArray = (JSONArray) jsonObject.get(key);

	    for (Object obj : jsonArray) {
	        result.add((JSONObject) obj);
	    }

	    return result;
	}

    public static JSONObject parseMessage(int rangTo, boolean addressed, boolean system, String name, boolean yellow, String channel, int chatPermissions, String nameTo, int chatPermissionsTo, String message, int rang) {
    	JSONObject obj = new JSONObject();
    	obj.put("rangTo", rangTo);
    	obj.put("addressed", addressed);
    	obj.put("system", system);
    	obj.put("name", name);
    	obj.put("yellow", yellow);
    	obj.put("channel", channel);
    	obj.put("chatPermissions", chatPermissions);
    	obj.put("nameTo", nameTo);
    	obj.put("chatPermissionsTo", chatPermissionsTo);
    	obj.put("message", message);
    	obj.put("rang", rang);
    	return obj;
    }

    public static String parseBuyItemData(String itemId, boolean addable, int count) {
    	JSONObject obj = new JSONObject();
    	obj.put("itemId", itemId);
    	obj.put("addable", addable);
    	obj.put("count", count);
    	return obj.toJSONString();
    }

    public static String parseInitFriendsService(List<IncomingFriend> incoming, List<OutgoingFriend> outcoming, List<Friend> friends) {
    	JSONObject obj = new JSONObject();
    	JSONArray incomingg = new JSONArray();
    	JSONArray outcomingg = new JSONArray();
    	JSONArray friendss = new JSONArray();
    	for (Friend fr : friends) {
    		JSONObject fri = new JSONObject();
    		fri.put("id", fr.username);
    		friendss.add(fri);
    	}
    	for (OutgoingFriend fr : outcoming) {
    		JSONObject fri = new JSONObject();
    		fri.put("id", fr.username);
    		outcomingg.add(fri);
    	}
    	for (IncomingFriend fr : incoming) {
    		JSONObject fri = new JSONObject();
    		fri.put("id", fr.username);
    		incomingg.add(fri);
    	}
    	obj.put("new_accepted", new ArrayList());
    	obj.put("incoming", incomingg);
    	obj.put("new_incoming", new ArrayList());
    	obj.put("outcoming", outcomingg);
    	obj.put("friends", friendss);
    	return obj.toJSONString();
    }

    public static void parseShowQuests(ClientEntity client) {
    	JSONObject obj = new JSONObject();
    	obj.put("completeForToday", client.user.getQuests().size() < 3);
    	obj.put("weeklyLevel", 0);
    	obj.put("weeklyProgress", 0);
    	if (client.user.getQuests().size() > 0) {
    		JSONArray challenges = new JSONArray();
    		for (Quest quest : client.user.getQuests()) {
    			challenges.add(parseQuest(quest));
    		}
    		obj.put("challenges", challenges);
    	}
    	obj.put("timeToNextQuest", 0);
    	obj.put("changeCost", 100);
    	if (client.user.getQuests().size() > 0) {
            new Command(Commands.ShowQuests, obj.toJSONString()).send(client);
    	} else {
    		new Command(Commands.ShowEmptyQuests, obj.toJSONString()).send(client);
    	}
    }

    public static JSONObject parseSkipQuest(Quest ques) {
		JSONObject obj = new JSONObject();
		QuestEntity quest = QuestsParser.getQuestById(ques.questId);
		obj.put("image", quest.image);
		obj.put("level", quest.level);
		obj.put("prizes", quest.prizes);
		obj.put("target_progress", quest.targetProgress);
		obj.put("progress", ques.targetProgress);
		obj.put("description", quest.description);
		obj.put("id", quest.id);
		obj.put("changeCost", 100);
		return obj;
	}

    public static JSONObject parseQuest(Quest ques) {
		JSONObject obj = new JSONObject();
		QuestEntity quest = QuestsParser.getQuestById(ques.questId);
		obj.put("image", quest.image);
		obj.put("level", quest.level);
		obj.put("prizes", quest.prizes);
		obj.put("target_progress", quest.targetProgress);
		obj.put("progress", ques.targetProgress);
		obj.put("description", quest.description);
		obj.put("id", quest.id);
		return obj;
	}

	public static String parseInitTank(BattleController user, boolean stateNull, int inc, boolean notificationOfEnter) {
		JSONObject obj = new JSONObject();
		List<JSONObject> models = new ArrayList<>();
		JSONObject resistances = parseModel(1659531301, -819911951, "resistances");
		JSONArray resistancess = new JSONArray();
		for (Resistance resist : user.client.user.equipment.mountedResistances) {
			ModificationInfo resistModification = GarageItemParser.items.get(resist.getParts()[0]).modifications.get(Integer.valueOf(resist.getParts()[1]));
			for (PropertyItem propert : resistModification.propertys) {
				resistancess.add(parseProperty(propert));
			}
		}
		resistances.put("resistances", resistancess);
		models.add(resistances);

		if (user.tank.weapon.getModel() != null) {
			Model weaponModel = user.tank.weapon.getModel();
			JSONObject weaponJson = parseModel(weaponModel.high, weaponModel.low, weaponModel.name);
			if (weaponModel.name.equals("laser")) {
				weaponJson.put("showTime", 500);
				weaponJson.put("colorRed", "16711684");
				weaponJson.put("colorBlue", "30719");
				weaponJson.put("locallyVisible", true);
			}
			models.add(weaponJson);
		}

		obj.put("sideAcceleration", user.tank.hull.sideAcceleration);
		obj.put("turnMaxSpeed", user.tank.hull.turnSpeed);
		obj.put("battleId", user.battle.getBattleEntity().battleId);
		obj.put("mass", user.tank.hull.mass);
		obj.put("tank_id", user.client.user.username);
		obj.put("turret_id", user.client.user.equipment.turretId);
		obj.put("incration", inc);
		obj.put("turnAcceleration", user.tank.hull.turnAcceleration);
		obj.put("acceleration", user.tank.hull.acceleration);
		obj.put("impact_force", 6.0);
		obj.put("sounds", user.tank.getSoundsObject());
		obj.put("turnDeceleration", user.tank.hull.turnAcceleration);
		obj.put("nickname", user.client.user.username);
		obj.put("rank", user.client.user.rang);
		obj.put("state_null", stateNull);
		obj.put("team_type", user.playerTeamType);
		obj.put("state", user.tank.state);
		obj.put("turnReverseAcceleration", user.tank.hull.reverseTurnAcceleration);
		obj.put("colormap_id", user.client.user.equipment.colormapId);
		obj.put("hull_id", user.client.user.equipment.hullId);
		obj.put("kickback", 2.5);
		obj.put("models", models);
		obj.put("turret_turn_speed", 2.66);
		obj.put("health", user.tank.health);
		obj.put("reverseAcceleration", user.tank.hull.reverseAcceleration);
		obj.put("maxSpeed", user.tank.hull.speed);
		obj.put("damping", user.tank.hull.damping);
		obj.put("turret_resource", MountableItemsDataParser.get(user.client.user.equipment.turretId).object3ds);
		obj.put("hull_resource", HullItemsDataParser.get(user.client.user.equipment.hullId).object3ds);
		obj.put("deceleration", user.tank.hull.acceleration);
		obj.put("colormap_resource", MountableItemsDataParser.get(user.client.user.equipment.colormapId).object3ds);
		obj.put("sfxData", user.tank.weapon.getSFX(0).toJSONString());
		obj.put("position", user.tank.concatPosition() + "@" + user.tank.position.rot);
		obj.put("turret_rotation_accel", 2.66);
		obj.put("notificationOfEnter", Boolean.toString(notificationOfEnter));
		return obj.toJSONString();
	}

	public static String parseSpawnCommand(BattleController bpc) {
		JSONObject obj = new JSONObject();
		obj.put("tank_id", bpc.client.user.username);
		obj.put("health", bpc.tank.health);
		obj.put("speed", bpc.tank.hull.speed);
		obj.put("turn_speed", bpc.tank.hull.turnSpeed);
		obj.put("turret_rotation_speed", 2.62);
		obj.put("incration_id", 1);
		obj.put("team_type", bpc.playerTeamType);
		obj.put("x", bpc.tank.position.x);
		obj.put("y", bpc.tank.position.y);
		obj.put("z", bpc.tank.position.z);
		obj.put("rot", bpc.tank.position.rot);
		return obj.toString();
	}

	public static String parseInitBonusRegionsData(List<BonusRegionData> regions) {
		JSONObject obj = new JSONObject();
		obj.put("zones", regions);
		return obj.toString();
	}

    public static JSONObject parseSpawnBonusData(BattleBonus bonus) {
    	JSONObject obj = new JSONObject();
    	obj.put("cords", 389617);
    	obj.put("parachute", 791554);
    	obj.put("x", bonus.pos.x);
    	obj.put("y", bonus.pos.y);
    	obj.put("z", bonus.pos.z);
    	obj.put("bonus_id", BonusModel.getBonusIdByName(bonus.id.split("_")[0]));
    	obj.put("id", bonus.id);
    	obj.put("disappearing_time", -3);
    	obj.put("parachute_inner", 304293);
    	obj.put("time_from_appearing", 0);
    	return obj;
    }

	public static String parseBattleChatMessage(BattleController owner, String message, boolean team) {
		JSONObject obj = new JSONObject();
		obj.put("message", message);
		obj.put("team_type", owner.playerTeamType);
		obj.put("team", team);
		obj.put("nickname", owner.client.user.username);
		obj.put("rank", owner.client.user.rang);
		obj.put("chat_level", owner.client.user.permissions);
		return obj.toJSONString();
	}

	public static JSONObject parseUserStat(BattleController controller) {
		JSONObject obj = new JSONObject();
		obj.put("team_type", controller.playerTeamType);
		obj.put("deaths", controller.userStat.deaths);
		obj.put("kills", controller.userStat.kills);
		obj.put("rank", controller.client.user.rang);
		obj.put("score", controller.userStat.score);
		obj.put("suspicious", (controller.suspicious));
		obj.put("id", controller.client.user.username);
		return obj;
	}

    public static String parseMoveCommand(BattleController player) {
        Tank tank = player.tank;
        JSONObject json = new JSONObject();
        JSONObject pos = new JSONObject();
        JSONObject orient = new JSONObject();
        JSONObject line = new JSONObject();
        JSONObject angle = new JSONObject();
        pos.put("x", Float.valueOf(tank.position.x));
        pos.put("y", Float.valueOf(tank.position.y));
        pos.put("z", Float.valueOf(tank.position.z));
        orient.put("x", Float.valueOf(tank.orientation.x));
        orient.put("y", Float.valueOf(tank.orientation.y));
        orient.put("z", Float.valueOf(tank.orientation.z));
        line.put("x", Float.valueOf(tank.linVel.x));
        line.put("y", Float.valueOf(tank.linVel.y));
        line.put("z", Float.valueOf(tank.linVel.z));
        angle.put("x", Float.valueOf(tank.angVel.x));
        angle.put("y", Float.valueOf(tank.angVel.y));
        angle.put("z", Float.valueOf(tank.angVel.z));
        json.put("position", pos);
        json.put("orient", orient);
        json.put("line", line);
        json.put("angle", angle);
        json.put("turretDir", tank.turretDir);
        json.put("ctrlBits", tank.controllBits);
        json.put("tank_id", tank.id);
        return json.toJSONString();
    }

    public static JSONObject parseInventoryItem(InventoryItem item) {
    	JSONObject obj = new JSONObject();
    	obj.put("itemEffectTime", item.itemEffectTime);
    	obj.put("count", item.count);
    	obj.put("slotId", item.slotId);
    	obj.put("id", item.id);
    	obj.put("itemRestSec", item.itemRestSec);
    	return obj;
    }

	public static String parseInventory(List<JSONObject> inventorys) {
		JSONObject obj = new JSONObject();
		obj.put("items", inventorys);
		return obj.toJSONString();
	}

    public static String parseInitMinesComand(HashMap<BattleController, ArrayList<ServerMine>> mines) {
        JSONObject jobj = new JSONObject();
        JSONArray array = new JSONArray();
        for (ArrayList<ServerMine> userMines : mines.values()) {
            for (ServerMine mine : userMines) {
                JSONObject _mine = new JSONObject();
                _mine.put("ownerId", mine.getOwner().tank.id);
                _mine.put("mineId", mine.getId());
                _mine.put("x", Float.valueOf(mine.getPosition().x));
                _mine.put("y", Float.valueOf(mine.getPosition().y));
                _mine.put("z", Float.valueOf(mine.getPosition().z));
                array.add(_mine);
            }
        }
        jobj.put("mines", array);
        return jobj.toJSONString();
    }

    public static String parsePutMineComand(ServerMine mine) {
        JSONObject jobj = new JSONObject();
        jobj.put("mineId", mine.getId());
        jobj.put("userId", mine.getOwner().tank.id);
        jobj.put("x", Float.valueOf(mine.getPosition().x));
        jobj.put("y", Float.valueOf(mine.getPosition().y));
        jobj.put("z", Float.valueOf(mine.getPosition().z));
        return jobj.toJSONString();
    }

    public static String parseInitBattleModel(BattleModel battle, boolean spectator) {
    	JSONObject obj = new JSONObject();
    	obj.put("map_resource_id", battle.map.mapResource);
    	obj.put("kick_period_ms", 300000);
    	obj.put("graphics_data", battle.getBattleEntity().graphics.toJSONString());
    	obj.put("billboard", ServerProperties.BILLBOARD_IMAGE);
    	obj.put("ambientSound", 38338);
    	obj.put("battleFinishSound", 97468);
    	obj.put("reArmorEnabled", battle.getBattleEntity().reArmorEnabled);
    	obj.put("skybox", battle.getBattleEntity().skybox);
    	obj.put("map_id", battle.getBattleEntity().mapid);
    	obj.put("gravity", -1000);
    	obj.put("spectator", spectator);
    	obj.put("killSound", 82299);
    	obj.put("game_mode", "day");
    	obj.put("invisible_time", 3500);
    	obj.put("bonusTakenSound", 32617);
    	return obj.toJSONString();
    }

    public static String parseInitDelayMount(DelayMountService delayMount) {
    	JSONObject obj = new JSONObject();
    	obj.put("delayMountWeaponInSec", delayMount.delayMountWeaponInSec);
    	obj.put("delayMountResistanceInSec", delayMount.delayMountResistanceInSec);
    	obj.put("delayMountArmorInSec", delayMount.delayMountArmorInSec);
    	return obj.toJSONString();
    }

	public static String parseInitClan() {
		JSONObject obj = new JSONObject();
		obj.put("loadingInServiceSpace", true);
		obj.put("restrictionTimeJoinClanInSec", 60);
		obj.put("giveBonusesClan", false);
		obj.put("clan", false);
		obj.put("showBuyLicenseButton", false);
		obj.put("showOtherClan", true);
		obj.put("flags", Arrays.asList(parseClanFlag(0, 0, "")));
		obj.put("clanMember", false);
		return obj.toJSONString();
	}

    public static String parseFishishBattle(FastHashMap<String, BattleController> users2, int timeToRestart) {
        JSONObject obj = new JSONObject();
        JSONArray users = new JSONArray();
        obj.put("time_to_restart", timeToRestart);
        if (users2 == null) {
            return obj.toString();
        }
        for (BattleController bpc : users2.values()) {
            JSONObject stat = new JSONObject();
            stat.put("kills", bpc.userStat.kills);
            stat.put("deaths", bpc.userStat.deaths);
            stat.put("id", bpc.client.user.username);
            stat.put("rank", bpc.client.user.rang);
            stat.put("prize", 999);
            stat.put("team_type", bpc.playerTeamType);
            stat.put("score", bpc.userStat.score);
            users.add(stat);
        }
        obj.put("users", users);
        return obj.toString();
    }

    public static JSONObject parseShopItem(int count, String name) {
    	JSONObject obj = new JSONObject();
    	obj.put("count", count);
    	obj.put("name", name);
    	return obj;
    }

    public static JSONObject parseDonateData(List<JSONObject> data) {
    	JSONObject obj = new JSONObject();
    	obj.put("data", data);
    	return obj;
    }

	public static JSONObject parseUpdateCountUsersDM(BattleModel battle) {
    	JSONObject obj = new JSONObject();
    	obj.put("battleId", battle.getBattleEntity().battleId);
    	obj.put("countPeople", battle.users.size());
    	obj.put("friendsDm", 0);
    	return obj;
	}

	public static JSONObject parseAddPlayerToBattle(BattleController controller) {
    	JSONObject obj = new JSONObject();
    	obj.put("battleId", controller.battle.getBattleEntity().battleId);
    	obj.put("id", controller.client.user.username);
    	obj.put("kills", controller.userStat.kills);
    	obj.put("name", controller.client.user.username);
    	obj.put("rank", controller.client.user.rang);
    	obj.put("type", controller.playerTeamType);
    	return obj;
	}

	public static JSONObject parseUpdateCountUsersTeam(BattleModel battle) {
		List<BattleController> redPeoples = battle.getUsersByTeam("RED");
		List<BattleController> bluePeoples = battle.getUsersByTeam("BLUE");
    	JSONObject obj = new JSONObject();
    	obj.put("battleId", battle.getBattleEntity().battleId);
    	obj.put("redPeople", redPeoples.size());
    	obj.put("bluePeople", bluePeoples.size());
    	obj.put("friendsRed", 0);
    	obj.put("friendsBlue", 0);
    	return obj;
	}

	public static String parseCTFModelData(BattleModel battleModel) {
        JSONObject obj = new JSONObject();
        CTFModel ctfModel = battleModel.ctfModel;
        obj.put("bluePedestalModel", 403950);
        obj.put("winSound", 437905);
        obj.put("flagTakeSound", 289695);
        obj.put("blueFlagSprite", 722218);
        obj.put("redPedestalModel", 337668);
        obj.put("redFlagSprite", 5680);
        obj.put("flagDropSound", 158214);
        obj.put("flagReturnSound", 177070);
        JSONObject light = new JSONObject();
        light.put("blueColor", 26367);
        light.put("redColor", 16711680);
        light.put("blueIntensity", 1);
        light.put("attenuationBegin", 100);
        light.put("redIntensity", 1);
        light.put("attenuationEnd", 1000);
        obj.put("lighting", light);
        JSONObject basePosBlue = new JSONObject();
        basePosBlue.put("x", Float.valueOf(battleModel.map.flagBluePosition.x));
        basePosBlue.put("y", Float.valueOf(battleModel.map.flagBluePosition.y));
        basePosBlue.put("z", Float.valueOf(battleModel.map.flagBluePosition.z));
        JSONObject basePosRed = new JSONObject();
        basePosRed.put("x", Float.valueOf(battleModel.map.flagRedPosition.x));
        basePosRed.put("y", Float.valueOf(battleModel.map.flagRedPosition.y));
        basePosRed.put("z", Float.valueOf(battleModel.map.flagRedPosition.z));
        JSONObject posBlue = new JSONObject();
        posBlue.put("x", Float.valueOf(ctfModel.getBlueFlag().position.x));
        posBlue.put("y", Float.valueOf(ctfModel.getBlueFlag().position.y));
        posBlue.put("z", Float.valueOf(ctfModel.getBlueFlag().position.z));
        JSONObject posRed = new JSONObject();
        posRed.put("x", Float.valueOf(ctfModel.getRedFlag().position.x));
        posRed.put("y", Float.valueOf(ctfModel.getRedFlag().position.y));
        posRed.put("z", Float.valueOf(ctfModel.getRedFlag().position.z));
        obj.put("basePosBlueFlag", basePosBlue);
        obj.put("basePosRedFlag", basePosRed);
        obj.put("posBlueFlag", posBlue);
        obj.put("posRedFlag", posRed);
        obj.put("blueFlagCarrierId", ctfModel.getBlueFlag().owner == null ? null : ctfModel.getBlueFlag().owner.tank.id);
        obj.put("redFlagCarrierId", ctfModel.getRedFlag().owner == null ? null : ctfModel.getRedFlag().owner.tank.id);
        return obj.toJSONString();
	}

    public static String parseDropFlagCommand(FlagServer flag) {
        JSONObject obj = new JSONObject();
        obj.put("x", Float.valueOf(flag.position.x));
        obj.put("y", Float.valueOf(flag.position.y));
        obj.put("z", Float.valueOf(flag.position.z));
        obj.put("flagTeam", flag.flagTeamType);
        return obj.toJSONString();
    }

    public static JSONObject parseFriendData(User user) {
        JSONObject friend = new JSONObject();
        friend.put("battleId", "");
        friend.put("rank", user.rang);
        friend.put("online", OnlineService.inOnline(user.username));
        friend.put("id", user.username);
        return friend;
    }

    public static JSONObject parseUpdateFriendsList(User owner, List<Friend> friends, List<OutgoingFriend> outcoming, List<IncomingFriend> incoming) {
    	JSONObject friendsss = new JSONObject();
    	JSONArray incomingg = new JSONArray();
    	JSONArray outcomingg = new JSONArray();
    	JSONArray friendss = new JSONArray();
        for (Friend friend : friends) {
        	if (friend.uid != owner.id) {
        		friends.remove(friend);
        	}
        }
        for (OutgoingFriend friend : outcoming) {
        	if (friend.uid != owner.id) {
        		friends.remove(friend);
        	}
        }
        for (IncomingFriend friend : incoming) {
        	if (friend.uid != owner.id) {
        		friends.remove(friend);
        	}
        }
    	for (Friend fr : friends) {
    		JSONObject fri = new JSONObject();
    		fri.put("id", fr.username);
    		friendss.add(fri);
    	}
    	for (OutgoingFriend fr : outcoming) {
    		JSONObject fri = new JSONObject();
    		fri.put("id", fr.username);
    		outcomingg.add(fri);
    	}
    	for (IncomingFriend fr : incoming) {
    		JSONObject fri = new JSONObject();
    		fri.put("id", fr.username);
    		incomingg.add(fri);
    	}
    	friendsss.put("friends", friendss);
    	friendsss.put("incoming", incomingg);
    	friendsss.put("outcoming", outcomingg);
    	return friendsss;
    }

    public static JSONObject parseLootboxPrize(String category, int count, int preview, String name) {
    	JSONObject obj = new JSONObject();
    	obj.put("category", category);
    	obj.put("count", count);
    	obj.put("image", preview);
    	obj.put("name", name);
    	return obj;
    }

	public static JSONObject parseSocialNetwork(String key, Boolean boolean1) {
    	JSONObject obj = new JSONObject();
    	obj.put("snId", key);
    	obj.put("enabled", boolean1);
    	return obj;
	}

	public static JSONObject parseModel(int high, int low, String name) {
		JSONObject models = new JSONObject();
	    models.put("high", high);
		models.put("low", low);
		models.put("name", name);
		return models;
	}

	public static JSONObject parseTiers(ClientEntity c) {
		JSONObject models = new JSONObject();
		List<JSONObject> tiers = new ArrayList<>();
		for (TierData tier : TiersParser.tiers.values()) {
			tiers.add(parseTierData(c.locale, tier));
		}
	    models.put("tiers", tiers);
		return models;
	}

	public static JSONObject parseTierData(String lang, TierData tier) {
		JSONObject models = new JSONObject();
	    models.put("freeItem", parseTierItem(lang, tier.freeItem));
		models.put("battlePassItem", parseTierItem(lang, tier.battlePassItem));
		models.put("stars", tier.stars);
		return models;
	}

	public static JSONObject parseTierItem(String lang, TierItem item) {
		JSONObject models = new JSONObject();
	    models.put("preview", item.preview);
		models.put("amount", item.amount);
		models.put("name", LocalizationLoader.getString(item.name).get(lang));
		models.put("received", item.received);
		return models;
	}

	public static JSONArray parseLootboxPrizes(List<LootboxPrize> randomReward) {
		JSONArray prizes = new JSONArray();
		for (LootboxPrize prize : randomReward) {
			prizes.add(parseLootboxPrize(prize.getCategory(), prize.getCount(), prize.getPreview(), prize.getName()));
		}
		return prizes;
	}

	public static JSONArray parseModifications(List<ModificationInfo> modifications) {
		JSONArray modificationss = new JSONArray();
		for (ModificationInfo modification : modifications) {
			JSONObject modificationn = new JSONObject();
            modificationn.put("properts", parseProperts(modification.propertys));
            modificationn.put("price", modification.price);
            modificationn.put("rank", modification.rank);
            modificationn.put("previewId", modification.previewId);
			modificationss.add(modificationn);
		}
		return modificationss;
	}

	public static JSONArray parseProperts(List<PropertyItem> propetys) {
		JSONArray properts = new JSONArray();
		for (PropertyItem propert : propetys) {
			properts.add(parseProperty(propert));
		}
		return properts;
	}

	public static JSONObject parseProperty(PropertyItem propert) {
		JSONObject propet = new JSONObject();
		propet.put("property", propert.property);
		propet.put("value", propert.value);
		return propet;
	}

	public static JSONObject parseBattleShowInfo(BattleModel battle) {
		JSONObject obj = new JSONObject();
		if (battle == null || battle.getBattleEntity() == null) {
			obj.put("null_battle", true);
			System.out.println("Battle is null");
			return obj;
		}

		BattleEntity battleEntity = battle.getBattleEntity();
		try {
			JSONArray users = new JSONArray();
			if (battle.users != null) {
				for (BattleController player : battle.users.values()) {
					JSONObject obj_user = new JSONObject();
					obj_user.put("nickname", player.client.user.username);
					obj_user.put("rank", player.client.user.rang);
					obj_user.put("kills", player.playerTeamType.equals("NONE") ? player.userStat.kills : player.userStat.score);
					obj_user.put("team_type", player.playerTeamType);
					users.add(obj_user);
				}
			} else {
				System.out.println("No users in battle: " + battleEntity.battleId);
			}

			obj.put("paidBattle", battleEntity.paidBattle);
			obj.put("battleId", battleEntity.battleId);
			obj.put("users_in_battle", users);
			obj.put("killsLimit", battleEntity.killsLimit);
			obj.put("autobalance", battleEntity.autoBalance);
			obj.put("friendlyFire", battleEntity.friendlyFire);
			obj.put("withoutBonuses", battleEntity.withoutBonuses);
			obj.put("withoutInventory", battleEntity.withoutInventory);
			obj.put("goldBoxesEnabled", battleEntity.goldBoxesEnabled);
			obj.put("clanName", "");
			obj.put("reArmorEnabled", battleEntity.reArmorEnabled);
			obj.put("type", battleEntity.type);
			obj.put("fullCash", battleEntity.fullCash);
			obj.put("timeLimit", battleEntity.timeLimit);
			obj.put("scoreRed", battleEntity.scoreRed);
			obj.put("scoreBlue", battleEntity.scoreBlue);
			obj.put("timeCurrent", battle.getTimeLeft());
			obj.put("minRank", battleEntity.minRank);
			obj.put("name", battleEntity.name);
			obj.put("spectator", false);
			obj.put("userAlreadyPaid", battleEntity.userAlreadyPaid);
			obj.put("maxPeople", battleEntity.maxPeople);
			obj.put("previewId", battleEntity.battle.map.previewId);
			obj.put("maxRank", battleEntity.maxRank);

			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			obj.put("error", "Failed to parse battle info: " + e.getMessage());
			System.out.println("Error sending battle info: " + obj.toJSONString());
			return obj;
		}
	}

	public static JSONObject parseBattleInfo(BattleModel battle) {
		JSONObject obj = new JSONObject();
		List<BattleController> redPeoples = battle.getUsersByTeam("RED");
		List<BattleController> bluePeoples = battle.getUsersByTeam("BLUE");
		BattleEntity battleEntity = battle.getBattleEntity();
		obj.put("battleId", battleEntity.battleId);
		obj.put("friendsRed", 0);
		obj.put("redPeople", redPeoples.size());
		obj.put("team", !battleEntity.type.equals("DM"));
		obj.put("bluePeople", bluePeoples.size());
		obj.put("suspicious", battle.isSuspicious());
		obj.put("type", battleEntity.type);
		obj.put("friendsBlue", 0);
		obj.put("timeLimit", battleEntity.timeLimit / 60);
		obj.put("friendsDm", 0);
		obj.put("proBattle", false);
		obj.put("formatBattle", false);
		obj.put("countPeople", battle.users.size());
		obj.put("privateBattle", false);
		obj.put("minRank", battleEntity.minRank);
		obj.put("name", battleEntity.name);
		obj.put("mapId", battleEntity.mapid);
		obj.put("maxPeople", battleEntity.maxPeople);
		obj.put("previewId", battleEntity.battle.map.previewId);
		obj.put("maxRank", battleEntity.maxRank);
		return obj;
	}

	public static String parseTankSpecificationChange(BattleController client, double speed, double turnSpeed, double turretRotationSpeed, double turretTurnAcceleration) {
		if (client == null) {
			Logger.log(Logger.ERROR, "JSON.parseTankSpecificationChange - Client is null");
			return "{}";
		}
		try {
			JSONObject json = new JSONObject();
			json.put("speed", speed);
			json.put("turnSpeed", turnSpeed);
			json.put("turretRotationSpeed", turretRotationSpeed);
			json.put("turretTurnAcceleration", turretTurnAcceleration);
			json.put("immediate", true);
			Command command = new Command(Commands.ChangeTankSpecification, client.tank.id, json.toJSONString());
			client.battle.send2Battle(command);
			return json.toJSONString();
		} catch (Exception e) {
			Logger.log(Logger.ERROR, "JSON.parseTankSpecificationChange - Error: " + e.getMessage());
			return "{\"speed\":" + speed + ",\"turnSpeed\":" + turnSpeed +
					",\"turretRotationSpeed\":" + turretRotationSpeed +
					",\"turretTurnAcceleration\":" + turretTurnAcceleration;
		}
	}
}
