package rt.server.services.protocol.commands;

import rt.server.client.ClientEntity;

public interface CommandHandler {
    void handle(ClientEntity client, String command, String[] args);
}
