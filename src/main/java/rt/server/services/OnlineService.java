package rt.server.services;

import java.util.ArrayList;
import java.util.List;
import rt.server.client.ClientEntity;
import rt.server.user.User;

public class OnlineService {
  private static int online;
  public static List<ClientEntity> CLIENTS = new ArrayList<>();
  private static int maxOnline;
  
  public static int getOnline() {
    return online;
  }
  
  public static int getMaxOnline() {
    return maxOnline;
  }
  
  public static ClientEntity getClientByUser(User user) {
  	for (ClientEntity client : CLIENTS) {
  		if (client.user == null) return null;
  		if (user == null) return null;
  		if (client.user.username.equals(user.username)) return client;
  	}
  	return null;
  }
  
  public static List<String> onlinePlayers = new ArrayList<>();
  
  public static Boolean inOnline(String username) {
    return Boolean.valueOf(onlinePlayers.contains(username));
  }
  
  public static void addOnline(String username) {
    online++;
    maxOnline = Math.max(maxOnline, online);
    onlinePlayers.add(username);
  }
  
  public static void removeInOnline(String nickname) {
    online--;
    onlinePlayers.remove(nickname);
  }

  public static String getOnlineMessage() {
  	return "Current online: " + getOnline() + "\nUsers: " + onlinePlayers + "\nMax online: " + getMaxOnline();
  }
}
