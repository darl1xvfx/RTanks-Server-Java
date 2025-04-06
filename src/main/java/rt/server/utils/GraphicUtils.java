package rt.server.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import rt.server.logger.Logger;
import rt.server.services.resource.Resource;

public class GraphicUtils {
	
	private static HashMap<String, JSONObject> graphics = new HashMap<String, JSONObject>();
	
    public static void parse() {
        JSONParser parser = new JSONParser();
        try {
            final File[] maps = Resource.get("battles/graphics").toFile().listFiles();
            File[] array;
            for (int length = (array = maps).length, i = 0; i < length; ++i) {
                final File file = array[i];
                if (!file.isDirectory() && file.getName().endsWith(".json")) {
                	String nm = file.getName().split(".json")[0];
                	graphics.put(nm, (JSONObject) parser.parse(new String(Files.readAllBytes(file.toPath()))));
                    Logger.log(Logger.INFO, "Loaded graphics for " + nm);
                }
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    public static JSONObject getGraphicsByMap(String mapid) {
    	return graphics.get(mapid);
    }
}
