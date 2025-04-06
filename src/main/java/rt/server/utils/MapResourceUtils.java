package rt.server.utils;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;
import rt.server.battles.maps.parser.Map;

public class MapResourceUtils {
	public static List<JSONObject> getResources(Map map) {
	    List<JSONObject> resources = new ArrayList<>();
	    resources.add(ResourceUtils.parseResourceToDependency(false, false, map.mapResource, 7, ResourceUtils.getMaxResourceVersion(map.mapResource)));
	    return resources;
	}
}
