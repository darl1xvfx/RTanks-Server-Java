package rt.server.services.protocol.commands.handlers;

import java.util.Arrays;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import rt.server.GameServer;
import rt.server.battles.BattleEntity;
import rt.server.battles.BattleModel;
import rt.server.battles.BattleProcessor;
import rt.server.challenges.QuestsParser;
import rt.server.challenges.QuestEntity;
import rt.server.challenges.QuestManager;
import rt.server.client.ClientEntity;
import rt.server.database.Repositories;
import rt.server.friends.FriendsModel;
import rt.server.garage.GarageItemParser;
import rt.server.garage.lootbox.LootboxService;
import rt.server.garage.modification.ModificationInfo;
import rt.server.garage.mountable.HullItemsDataParser;
import rt.server.garage.mountable.MountableItemData;
import rt.server.garage.mountable.MountableItemsDataParser;
import rt.server.garage.mountable.Resistance;
import rt.server.localization.LocalizationLoader;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.CommandHandler;
import rt.server.services.protocol.commands.Commands;
import rt.server.services.resource.Resource;
import rt.server.utils.JSON;

public class LobbyHandler implements CommandHandler {

	@Override
	public void handle(ClientEntity client, String command, String[] args) {
		if (command.equals(Commands.GetGarageData.command)) {
			client.layoutService.changeLayoutSwitch("garage");
			new Command(Commands.InitGarageItems, JSON.parseGarageItemsData(client.user.getItems())).send(client);
			new Command(Commands.InitMarket, JSON.parseGarageItemsData(GarageItemParser.getItems(client.user))).send(client);

			String hullId = client.user.equipment.hullId;
			HullItemsDataParser.HullItemData hullData = HullItemsDataParser.get(hullId);
			int hullObject3ds = (hullData != null) ? hullData.object3ds : MountableItemsDataParser.get(hullId).object3ds;
			new Command(Commands.InitMountedItem, hullId, hullObject3ds).send(client);

			String turretId = client.user.equipment.turretId;
			MountableItemData turretData = MountableItemsDataParser.get(turretId);
			int turretObject3ds = (turretData != null) ? turretData.object3ds : 0;
			new Command(Commands.InitMountedItem, turretId, turretObject3ds).send(client);

			String colormapId = client.user.equipment.colormapId;
			MountableItemData colormapData = MountableItemsDataParser.get(colormapId);
			int colormapObject3ds = (colormapData != null) ? colormapData.object3ds : 0;
			new Command(Commands.InitMountedItem, colormapId, colormapObject3ds).send(client);

			return;
		}
		if (command.equals(Commands.ChangePassword.command)) {
			String oldPassword = args[0];
			String newPassword = args[1];

			try {
				boolean success = client.user.changePassword(oldPassword, newPassword);

				if (success) {
					new Command(Commands.ShowAlert, "Пароль успешно изменён").send(client);
				} else {
					String errorMessage = client.user.password.equals(oldPassword)
							? "Новый пароль должен содержать минимум 4 символов"
							: "Неверный старый пароль";
					new Command(Commands.ShowAlert, errorMessage).send(client);
				}
			} catch (Exception e) {
				new Command(Commands.ShowAlert, "Ошибка сервера при смене пароля").send(client);
				e.printStackTrace();
			}
			return;
		}
		if (command.equals(Commands.GetDataInitBattleSelect.command))
		{
			client.initBattleSelect();
			return;
		};
		if (command.equals(Commands.CreateBattle.command))
		{
			this.createBattle(args[0], client);
			return;
		};
		if (command.equals(Commands.GetShowBattleInfo.command)) 
		{
			this.showBattleInfo(args[0], client);
            return;
		};
		if (command.equals(Commands.GetShop.command))
		{
			new Command(Commands.OpenShop, Resource.fileToString("shop.json")).send(client);
			return;
		};
		if (command.equals(Commands.EnterBattle.command))
		{
			client.initBattle(args[0], args[1]);
			return;
		};
		if (command.equals(Commands.EnterBattleTeam.command))
		{
			client.initBattleTeam(args[0], args[1]);
			return;
		};
		if (command.equals(Commands.ShowProfile.command))
		{
			new Command(Commands.ShowProfile, JSON.parseShowSettingsData()).send(client);
			return;
		};
		if (command.equals(Commands.GetUserInfo.command))
		{
			new Command(Commands.UpdateUserInfo, JSON.parseUserInfo(Repositories.userRepository.getUser(args[0]))).send(client);
			return;
		};
		if (command.equals(Commands.ShowQuests.command))
		{
			JSON.parseShowQuests(client);
			new Command(Commands.SendTierInfo, JSON.parseTiers(client)).send(client);
			return;
		};
		if (command.equals(Commands.TryActivatePromocode.command))
		{
			GameServer.shopModel.activatePromocode(client, args[0]);
			return;
		};
		if (command.equals(Commands.GetFriends.command))
		{
			new Command(Commands.InitFriendsList, JSON.parseUpdateFriendsList(client.user, client.user.getFriends(), client.user.getOutgoingFriends(), client.user.getIncomingFriends())).send(client);
			return;
		};
		if (command.equals(Commands.MakeFriend.command)) 
		{
            FriendsModel.makeFriend(client, args[0]);
            return;
		};
		if (command.equals(Commands.AcceptFriend.command)) 
		{
			FriendsModel.acceptFriend(client, args[0]);
			return;
		};
		if (command.equals(Commands.ChangeQuest.command))
		{
            if (client.user.canSkipQuestForFree) {
            	QuestManager.getInstance().skipQuestForFree(client, args[0]);
            } else {
            	QuestManager.getInstance().skipQuestForCrystals(client, args[0], Integer.valueOf(args[1]));
            }
            return;
		};
		if (command.equals(Commands.QuestTakePrize.command))
		{
			QuestManager.getInstance().takePrize(client, args[0]);
			return;
		};
		if (command.equals(Commands.OpenLootbox.command))
		{
			int[] counts = {1, 5, 15};
			int count = Integer.valueOf(args[0]);
			for (int i : counts) {
				if (i == count) {
					new Command(Commands.OpenLootbox, JSON.parseLootboxPrizes(LootboxService.getRandomReward(client, count))).send(client);	
				}
			}
			return;
		};
		if (command.equals(Commands.ShopBuyItem.command))
		{
			String[] item = args[0].split("_");
			GameServer.shopModel.donateSuccessfully(client, Arrays.asList(JSON.parseShopItem(Integer.valueOf(item[1]), LocalizationLoader.getString("CRYSTALL").get(client.locale))));
			return;
		};
		if (command.equals(Commands.ReturnToBattle.command))
		{
			client.layoutService.changeLayoutSwitch("battle");
			client.layoutService.endLayoutSwitch(client.currentLayout, "battle");
			if (client.controller.changedEquipment) {
				new Command(Commands.InitDelayMount, JSON.parseInitDelayMount(client.delayMountService)).send(client);
				client.delayMountService.startTimer();
				if (client.controller.tank.state.equals("suicide")) {
					return;
				};
				new Command(Commands.StartRearmored, 5000).send(client);
			};
			return;
		};
	}
	
	private void showBattleInfo(String string, ClientEntity client) {
	    if (string != null && !string.equals("null") && !string.isEmpty()) {
	        BattleModel battle = BattleProcessor.getBattleById(string);
	        if (battle != null) {
	            new Command(Commands.ShowBattleInfo, JSON.parseBattleShowInfo(battle)).send(client);
			}
	    }	
	}

	private void createBattle(String json, ClientEntity client) {
		JSONParser parser = new JSONParser();
		try {
            Object obj = parser.parse(json);
            JSONObject jparser = (JSONObject)obj;
            BattleEntity battleEntity = new BattleEntity(BattleProcessor.generateId(), Boolean.valueOf(String.valueOf(jparser.get("autoBalance"))), Boolean.valueOf(String.valueOf(jparser.get("friendlyFire"))), false, Boolean.valueOf(String.valueOf(jparser.get("goldBoxesEnabled"))), Integer.parseInt(String.valueOf(jparser.get("scoreLimit"))), Integer.parseInt(String.valueOf(jparser.get("numPlayers"))), Integer.parseInt(String.valueOf(jparser.get("maxRang"))), Integer.parseInt(String.valueOf(jparser.get("minRang"))), String.valueOf(jparser.get("mapId")), /*Boolean.valueOf(String.valueOf(jparser.get("proBattle")))*/false, Boolean.valueOf(String.valueOf(jparser.get("reArmorEnabled"))), 0, 0, false, Integer.parseInt(String.valueOf(jparser.get("time"))), Integer.parseInt(String.valueOf(jparser.get("time"))), String.valueOf(jparser.get("battleMode")), false, Boolean.valueOf(String.valueOf(jparser.get("withoutBonuses"))), Boolean.valueOf(String.valueOf(jparser.get("withoutInventory"))), String.valueOf(jparser.get("mapId")));
            BattleModel battle = new BattleModel(battleEntity);
            BattleProcessor.addBattle(battle);
            GameServer.send2Lobbys(new Command(Commands.CreateBattle, JSON.parseBattleInfo(battle)));
            this.showBattleInfo(battleEntity.battleId, client);
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}
	}
}
