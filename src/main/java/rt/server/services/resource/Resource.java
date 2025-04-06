package rt.server.services.resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Resource {
    private static Path resourceDirectory = null;

    public static Path get(String path) {
        if (resourceDirectory == null) {
            resourceDirectory = findResourceDirectory();
            if (resourceDirectory == null) {
                throw new IllegalStateException("Resource directory not found");
            }
        }
        return resourceDirectory.resolve(path);
    }

    private static Path findResourceDirectory() {
        Path[] possibleDirectories = {
                Paths.get("data"),
                Paths.get("../data"),
                Paths.get("src/main/resources/data"),
                Paths.get("../src/main/resources/data")
        };

        for (Path directory : possibleDirectories) {
            if (Files.exists(directory)) {
                return directory;
            }
        }

        System.err.println("Resource directory not found in any of the expected locations.");
        return null;
    }
    
    public static String fileToString(String file) {
    	try {
			return new String(Files.readAllBytes(get(file)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return "";
    }
}
