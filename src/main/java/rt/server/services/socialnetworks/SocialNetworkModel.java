package rt.server.services.socialnetworks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.simple.JSONObject;
import rt.server.client.ClientEntity;
import rt.server.logger.Logger;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;
import rt.server.utils.JSON;

public class SocialNetworkModel {
	
	public static HashMap<String, Boolean> SOCIAL_NETWORKS = new HashMap<String, Boolean>();
	
    public static void init() {
    	SOCIAL_NETWORKS.put("discord", true);
    	Logger.log(Logger.INFO, "Loaded all social networks!");
    }
    
    public static void sendInitSocialNetworks(ClientEntity client) {
    	List<JSONObject> networks = new ArrayList<JSONObject>();
    	for (String key : SOCIAL_NETWORKS.keySet()) {
    		networks.add(JSON.parseSocialNetwork(key, SOCIAL_NETWORKS.get(key)));
    	}
    	new Command(Commands.InitSocialNetwork, networks).send(client);
    }
}
