package rt.server.lobbychat.censore;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import rt.server.services.resource.Resource;

public class CensoreModel {
	
	private static List<String> censore = new ArrayList<>();
	
    public static void parse() {
        try {
            Throwable t = null;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(Resource.get("censore.txt").toFile()), StandardCharsets.UTF_8));
                try {
                    String line;
                    while ((line = reader.readLine()) != null)
                        censore.add(line);
                } finally {
                    if (reader != null)
                        reader.close();
                }
            } finally {
                if (t == null) {
                    Throwable t2 = null;
                    t = t2;
                } else {
                    Throwable t2 = null;
                    if (t != t2)
                        t.addSuppressed(t2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String checkAndCensore(String text) {
        for (String badWord : censore) {
            text = text.replaceAll("(?i)" + badWord, "*".repeat(badWord.length()));
        }
        return text;
    }
}
