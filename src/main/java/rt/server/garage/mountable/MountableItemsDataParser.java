package rt.server.garage.mountable;

import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import rt.server.services.resource.Resource;

public class MountableItemsDataParser {

	private static HashMap<String, MountableItemData> datas;

	public static void parse() {
		datas = new HashMap<String, MountableItemData>();
		try {
			JSONObject jsonobj = (JSONObject) new JSONParser().parse(Resource.fileToString("mount_datas.json"));
			for (Object key : jsonobj.keySet()) {
				JSONObject obj = (JSONObject) jsonobj.get(key);
				datas.put((String) key, new MountableItemData((int)(long)obj.get("object3ds")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static MountableItemData get(String string) {
		return datas.get(string);
	}

}
