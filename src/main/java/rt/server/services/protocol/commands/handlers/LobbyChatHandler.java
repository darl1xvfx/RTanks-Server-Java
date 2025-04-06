package rt.server.services.protocol.commands.handlers;

import rt.server.GameServer;
import rt.server.Main;
import rt.server.battles.BattleModel;
import rt.server.battles.BattleProcessor;
import rt.server.services.OnlineService;
import rt.server.services.ban.BanChat;
import rt.server.services.ban.BanTimeType;
import rt.server.client.ClientEntity;
import rt.server.database.Repositories;
import rt.server.lobbychat.Channels;
import rt.server.lobbychat.censore.CensoreModel;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.CommandHandler;
import rt.server.services.protocol.commands.Commands;
import rt.server.user.User;
import rt.server.utils.BanService;
import rt.server.utils.JSON;
import org.json.simple.JSONObject;

public class LobbyChatHandler implements CommandHandler {

	@Override
	public void handle(ClientEntity client, String command, String[] args) {
		if (args.length == 3 && (!command.isEmpty() || !command.equals(" "))) {
			if (!client.floodController.detected(command)) {
               if (command.startsWith("/")) {
                  String temp = command.replace('/', ' ').trim();
                  String[] arguments = temp.split(" ");
                  switch (arguments[0]) {
                      case "addscore":
                          client.addScore(Integer.parseInt(arguments[1]));
                          this.system(client, "Score added", args[0], false);
                	      return;
                      case "addcry":
                	      client.addCrystals(Integer.parseInt(arguments[1]));
                	      this.system(client, "Crystals added", args[0], false);
                	      return;
                      case "addstars":
                	      client.addStars(Integer.parseInt(arguments[1]));
                	      this.system(client, "Stars added", args[0], false);
                	      return;
                      case "addpremium":
                	      client.addPremium(Integer.parseInt(arguments[1]));
                	      this.system(client, "Premium added", args[0], false);
                	      return;
                      case "stop":
                	      Main.shutdown();
                	      return;
                      case "online":
                	      this.system(client, OnlineService.getOnlineMessage(), args[0], false);
                	      return;
                      case "blockgame":
                           User userr = Repositories.userRepository.getUser(arguments[1]);
                           int reasonn = Integer.parseInt(arguments[2]);
                	       BanService.banGame(userr, client.user, reasonn);
                	       this.system(client, "Танкист " + userr.username + " был заблокирован и кикнут" , args[0], false);
                	       return;
                      default:
                          if (arguments[0].startsWith("ban")) {
                              BanTimeType time = BanChat.getTimeType(arguments[0]);
                              User user = Repositories.userRepository.getUser(arguments[1]);
                              String reason = Main.concatMassive(arguments, 2);
                              if (user == null) {
                               	  return;
                              }
                              BanService.banChat(time, user, client.user, reason);
                              this.system(client, "Танкист " + user.username + " лишен права выхода в эфир " + time.getNameType() +" Причина: " + reason , args[0], false);
                          }
                       return;
                  }
              } else {
            	  String msg = /*CensoreModel.checkAndCensore(*/command/*)*/;
            	  if (this.isBattleLink(msg)) {
            		  msg = this.generateBattleLink(msg);
            	  }
                  if (Channels.channels.get(args[0]).size() >= 50) {
            	      Channels.channels.get(args[0]).remove(0);
                  }
        	      JSONObject messageObject = JSON.parseMessage(0, false, false, client.user.username, false, args[0], client.user.permissions, "", 0, msg, client.user.rang);
        	      GameServer.send2Lobbys("lobby_chat;" + messageObject);
        	      Channels.channels.get(args[0]).add(messageObject);
              }
		   } else {
			   this.system(client, "Танксит пидорас. Причина: Флуд.", args[0], false);
		   }
		}
		return;
	}
	
	private String generateBattleLink(String msg) {
		String msgg = "#battle|";
		BattleModel bm = BattleProcessor.getBattleById(msg.replaceAll("#/battle=", ""));
		msgg += bm.getBattleEntity().name + "|" + bm.getBattleEntity().battleId;
		return msgg;
	}

	private boolean isBattleLink(String msg) {
		return msg.startsWith("#/battle=");
	}

	private void system(ClientEntity c, String m, String channel, boolean yellow) {
		new Command(Commands.LobbyChatSystem, m, yellow ? "yellow" : "green", channel).send(c);
	}
}
