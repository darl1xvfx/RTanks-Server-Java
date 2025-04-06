package rt.server;

import rt.server.logger.Logger;
import rt.server.services.resource.Resource;
import rt.server.services.resource.ServerIdResource;
import rt.server.utils.ResourceUtils;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import com.sun.net.httpserver.*;

public class ResourceServer {
  private static final String STATIC_ROOT = "static";
  private static final String ORIGINAL_PACK_NAME = "original";
  private static HttpServer server;

  public static void start() throws IOException {
    server = HttpServer.create(new InetSocketAddress(ServerProperties.IP, ServerProperties.RESOURCE_PORT), 0);
    server.createContext("/", new RequestHandler());
    server.setExecutor(Executors.newFixedThreadPool(10));
    server.start();
    Logger.log(Logger.INFO, "Resource Server started on port " + ServerProperties.RESOURCE_PORT);
  }
  
  public static void shutdown() {
	  server.stop(0);
  }

  static class RequestHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
      String[] pathSegments = exchange.getRequestURI().getPath().split("/");
      if (pathSegments.length < 7) {
          sendNotFound(exchange, "Invalid URL format.");
          return;
        }

        String id1 = pathSegments[2];
        String id2 = pathSegments[3];
        String id3 = pathSegments[4];
        String id4 = pathSegments[5];
        String version = pathSegments[6];
        String file = pathSegments[7];

        Logger.log(Logger.INFO, String.format("Received request for resource: %s/%s/%s/%s/%s/%s", id1, id2, id3, id4, version, file));

        ServerIdResource resourceId;
        try {
          resourceId = decodeId(Arrays.asList(id1, id2, id3, id4, version));
        } catch (Exception e) {
          Logger.log(Logger.ERROR, "Failed to decode resource ID: " + e.getMessage());
          sendNotFound(exchange, "Invalid resource ID.");
          return;
        }

      Path resourcePath = getResourcePath(resourceId, version, file);
      File resource = resourcePath.toFile();

      if (!resource.exists()) {
        Logger.log(Logger.ERROR, "Resource not found. " + resourcePath);
        sendNotFound(exchange, "Resource not found.");
        return;
      }

      String contentType = getContentType(resource);
      exchange.getResponseHeaders().set("Content-Type", contentType);
      exchange.sendResponseHeaders(200, resource.length());
      try (OutputStream os = exchange.getResponseBody(); InputStream is = new FileInputStream(resource)) {
        is.transferTo(os);
      }
      Logger.log(Logger.INFO, String.format("Sent resource %s:%s/%s", resourceId.id, version, file));
    }
    
    public static ServerIdResource decodeId(List<String> parts) {
        return new ServerIdResource(
                (Long.parseLong(parts.get(0), 8) << 24) |
                        (Long.parseLong(parts.get(1), 8) << 16) |
                        (Long.parseLong(parts.get(2), 8) << 8) |
                        Long.parseLong(parts.get(3), 8),
                Long.parseLong(parts.get(4), 8)
        );
    }

    private Path getResourcePath(ServerIdResource resourceId, String version, String file) {
        int[] versionOffsets = {0, -4, -2};
        for (int offset : versionOffsets) {
          Path path = Resource.get(String.format("%s/%s/%s/%d/%s", STATIC_ROOT, ORIGINAL_PACK_NAME, resourceId.id, Integer.parseInt(version) + offset, file));
          if (path.toFile().exists()) {
            return path;
          }
        }
        return Paths.get("");
      }

    private void sendNotFound(HttpExchange exchange, String message) throws IOException {
      exchange.sendResponseHeaders(404, 0);
      try (OutputStream os = exchange.getResponseBody()) {
        os.write(getNotFoundBody(message).getBytes());
      }
    }

    private String getNotFoundBody(String message) {
      return "<html><body><h1>404 Not Found</h1><p>" + message + "</p></body></html>";
    }

    private String getContentType(File file) {
      return ResourceUtils.getContentType(file.getName());
    }
  }
}
