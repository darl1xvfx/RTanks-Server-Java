package rt.server.challenges;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import rt.server.challenges.items.TierItem;
import rt.server.services.resource.Resource;

public class TiersParser {
	
	public static TreeMap<Integer, TierData> tiers;
	
	public static void parse() {
		HashMap<Integer, TierData> tiersss = new HashMap<>();
		try {
			JSONObject obj = (JSONObject) new JSONParser().parse(Resource.fileToString("challenges/tier.json"));
			JSONArray tierss = (JSONArray)obj.get("tiers");
			for (Object tier : tierss) {
				JSONObject tierObj = (JSONObject)tier;
				JSONObject freeItem = (JSONObject)tierObj.get("freeItem");
				JSONObject battlePassItem = (JSONObject)tierObj.get("battlePassItem");
				tiersss.put((int)(long)tierObj.get("stars"), new TierData(new TierItem((int)(long)freeItem.get("preview"), (int)(long)freeItem.get("amount"), (String)freeItem.get("name"), (boolean)freeItem.get("received")), new TierItem((int)(long)battlePassItem.get("preview"), (int)(long)battlePassItem.get("amount"), (String)battlePassItem.get("name"), (boolean)battlePassItem.get("received")), (int)(long)tierObj.get("stars")));
			}
			tiers = new TreeMap<>(tiersss);
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}
	
	public static List<TierData> getTiersByStars(int stars) {
		List<TierData> filteredTiers = new ArrayList<>();
        for (Map.Entry<Integer, TierData> entry : tiers.entrySet()) {
            if (entry.getKey() <= stars) {
                filteredTiers.add(entry.getValue());
            }
        }
        return filteredTiers;
	}
}
