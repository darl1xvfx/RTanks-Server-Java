package rt.server.services.protocol.commands.handlers;

import org.json.simple.JSONArray;
import rt.server.client.ClientEntity;
import rt.server.database.Repositories;
import rt.server.garage.GarageItem;
import rt.server.garage.GarageItemParser;
import rt.server.garage.OwnedGarageItem;
import rt.server.garage.modification.ModificationInfo;
import rt.server.garage.mountable.HullItemsDataParser;
import rt.server.garage.mountable.MountableItemData;
import rt.server.garage.mountable.MountableItemsDataParser;
import rt.server.garage.mountable.Resistance;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.CommandHandler;
import rt.server.services.protocol.commands.Commands;
import rt.server.utils.JSON;

public class GarageHandler implements CommandHandler {

	@Override
	public void handle(ClientEntity client, String command, String[] args) {
		if (command.equals(Commands.GetGarageData.command)) {
			JSONArray resistances = new JSONArray();
			if (client.controller == null) {
				client.layoutService.endLayoutSwitch("battleselect", "garage");
			} else {
				client.layoutService.endLayoutSwitch("battle", "garage");
			}
			for (Resistance resist : client.user.equipment.mountedResistances) {
				ModificationInfo resistModification = GarageItemParser.items.get(resist.getParts()[0]).modifications.get(Integer.valueOf(resist.getParts()[1]));
				resistances.add(resistModification.previewId);
			}
			new Command(Commands.GarageObjectLoaded, resistances).send(client);
			return;
		}
		if (command.equals(Commands.TryBuyItem.command)) {
			String[] idd = args[0].split("_m");
			ModificationInfo item = GarageItemParser.items.get(idd[0]).modifications.get(Integer.valueOf(idd[1]));
			client.user.addItem(new OwnedGarageItem(idd[0], Integer.valueOf(idd[1]), client.user));
			client.user.crystals -= item.price;
			if (client.user.getFirstPurchase()) {
				client.user.setFirstPurchase(false);
				client.user.crystals += 500;
				new Command(Commands.CompleteAchivement, 0).send(client);
			}
			new Command(Commands.BuyItem, args[0], JSON.parseBuyItemData(idd[0], true, 0)).send(client);
			Repositories.userRepository.updateUser(client.user);
			return;
		}
		if (command.equals(Commands.TryMountItem.command)) {
			HullItemsDataParser.HullItemData hullData = HullItemsDataParser.get(args[0]);
			MountableItemData itemData = MountableItemsDataParser.get(args[0]);

			int object3ds;
			if (hullData != null) {
				object3ds = hullData.object3ds;
			} else if (itemData != null) {
				object3ds = itemData.object3ds;
			} else {
				System.out.println("Item not found: " + args[0]);
				return;
			}
			new Command(Commands.MountItem, args[0], object3ds).send(client);
			GarageItem item = GarageItemParser.items.get(args[0].split("_")[0]);
			if (client.controller != null) {
				client.controller.changedEquipment = true;
				if (item != null) {
					switch (item.type) {
						case 1:
							client.delayMountService.delayMountWeaponInSec = 60;
							break;
						case 2:
							client.delayMountService.delayMountArmorInSec = 60;
							break;
						case 3:
							client.delayMountService.delayMountResistanceInSec = 60;
							break;
					}
				}
			}
			switch (item.type) {
				case 1:
					client.user.equipment.turretId = args[0];
					break;
				case 2:
					client.user.equipment.hullId = args[0];
					break;
				case 3:
					client.user.equipment.colormapId = args[0];
					break;
			}
			Repositories.userRepository.updateUser(client.user);
			return;
		}
		if (command.equals(Commands.TryMountResistance.command)) {
			Resistance resist = new Resistance(args[0], client.user.equipment);
			client.user.equipment.mountedResistances.add(resist);
			if (client.controller != null) {
				client.delayMountService.delayMountResistanceInSec = 60;
				client.controller.changedEquipment = true;
			}
			Repositories.userRepository.updateUser(client.user);
			return;
		}
		if (command.equals(Commands.TryUnmountResistance.command)) {
			for (Resistance res : client.user.equipment.mountedResistances) {
				if (res.resistance.equals(args[0])) {
					client.user.equipment.mountedResistances.remove(res);
					if (client.controller != null) {
						client.delayMountService.delayMountResistanceInSec = 60;
						client.controller.changedEquipment = true;
					}
					break;
				}
			}
			Repositories.userRepository.updateUser(client.user);
			return;
		}
		if (command.equals(Commands.TryUpdateItem.command)) {
			this.onTryUpdateItem(client, args[0]);
		}
	}

	public void onTryUpdateItem(ClientEntity client, String id) {
		OwnedGarageItem ownedItem = client.user.getItem(id.substring(0, id.length() - 3));
		GarageItem item = GarageItemParser.items.get(ownedItem.itemId);
		int modificationID = Integer.parseInt(id.substring(id.length() - 1));
		ownedItem.modification = modificationID + 1;
		client.user.crystals -= item.modifications.get(modificationID + 1).price;
		new Command(Commands.UpdateItem, id).send(client);
		Repositories.userRepository.updateUser(client.user);
	}
}