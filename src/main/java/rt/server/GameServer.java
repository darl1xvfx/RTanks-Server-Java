package rt.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import rt.server.antiddos.AntiDDOSModel;
import rt.server.client.ClientEntity;
import rt.server.logger.Logger;
import rt.server.services.OnlineService;
import rt.server.services.protocol.commands.Command;
import rt.server.services.shop.ShopModel;

public class GameServer {

	private static ServerSocketChannel serverChannel;
	private static final ExecutorService clientThreadPool = Executors.newCachedThreadPool();
	private static boolean isOpened = false;
	public static ShopModel shopModel;
	private static AntiDDOSModel antiDDOSModel;

	public static void start() {
		initModels();
		open();
	}

	private static void initModels() {
		shopModel = new ShopModel();
		antiDDOSModel = new AntiDDOSModel(ServerProperties.MIN_DDOS_TIMEOUT, ServerProperties.MIN_DDOS_COUNT);
	}

	private static void open() {
		isOpened = true;
		try {
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(true);
			serverChannel.socket().bind(new InetSocketAddress(ServerProperties.IP, ServerProperties.GAME_PORT));
			Logger.log(Logger.INFO, "Game server started on port " + serverChannel.socket().getLocalPort());
			while (isOpened) {
				acceptClient();
			}
		} catch (IOException e) {
			Logger.log(Logger.ERROR, "Error starting server: " + e.getMessage());
		} finally {
			closeServerChannel();
		}
	}

	private static void acceptClient() {
		try {
			Socket socket = serverChannel.socket().accept();
			socket.setKeepAlive(true);
			//if (antiDDOSModel.onConnected(socket)) {
				Logger.log(Logger.INFO, "New connection!");
				ClientEntity clientEntity = new ClientEntity(socket);
				OnlineService.CLIENTS.add(clientEntity);
				clientThreadPool.execute(clientEntity);	
			//}
		} catch (IOException e) {
			Logger.log(Logger.ERROR, "Error accepting new connection: " + e.getMessage());
		}
	}

	private static void closeServerChannel() {
		if (serverChannel != null && serverChannel.isOpen()) {
			try {
				serverChannel.close();
				Logger.log(Logger.INFO, "Server channel closed");
			} catch (IOException e) {
				Logger.log(Logger.ERROR, "Error closing server channel: " + e.getMessage());
			}
		}
	}

	public static void shutdown() {
		try {
			clientThreadPool.shutdown();
			if (!clientThreadPool.awaitTermination(30, TimeUnit.SECONDS)) {
				clientThreadPool.shutdownNow();
				if (!clientThreadPool.awaitTermination(30, TimeUnit.SECONDS)) {
					Logger.log(Logger.ERROR, "Client thread pool did not terminate");
				}
			}
		} catch (InterruptedException ie) {
			clientThreadPool.shutdownNow();
			Thread.currentThread().interrupt();
		}
		closeServerChannel();
		isOpened = false;
		Logger.log(Logger.INFO, "Game server shut down");
	}
	
	public static void send2Lobbys(Command packet) {
		for (ClientEntity client : OnlineService.CLIENTS) {
			if (!client.currentLayout.equals("battle")) {
				packet.send(client);
			}
		}
	}

	public static void send2Lobbys(String string) {
		for (ClientEntity client : OnlineService.CLIENTS) {
			if (!client.currentLayout.equals("battle")) {
				client.send(string);
			}
		}	
	}
}
