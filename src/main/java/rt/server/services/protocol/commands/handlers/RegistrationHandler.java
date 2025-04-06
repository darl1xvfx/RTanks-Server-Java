package rt.server.services.protocol.commands.handlers;

import org.json.JSONArray;
import rt.server.ServerProperties;
import rt.server.client.ClientEntity;
import rt.server.database.Repositories;
import rt.server.logger.Logger;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.CommandHandler;
import rt.server.services.protocol.commands.Commands;
import rt.server.user.Equipment;
import rt.server.user.permissions.Permissions; // Убедитесь, что импорт правильный
import rt.server.user.User;

public class RegistrationHandler implements CommandHandler {

	private static final String EXIST = "exist";
	private static final String NOT_EXIST = "not_exist";
	private static final String REGISTER = "REGISTER";
	private static final String STATE = "state";

	@Override
	public void handle(ClientEntity client, String command, String[] args) {
		if (command.equals(Commands.CheckName.command)) {
			handleCheckName(client, args);
			return;
		}

		if (command.equals(STATE)) {
			handleState(client);
			return;
		}

		handleRegistration(client, command, args);
	}

	private void handleCheckName(ClientEntity client, String[] args) {
		if (args.length < 1 || args[0] == null) {
			Logger.log(Logger.ERROR, "Неверный запрос CheckName: отсутствует имя пользователя");
			return;
		}

		String username = args[0];
		if (Repositories.userRepository.hasUser(username)) {
			new Command(Commands.CheckNameResult, EXIST).send(client);
			new Command(Commands.SetFreeUids, getFreeUIDs(username)).send(client);
		} else {
			new Command(Commands.CheckNameResult, NOT_EXIST).send(client);
		}
	}

	private void handleState(ClientEntity client) {
		if (ServerProperties.REGISTRATION_CAPTCHA_ENABLED) {
			client.createCaptcha(REGISTER);
		}
	}

	private void handleRegistration(ClientEntity client, String command, String[] args) {
		if (args.length < 1 || args[0] == null) {
			Logger.log(Logger.ERROR, "Неверный запрос регистрации: отсутствует пароль");
			new Command(Commands.AuthWrongCaptcha).send(client);
			return;
		}

		if (ServerProperties.REGISTRATION_CAPTCHA_ENABLED) {
			if (args.length < 2 || args[1] == null || !client.captchas.get(REGISTER).equals(args[1])) {
				Logger.log(Logger.ERROR, "Неверная капча для пользователя: " + command);
				new Command(Commands.AuthWrongCaptcha).send(client);
				client.createCaptcha(REGISTER);
				return;
			}
		}

		User user = new User(
				command,
				args[0],
				0,
				500,
				0,
				new Equipment("smoky_m0", "hunter_m0", "green"),
				Permissions.CHATADMINISTRATOR.toInt()
		);

		Repositories.userRepository.createUser(user);
		new Command(Commands.InfoDone).send(client);
		client.user = user;
		client.initLobby();
	}

	private JSONArray getFreeUIDs(String uid) {
		JSONArray uids = new JSONArray();
		for (String suffix : ServerProperties.UIDS_SUFFIX) {
			String uidd = uid + suffix;
			if (!Repositories.userRepository.hasUser(uidd)) {
				uids.put(uidd);
			}
		}
		return uids;
	}
}