package rt.server.utils;

import java.io.File;

import org.json.simple.JSONObject;

import rt.server.services.resource.Resource;

public class ResourceUtils {
    private ResourceUtils() {}

    public static String getContentType(String fileName) {
        String extension = getFileExtension(fileName);
        switch (extension) {
           case "jpg":
               return "image/jpeg";
           case "png":
               return "image/png";
           case "json":
               return "application/json";
           case "xml":
               return "application/xml";
           default:
               return "application/octet-stream";
        }
    }

    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) ? fileName.substring(lastDotIndex + 1) : "";
    }
    
    public static JSONObject parseResourceToDependency(boolean lazy, boolean alpha, int id, int type, int version) {
    	JSONObject obj = new JSONObject();
    	obj.put("lazy", lazy);
    	obj.put("alpha", alpha);
    	obj.put("id", id);
    	obj.put("type", type);
    	obj.put("version", version);
    	return obj;
    }

	public static int getMaxResourceVersion(int mapResource) {
		File[] files = Resource.get("static/original/" + mapResource).toFile().listFiles();
		int version = 0;
		for (File file : files) {
			int maxVers = Integer.valueOf(file.getName());
			if (maxVers > version) {
				version = maxVers;
			}
		}
		return version;
	}
}
