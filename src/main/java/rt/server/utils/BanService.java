package rt.server.utils;

import rt.server.services.OnlineService;
import rt.server.ServerProperties;
import rt.server.services.ban.BanTimeType;
import rt.server.client.ClientEntity;
import rt.server.database.Repositories;
import rt.server.user.User;

public class BanService {
    public static void banChat(BanTimeType time, User user, User user2, String reason) {
    }
    
    public static void banGame(User user, User user2, int reason) {
    	Repositories.userRepository.banUser(user, reason);
    	Repositories.userRepository.updateUser(user);
    	ClientEntity userClient = OnlineService.getClientByUser(user);
    	if (userClient != null) {
    		userClient.disconnect();
    	}
    }
    
    public static void unban(User user) {
    	Repositories.userRepository.unbanUser(user);
    	Repositories.userRepository.updateUser(user);
    }
}
