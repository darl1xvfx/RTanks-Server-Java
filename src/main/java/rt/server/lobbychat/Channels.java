package rt.server.lobbychat;

import java.util.*;
import org.json.simple.JSONObject;

public class Channels {
    public static HashMap<String, ArrayList<JSONObject>> channels;
    
    public static void init() {
    	channels = new HashMap<String, ArrayList<JSONObject>>();
    	channels.put("Общий RU", new ArrayList<JSONObject>());
    	channels.put("Общий EN", new ArrayList<JSONObject>());
    }
}
