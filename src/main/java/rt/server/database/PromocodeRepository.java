package rt.server.database;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import rt.server.PromoCodeItem;
import rt.server.logger.Logger;
import rt.server.services.resource.Resource;
import java.util.*;

public class PromocodeRepository {
	
	public HashMap<String, ArrayList<PromoCodeItem>> promocodes = new HashMap<String, ArrayList<PromoCodeItem>>();

	public void init() {
		try {
			JSONArray arr = (JSONArray) new JSONParser().parse(Resource.fileToString("promocodes.json"));
			for (Object obj : arr) {
				ArrayList<PromoCodeItem> items = new ArrayList<PromoCodeItem>();
				JSONObject jsonObj = (JSONObject)obj;
				String promocode = (String)jsonObj.get("code");
				JSONArray prizes = (JSONArray)jsonObj.get("types");
				for (Object prize : prizes) {
					JSONObject jsonPrize = (JSONObject)prize;
					items.add(new PromoCodeItem((String)jsonPrize.get("type"), (int)(long)jsonPrize.get("quantity")));
				}
				promocodes.put(promocode, items);
				Logger.log(Logger.INFO, "Loaded promocode: " + promocode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
