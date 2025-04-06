package rt.server.services.protocol.commands.handlers;

import rt.server.clans.Clan;
import rt.server.client.ClientEntity;
import rt.server.database.Repositories;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.CommandHandler;
import rt.server.services.protocol.commands.Commands;

public class ClanHandler implements CommandHandler  {
	@Override
	public void handle(ClientEntity client, String command, String[] args) {
		if (command.equals(Commands.ShowClan.command))
		{
			new Command(Commands.ShowClan).send(client);
			return;
		};
		if (command.equals(Commands.ValidateTag.command))
		{
			new Command(Commands.ClanTagNotExist).send(client);
			return;
		};
		if (command.equals(Commands.ValidateName.command))
		{
			new Command(Commands.ClanNameNotExist).send(client);
			return;
		};
		if (command.equals(Commands.CreateClan.command))
		{
			return;
			//Repositories.clanRepository.createClan(new Clan(args[0], args[1], Integer.valueOf(args[2]), client.user.username));
		};
	}
}
