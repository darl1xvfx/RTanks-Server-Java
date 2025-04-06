package rt.server.services.protocol.commands.handlers;

import rt.server.client.ClientEntity;
import rt.server.database.Repositories;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.CommandHandler;
import rt.server.services.protocol.commands.Commands;
import rt.server.services.resource.Resource;
import rt.server.services.socialnetworks.SocialNetworkModel;

public class SystemHandler implements CommandHandler {

	@Override
	public void handle(ClientEntity client, String command, String[] args) {
		try {
		   if (command.equals(Commands.GetAesData.command))
		   {
			   client.locale = args[0];
		       new Command(Commands.InitLocalization, Resource.fileToString("lang/" + args[0] + ".json")).send(client);
			   client.dependencyUtils.loadDependency("auth.json", () -> {
				   SocialNetworkModel.sendInitSocialNetworks(client);
				   new Command(Commands.InitAuth).send(client);
			   });
			   return;
		   };
		   if (command.equals(Commands.ResourcesLoaded.command))
		   {
			   client.dependencyUtils.mark(Integer.valueOf(args[0]));
			   return;
		   };
		   if (command.equals(Commands.ChangeCountry.command))
		   {
			 if (client.user != null) {  
				 client.user.country = args[0];
				 Repositories.userRepository.updateUser(client.user);
			 };
			 new Command(Commands.UpdateCountry, args[0]).send(client);
			 return;
		   };
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
