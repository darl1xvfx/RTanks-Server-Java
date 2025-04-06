package rt.server.garage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import rt.server.garage.modification.ModificationInfo;
import rt.server.services.resource.Resource;
import rt.server.user.User;
import java.io.BufferedReader;

public class GarageItemParser {

	public static HashMap<String, GarageItem> items;

	public static void parse() {
		if (items == null) {
			items = new HashMap<>();
			parseAndInitItems(Resource.fileToString("market.json"));
		}
	}

	private static void parseAndInitItems(String json) {
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(json);
			JSONObject jparser = (JSONObject)obj;
			JSONArray jarray = (JSONArray)jparser.get("items");
			for (int i = 0; i < jarray.size(); i++) {
				JSONObject item = (JSONObject)jarray.get(i);
				JSONArray modification = (JSONArray)item.get("modification");
				List<ModificationInfo> modificationInfos = new ArrayList<>();
				List<PropertyItem> propertyss = new ArrayList<>();
				for (Object modific : modification) {
					JSONObject jsonModific = (JSONObject) modific;
					List<PropertyItem> propertys = new ArrayList<>();
					for (Object property : (JSONArray)jsonModific.get("properts")) {
						JSONObject jsonPropert = (JSONObject) property;
						propertys.add(new PropertyItem(String.valueOf(jsonPropert.get("property")), String.valueOf(jsonPropert.get("value"))));
					}
					modificationInfos.add(new ModificationInfo(jsonModific.get("previewId") == null ? 0 : Integer.valueOf(String.valueOf(jsonModific.get("previewId"))), Integer.valueOf(String.valueOf(jsonModific.get("price"))), Integer.valueOf(String.valueOf(jsonModific.get("rank"))), propertys));
				}
				for (Object propertyy : (JSONArray)item.get("properts")) {
					JSONObject jsonPropertt = (JSONObject) propertyy;
					propertyss.add(new PropertyItem(String.valueOf(jsonPropertt.get("property")), String.valueOf(jsonPropertt.get("value"))));
				}
				GarageItem it = new GarageItem(String.valueOf(item.get("id")), String.valueOf(item.get("name")), String.valueOf(item.get("description")), Integer.parseInt(String.valueOf(item.get("type"))), Integer.parseInt(String.valueOf(item.get("rank"))), Integer.parseInt(String.valueOf(item.get("index"))), Integer.parseInt(String.valueOf(item.get("modificationID"))), Integer.parseInt(String.valueOf(item.get("price"))), Integer.parseInt(String.valueOf(item.get("next_price"))), Integer.parseInt(String.valueOf(item.get("next_rank"))), Boolean.parseBoolean(String.valueOf(item.get("isInventory"))), Integer.valueOf(String.valueOf(((JSONObject)modification.get(Integer.parseInt(String.valueOf(item.get("modificationID"))))).get("previewId"))));
				it.modifications = modificationInfos;
				it.propetys = propertyss;
				items.put(it.id, it);
			}
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}
	}

	public static List<JSONObject> getItems(User user) {
		List<JSONObject> itemss = new ArrayList<JSONObject>();
		for (GarageItem item : items.values()) {
			boolean shouldAdd = true;
			for (OwnedGarageItem userItem : user.getOwnedItems()) {
				if (userItem.getItemId().equals(item.id)) {
					shouldAdd = false;
					break;
				}
			}
			if (shouldAdd) {
				itemss.add(item.toItemObject());
			}
		}
		return itemss;
	}

}
