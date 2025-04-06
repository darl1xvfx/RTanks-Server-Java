package rt.server.services.shop;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;
import rt.server.PromoCodeItem;
import rt.server.client.ClientEntity;
import rt.server.database.PromocodeRepository;
import rt.server.database.Repositories;
import rt.server.localization.LocalizationLoader;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;
import rt.server.utils.JSON;

public class ShopModel {
    public void activatePromocode(ClientEntity client, String promocode) {
		PromocodeRepository promoRepository = Repositories.promocodeRepository;
		if (promoRepository.promocodes.containsKey(promocode)) {
			ArrayList<PromoCodeItem> items = promoRepository.promocodes.get(promocode);
			ArrayList<JSONObject> itemss = new ArrayList<JSONObject>();
			for (PromoCodeItem item : items) {
				itemss.add(JSON.parseShopItem(item.quantity, LocalizationLoader.getString(item.type.toUpperCase()).get(client.locale)));
			}
			new Command(Commands.ActivatePromocodeSuccessfully).send(client);
			this.donateSuccessfully(client, itemss);
			promoRepository.promocodes.remove(promocode);
		} else {
			new Command(Commands.ActivatePromocodeFailed).send(client);
		}
    }
    
    public void donateSuccessfully(ClientEntity client, List<JSONObject> items) {
    	new Command(Commands.DonateSuccessfully, JSON.parseDonateData(items)).send(client);
    }
}
