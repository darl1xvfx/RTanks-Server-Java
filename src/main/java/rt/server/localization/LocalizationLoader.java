package rt.server.localization;

import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import rt.server.logger.Logger;
import rt.server.services.resource.Resource;

public class LocalizationLoader {

    private static final HashMap<String, LocalizedString> localizations = new HashMap<>();

    public static void init() {
        try {
            JSONArray locale = (JSONArray) new JSONParser().parse(Resource.fileToString("strings.json"));
            for (Object obj : locale) {
                JSONObject item = (JSONObject) obj;
                LocalizedString str = new LocalizedString();
                str.locale = (HashMap<String, String>) item.get("value");
                localizations.put((String) item.get("key"), str);
                Logger.log(Logger.INFO, "Parsed new string: " + item.get("key"));
            }
        } catch (Exception e) {
            Logger.log(Logger.ERROR, "Failed to load localizations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static LocalizedString getString(String key) {
        return localizations.get(key);
    }
}
