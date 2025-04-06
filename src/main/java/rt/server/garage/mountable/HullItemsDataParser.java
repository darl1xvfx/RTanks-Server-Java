package rt.server.garage.mountable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import rt.server.logger.Logger;
import rt.server.services.resource.Resource;

public class HullItemsDataParser {

    private static HashMap<String, HullItemData> hullDatas;

    public static void parse() {
        hullDatas = new HashMap<>();
        String directoryPath = "garage/items/hull/";

        try {
            Path hullDir = Resource.get(directoryPath);
            if (!Files.exists(hullDir) || !Files.isDirectory(hullDir)) {
                Logger.log(Logger.INFO, "Hull directory not found: " + hullDir);
                return;
            }
            Files.list(hullDir)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .forEach(filePath -> {
                        try {
                            String fileName = filePath.getFileName().toString();
                            String jsonContent = Resource.fileToString(directoryPath + fileName);
                            if (jsonContent.isEmpty()) {
                                Logger.log(Logger.WARNING, "Skipping empty file: " + fileName);
                                return;
                            }
                            JSONObject jsonObj = (JSONObject) new JSONParser().parse(jsonContent);
                            for (Object key : jsonObj.keySet()) {
                                JSONObject obj = (JSONObject) jsonObj.get(key);
                                int object3ds = ((Long) obj.get("object3ds")).intValue();
                                String hullKey = fileName.replace(".json", "") + "_" + key;
                                hullDatas.put(hullKey, new HullItemData(object3ds));
                            }
                            Logger.log(Logger.INFO, "Loaded new hull: " + fileName);
                        } catch (Exception e) {
                            Logger.log(Logger.ERROR, "Error parsing file " + filePath + ": " + e.getMessage());
                        }
                    });

        } catch (IOException e) {
            Logger.log(Logger.ERROR, "Error accessing hull directory: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static HullItemData get(String key) {
        return hullDatas.get(key);
    }

    public static class HullItemData {
        public int object3ds;

        public HullItemData(int object3ds) {
            this.object3ds = object3ds;
        }
    }
}