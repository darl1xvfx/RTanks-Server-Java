package rt.server.friends;

import rt.server.ServerProperties;
import rt.server.client.ClientEntity;
import rt.server.database.Repositories;
import rt.server.database.UserRepository;
import rt.server.services.OnlineService;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;
import rt.server.user.User;
import rt.server.utils.JSON;

public class FriendsModel {

    private static UserRepository userRepository = Repositories.userRepository;

    public static void acceptFriend(ClientEntity client, String username) {
        if (!userRepository.hasUser(username)) {
            return; // Пользователь не существует
        }

        User user = userRepository.getUser(username);
        if (client.user.username.equals(user.username)) {
            return; // Нельзя добавить себя в друзья
        }

        // Выполняем все операции в одной транзакции
        Repositories.executeInTransaction(session -> {
            // Удаляем запросы, проверяя их существование в базе
            client.user.removeIncomingFriend(user.username);
            user.removeOutgoingFriend(client.user.username);

            // Добавляем друзей
            client.user.addFriend(user.username);
            user.addFriend(client.user.username);

            // Обновляем пользователей в базе
            session.merge(client.user);
            session.merge(user);
        });

        // Отправляем обновленные списки друзьям
        ClientEntity userClient = OnlineService.getClientByUser(user);
        new Command(Commands.UpdateFriendsList,
                JSON.parseUpdateFriendsList(client.user, client.user.getFriends(),
                        client.user.getOutgoingFriends(), client.user.getIncomingFriends()))
                .send(client);

        if (userClient != null && userClient != client) {
            new Command(Commands.UpdateFriendsList,
                    JSON.parseUpdateFriendsList(user, user.getFriends(),
                            user.getOutgoingFriends(), user.getIncomingFriends()))
                    .send(userClient);
        }
    }

    public static void makeFriend(ClientEntity client, String username) {
        if (!userRepository.hasUser(username)) {
            return; // Пользователь не существует
        }

        User user = userRepository.getUser(username);
        if (client.user.username.equals(user.username)) {
            return; // Нельзя отправить запрос себе
        }

        // Добавляем исходящий и входящий запросы в одной транзакции
        Repositories.executeInTransaction(session -> {
            client.user.addOutgoingFriend(username);
            user.addIncomingFriend(client.user.username);

            // Обновляем пользователей в базе
            session.merge(client.user);
            session.merge(user);
        });

        // Отправляем обновленные списки
        ClientEntity userClient = OnlineService.getClientByUser(user);
        new Command(Commands.UpdateFriendsList,
                JSON.parseUpdateFriendsList(client.user, client.user.getFriends(),
                        client.user.getOutgoingFriends(), client.user.getIncomingFriends()))
                .send(client);

        if (userClient != null && userClient != client) {
            new Command(Commands.UpdateFriendsList,
                    JSON.parseUpdateFriendsList(user, user.getFriends(),
                            user.getOutgoingFriends(), user.getIncomingFriends()))
                    .send(userClient);
        }
    }
}