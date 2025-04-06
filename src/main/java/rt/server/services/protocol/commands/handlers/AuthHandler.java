package rt.server.services.protocol.commands.handlers;

import rt.server.ServerProperties;
import rt.server.services.ban.block.BlockGameReason;
import rt.server.client.ClientEntity;
import rt.server.database.Repositories;
import rt.server.discord.oauth.DiscordOAuth2;
import rt.server.localization.LocalizationLoader;
import rt.server.logger.Logger;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.CommandHandler;
import rt.server.services.protocol.commands.Commands;
import rt.server.user.User;

public class AuthHandler implements CommandHandler {

	@Override
	public void handle(ClientEntity client, String command, String[] args) {
		if (command.equals(Commands.Login.command) || (!command.equals("state") && args.length >= 2)) {
			String username = command.equals(Commands.Login.command) ? args[0] : command;
			String password = args[0];
			Boolean remember = args.length > 1 ? Boolean.valueOf(args[1]) : false;

			User user = Repositories.userRepository.getUser(username);

			if (user == null) {
				Logger.log(Logger.ERROR, "Пользователь не найден: " + username);
				new Command(Commands.AuthDenied).send(client);
				return;
			}

			if (user.password.equals(password)) {
				if (!Repositories.userRepository.userIsBlocked(user.username)) {
					if (remember) {
						user.hash = String.valueOf(user.hashCode());
						Repositories.userRepository.updateUser(user);
						new Command(Commands.RememberHash, user.username, user.hash).send(client);
					}
					new Command(Commands.AuthAccept).send(client);
					client.user = user;
					client.initLobby();
				} else {
					String banReason = BlockGameReason.getReasonById(Repositories.userRepository.getBanReasonId(user.username)).getReason();
					String banMessage = LocalizationLoader.getString("BAN").get(client.locale).replaceAll("%REASON%", banReason);
					new Command(Commands.Ban, banMessage).send(client);
					Logger.log(Logger.ERROR, "Пользователь " + user.username + " заблокирован. Причина: " + banReason);
				}
			} else {
				Logger.log(Logger.ERROR, "Неверный пароль для " + username);
				new Command(Commands.AuthDenied).send(client);
			}
			return;
		}

		if (command.equals("state")) {
			if (ServerProperties.REGISTRATION_CAPTCHA_ENABLED) {
				client.createCaptcha("REGISTER");
			}
			return;
		}

		if (command.equals(Commands.AuthRefreshCaptcha.command)) {
			client.createCaptcha("REGISTER");
			return;
		}

		if (command.equals(Commands.LoginByHash.command)) {
			User user = Repositories.userRepository.getUserByHash(args[0]);
			if (user != null) {
				if (!Repositories.userRepository.userIsBlocked(user.username)) {
					new Command(Commands.AuthAccept).send(client);
					client.user = user;
					client.initLobby();
				} else {
					String banReason = BlockGameReason.getReasonById(Repositories.userRepository.getBanReasonId(user.username)).getReason();
					String banMessage = LocalizationLoader.getString("BAN").get(client.locale).replaceAll("%REASON%", banReason);
					new Command(Commands.Ban, banMessage).send(client);
					Logger.log(Logger.ERROR, "Пользователь " + user.username + " заблокирован. Причина: " + banReason);
				}
			} else {
				new Command(Commands.AuthDenied).send(client);
			}
			return;
		}

		if (command.equals(Commands.ExternalLogin.command)) {
			switch (args[0]) {
				case "discord":
					new Command(Commands.AuthOpenURL, DiscordOAuth2.generateUri()).send(client);
					return;
			}
			return;
		}
	}
}