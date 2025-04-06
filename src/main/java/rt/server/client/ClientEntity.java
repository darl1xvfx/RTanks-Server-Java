package rt.server.client;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rt.server.GameServer;
import rt.server.services.OnlineService;
import rt.server.ServerProperties;
import rt.server.battles.BattleController;
import rt.server.battles.BattleModel;
import rt.server.battles.BattleProcessor;
import rt.server.challenges.QuestManager;
import rt.server.challenges.TierData;
import rt.server.challenges.TiersParser;
import rt.server.database.Repositories;
import rt.server.garage.DelayMountService;
import rt.server.lobbychat.flood.FloodController;
import rt.server.logger.Logger;
import rt.server.services.protocol.ProtocolUtils;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.CommandHandler;
import rt.server.services.protocol.commands.Commands;
import rt.server.services.protocol.commands.handlers.AuthHandler;
import rt.server.services.protocol.commands.handlers.BattleHandler;
import rt.server.services.protocol.commands.handlers.ClanHandler;
import rt.server.services.protocol.commands.handlers.GarageHandler;
import rt.server.services.protocol.commands.handlers.LobbyChatHandler;
import rt.server.services.protocol.commands.handlers.LobbyHandler;
import rt.server.services.protocol.commands.handlers.RegistrationHandler;
import rt.server.services.protocol.commands.handlers.SystemHandler;
import rt.server.services.resource.Resource;
import rt.server.user.User;
import rt.server.utils.CaptchaUtils;
import rt.server.utils.DependencyUtils;
import rt.server.utils.JSON;
import rt.server.utils.RankUtils;

public class ClientEntity implements Runnable {

	private Socket socket;
	public DependencyUtils dependencyUtils;
	private volatile boolean running;
	public String currentLayout = "";
	public LayoutService layoutService;
	public User user;
	public BattleController controller;
	public DelayMountService delayMountService;
	public HashMap<String, String> captchas;
	public String locale = "NONE";
	public FloodController floodController;
	private final ExecutorService executorService;
	private OutputStream outputStream;

	public ClientEntity(Socket socket) {
		this.socket = socket;
		this.dependencyUtils = DependencyUtils.getInstance(this);
		this.running = true;
		this.layoutService = new LayoutService(this);
		this.delayMountService = new DelayMountService();
		this.floodController = new FloodController();
		this.captchas = new HashMap<>();
		this.executorService = Executors.newCachedThreadPool();
	}

	public void send(String packet) {
		String packett = packet + ServerProperties.DELIM_COMMANDS_SYMBOL;
		try {
			if (outputStream == null) {
				outputStream = new BufferedOutputStream(this.socket.getOutputStream());
			}
			outputStream.write(packett.getBytes(StandardCharsets.UTF_8));
			outputStream.flush();
		} catch (IOException e) {
			Logger.log(Logger.ERROR, "Error sending data to client: " + e.getMessage());
		}
	}


	@Override
	public void run() {
		try {
			InputStream inputStream = this.socket.getInputStream();
			byte[] arrayOfByte = new byte[8192];
			while (running) {
				int bytes = inputStream.read(arrayOfByte);
				if (bytes == -1) {
					break;
				}
				if (bytes > 0) {
					String data = new String(arrayOfByte, 0, bytes);
					if (!data.isEmpty()) {
						String decrypted = ProtocolUtils.decrypt(data, 1);
						if (decrypted != null && !decrypted.isEmpty()) {
							sendToHandlersAsync(decrypted);
						}
					}
				} else {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			}
		} catch (IOException e) {
			Logger.log(Logger.ERROR, "Error reading from socket: " + e.getMessage());
		} finally {
			disconnect();
		}
	}

	private void sendToHandlersAsync(String decrypted) {
		executorService.submit(() -> sendToHandlers(decrypted));
	}

	private void sendToHandlers(String decrypted) {
		CommandHandler handler = null;
		String[] args = ProtocolUtils.getArgsFromPacket(decrypted);
		String name = ProtocolUtils.getNameFromPacket(decrypted);
		String type = ProtocolUtils.getTypeFromPacket(decrypted);
		switch (type)
		{
			case "system":
				handler = new SystemHandler();
				handler.handle(this, name, args);
				return;
			case "auth":
				handler = new AuthHandler();
				handler.handle(this, name, args);
				return;
			case "registration":
				handler = new RegistrationHandler();
				handler.handle(this, name, args);
				return;
			case "lobby":
				handler = new LobbyHandler();
				handler.handle(this, name, args);
				return;
			case "lobby_chat":
				handler = new LobbyChatHandler();
				handler.handle(this, name, args);
				return;
			case "garage":
				handler = new GarageHandler();
				handler.handle(this, name, args);
				return;
			case "battle":
				handler = new BattleHandler();
				handler.handle(this, name, args);
				return;
			case "clan":
				handler = new ClanHandler();
				handler.handle(this, name, args);
        }
	}

	public void initLobby() {
		for (TierData tier : TiersParser.getTiersByStars(this.user.stars)) {
			this.user.receivedTiers.add(tier.stars);
		}
		new Command(Commands.InitCountries, Resource.fileToString("countries.json"), user.country).send(this);
		QuestManager.getInstance().userInited(this);
		new Command(Commands.SetStars, this.user.stars).send(this);
		new Command(Commands.StartEvent, (int)ServerProperties.CHALLENGES_TIME).send(this);
		new Command(Commands.InitFriendsService, JSON.parseInitFriendsService(this.user.getIncomingFriends(), this.user.getOutgoingFriends(), this.user.getFriends())).send(this);
		new Command(Commands.UpdateUserInfo, JSON.parseUserInfo(this.user)).send(this);
		new Command(Commands.InitPanel, JSON.parseInitPanelData(this.user)).send(this);
		this.updatePremiumStatus();
		new Command(Commands.SetRankProgress, RankUtils.getUpdateNumber(this.user.score)).send(this);
		new Command(Commands.InitClan, JSON.parseInitClan()).send(this);
		new Command(Commands.UpdateClanLicense, true).send(this);
		initBattleSelect();
		initChat();
	}

	public void initChat() {
		new Command(Commands.InitChat).send(this);
		new Command(Commands.InitMessages, JSON.parseInitMessages(this), Resource.fileToString("news.json")).send(this);
	}

	public void onExitFromBattle() {
		if (this.controller != null) {
			this.controller.onDestroy(false);
			this.controller = null;
		}
		this.initChat();
	}

	public void initBattleSelect() {
		this.layoutService.changeLayoutSwitch("battleselect");
		new Command(Commands.InitBattleSelect, JSON.parseBattleMapList(false, BattleProcessor.getBattles())).send(this);
		this.layoutService.endLayoutSwitch(this.currentLayout, "battleselect");
	}

	public void addScore(int score) {
		this.user.score += score;
		new Command(Commands.SetScore, this.user.score).send(this);
		new Command(Commands.SetRankProgress, RankUtils.getUpdateNumber(this.user.score)).send(this);
		if (this.user.score >= this.user.getNextScore()) {
			this.addCrystals(RankUtils.getRankByIndex(this.user.rang - 1).prize);
			this.updatePremiumStatus();
			if ((this.user.rang >= 30)) {
				return;
			}
			this.user.rang = RankUtils.getNumberRank(RankUtils.getRankByScore(this.user.score));
			new Command(Commands.SetRank, this.user.rang, this.user.getNextScore()).send(this);
			if (this.controller != null && this.controller.userInited) {
				this.controller.battle.send2Battle(new Command(Commands.CreateLevelupEffect, this.user.username, this.user.rang));
			}
		}
		Repositories.userRepository.updateUser(this.user);
	}

	public void addCrystals(int crystals) {
		this.user.crystals += crystals;
		new Command(Commands.SetCrystals, this.user.crystals).send(this);
		Repositories.userRepository.updateUser(this.user);
	}

	public void addPremium(int premium) {
		if (user.premium <= 0) {
			new Command(Commands.StartPremiumAccount).send(this);
		}
		user.premium += premium * 86400;
		this.updatePremiumStatus();
		Repositories.userRepository.updateUser(this.user);
	}

	private void updatePremiumStatus() {
		new Command(Commands.UpdatePremiumStatus, this.hasPremium()).send(this);
	}

	public boolean hasPremium() {
		return user.premium > 0.00001;
	}

	public void initBattle(String battleId, String spectator) {
		BattleModel battle = BattleProcessor.getBattleById(battleId);
		this.controller = new BattleController(battle, this, "NONE");
		this.layoutService.changeLayoutSwitch("battle");
		new Command(Commands.StartBattle).send(this);
		this.controller.init();
		GameServer.send2Lobbys(new Command(Commands.AddPlayerToBattle, JSON.parseAddPlayerToBattle(this.controller)));
		GameServer.send2Lobbys(new Command(Commands.UpdateCountUsersInDMBattle, JSON.parseUpdateCountUsersDM(battle)));
	}

	public void createCaptcha(String location) {
		try {
			String answer = CaptchaUtils.getRandomCaptchaText(6);
			byte[] captcha = CaptchaUtils.generateCaptcha(answer);
			if (captchas.containsKey(location)) {
				captchas.remove(location);
			}
			captchas.put(location, answer);
			new Command(Commands.AuthEnableCaptcha, location, CaptchaUtils.bytesToString(captcha)).send(this);
			new Command(Commands.AuthUpdateCaptcha, location, CaptchaUtils.bytesToString(captcha)).send(this);
		} catch (Exception e) {}
	}

	public void disconnect() {
		if (this.controller != null) {
			this.delayMountService.stopTimer();
			this.controller.onDisconnect();
		}
		OnlineService.CLIENTS.remove(this);
		try {
			if (socket != null && !socket.isClosed()) {
				socket.close();
				Logger.log(Logger.INFO, "Client disconnected: " + socket.getRemoteSocketAddress());
			}
		} catch (IOException e) {
			Logger.log(Logger.ERROR, "Error disconnecting client: " + e.getMessage());
		}
		running = false;
	}

	public void initBattleTeam(String battleId, String team) {
		BattleModel battle = BattleProcessor.getBattleById(battleId);
		this.controller = new BattleController(battle, this, Boolean.valueOf(team) ? "RED" : "BLUE");
		this.layoutService.changeLayoutSwitch("battle");
		new Command(Commands.StartBattle).send(this);
		this.controller.init();
		GameServer.send2Lobbys(new Command(Commands.AddPlayerToBattle, JSON.parseAddPlayerToBattle(this.controller)));
		GameServer.send2Lobbys(new Command(Commands.UpdateCountUsersInTDMBattle, JSON.parseUpdateCountUsersTeam(battle)));
	}

	public void addStars(int parseInt) {
		this.user.stars += parseInt;
		new Command(Commands.SetStars, this.user.stars).send(this);
		Repositories.userRepository.updateUser(this.user);
		for (TierData tier : TiersParser.getTiersByStars(this.user.stars)) {
			if (!this.user.receivedTiers.contains(tier.stars)) {
				this.user.receivedTiers.add(tier.stars);
				switch (tier.freeItem.name) {
					case "CRYSTALL": {
						this.addCrystals(tier.freeItem.amount);
						Repositories.userRepository.updateUser(this.user);
						return;
					}
				}
			}
		}
	}
}
