package rt.server.antiddos;

import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import rt.server.logger.Logger;

public class AntiDDOSModel {

    private static final ConcurrentHashMap<String, AntiDDOSHandler> handlers = new ConcurrentHashMap<>();
    public static final List<String> blackList = new CopyOnWriteArrayList<>();
    public static int MIN_CONNECTION_INTERVAL;
    public static int MAX_CONNECTION_VALUE;

    public AntiDDOSModel(int interval, int value) {
        MIN_CONNECTION_INTERVAL = interval;
        MAX_CONNECTION_VALUE = value;
        Logger.log(Logger.INFO, "AntiDDOSModel initialized!");
    }

    public boolean onConnected(Socket socket) {
        String ipAddress = getIpAddress(socket);

        if (blackList.contains(ipAddress)) {
            Logger.log(Logger.INFO, "This IP (" + ipAddress + ") is blacklisted. Login denied.");
            return false;
        }

        handlers.computeIfAbsent(ipAddress, k -> new AntiDDOSHandler(socket)).handle();
        return true;
    }

    private String getIpAddress(Socket socket) {
        return socket.getRemoteSocketAddress().toString().split(":")[0];
    }

    static class AntiDDOSHandler {
        private final Socket socket;
        private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        private int connectionsValue;
        private boolean removerStarted = false;

        public AntiDDOSHandler(Socket socket) {
            this.socket = socket;
            this.connectionsValue = 0;
        }

        public void handle() {
            String ipAddress = getIpAddress();
            connectionsValue++;

            Logger.log(Logger.INFO, "Checking the new IP (" + ipAddress + "). Current connections: " + connectionsValue);

            if (connectionsValue >= AntiDDOSModel.MAX_CONNECTION_VALUE) {
                blacklistIp(ipAddress);
            } else {
                Logger.log(Logger.INFO, "Connection is assigned to this IP (" + ipAddress + ").");
                if (!removerStarted) {
                	startConnectionRemover(ipAddress);
                }
            }
            updateThis();
        }

        private String getIpAddress() {
            return socket.getRemoteSocketAddress().toString().split(":")[0].replaceAll("/", "");
        }

        private void blacklistIp(String ipAddress) {
            blackList.add(ipAddress);
            Logger.log(Logger.INFO, "This IP (" + ipAddress + ") is blacklisted.");
            closeSocket();
        }

        private void closeSocket() {
            try {
                socket.close();
            } catch (Exception e) {
                Logger.log(Logger.ERROR, "Error closing socket for IP: " + getIpAddress() + ", " + e.getMessage());
            }
        }

        private void startConnectionRemover(String ipAddress) {
            scheduler.scheduleAtFixedRate(() -> {
            	removerStarted = true;
                connectionsValue--;
                Logger.log(Logger.INFO, "The connection has been disconnected from this IP (" + ipAddress + "). Current connections: " + connectionsValue);
                if (connectionsValue <= 0) {
                    Logger.log(Logger.INFO, "The connections have been cleaned from this IP (" + ipAddress + ").");
                    scheduler.shutdown();
                    handlers.remove(ipAddress);
                    removerStarted = false;
                } else {
                	updateThis();
                }
            }, AntiDDOSModel.MIN_CONNECTION_INTERVAL, AntiDDOSModel.MIN_CONNECTION_INTERVAL, TimeUnit.MILLISECONDS);
        }

        private void updateThis() {
            handlers.replace(getIpAddress(), this);
        }
    }
}