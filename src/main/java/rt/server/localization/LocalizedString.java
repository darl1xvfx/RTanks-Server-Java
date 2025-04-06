package rt.server.localization;

import java.util.HashMap;

public class LocalizedString {
	
	public HashMap<String, String> locale;
	
    public String get(String localization) {
    	return locale.get(localization);
    }
}
