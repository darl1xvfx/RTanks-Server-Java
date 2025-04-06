package rt.server.challenges;

import java.util.HashSet;
import java.util.List;
import org.hibernate.Hibernate;
import rt.server.client.ClientEntity;
import rt.server.database.Repositories;
import rt.server.logger.Logger;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;
import rt.server.user.Quest;
import rt.server.user.User;
import rt.server.utils.JSON;

public class QuestManager {

    private static QuestManager instance = new QuestManager();

    public static QuestManager getInstance() {
        return instance;
    }

    public void userInited(ClientEntity lobby) {
        try {
            User user = lobby.user;
            Hibernate.initialize(user.getQuests());
            List<Quest> userQuests = user.getQuests();
            for (Quest userQuest : userQuests) {
                QuestEntity baseQuest = QuestsParser.getQuestById(userQuest.questId);
                if (baseQuest == null) {
                    userQuests.remove(userQuest);
                    Repositories.userRepository.deleteUserQuest(userQuest);
                }
            }
            if (user.rang >= 4 && userQuests.size() < 3) {
                user.canSkipQuestForFree = true;
                for (int i = 0; i <= (4 - userQuests.size()); i++) {
                    if (userQuests.size() < 3) {
                        this.checkAndAddQuestsByLevel(user);
                    }
                }
            }
            user.setQuests(userQuests);
            Logger.log(Logger.INFO, "User inited: " + user.username + " with Quests: " + user.getQuests());
            Repositories.userRepository.updateUser(user);
        } catch (Exception e) {
            Logger.log(Logger.ERROR, "Failed to init user: " + e.getMessage());
            throw e;
        }
    }

    private void checkAndAddQuestsByLevel(User user) {
        HashSet<String> levels = new HashSet<>();
        List<Quest> userQuests = user.getQuests();
        for (Quest userQuest : userQuests) {
            QuestEntity baseQuest = QuestsParser.getQuestById(userQuest.questId);
            if (baseQuest != null) {
                levels.add(baseQuest.level);
            }
        }
        if (!levels.contains("easy")) {
            QuestEntity easyQuest = QuestsParser.getRandomEasyQuest();
            if (easyQuest != null) {
                Quest quest = new Quest(easyQuest.id, user);
                userQuests.add(quest);
                Repositories.persistObject(quest);
            }
        }
        if (!levels.contains("normal")) {
            QuestEntity normalQuest = QuestsParser.getRandomNormalQuest();
            if (normalQuest != null) {
                Quest quest = new Quest(normalQuest.id, user);
                userQuests.add(quest);
                Repositories.persistObject(quest);
            }
        }
        if (!levels.contains("hard")) {
            QuestEntity hardQuest = QuestsParser.getRandomHardQuest();
            if (hardQuest != null) {
                Quest quest = new Quest(hardQuest.id, user);
                userQuests.add(quest);
                Repositories.persistObject(quest);
            }
        }
        user.setQuests(userQuests);
        Repositories.userRepository.updateUser(user);
    }

    public void takePrize(ClientEntity client, String questId) {
        try {
            User user = client.user;
            for (Quest userQuest : user.getQuests()) {
                if (userQuest.questId.equals(questId)) {
                    user.getQuests().remove(userQuest);
                    Repositories.userRepository.deleteUserQuest(userQuest);
                    break;
                }
            }
            Repositories.userRepository.updateUser(user);
            new Command(Commands.TakeDailyQuestPrize, questId).send(client);
        } catch (Exception e) {
            Logger.log(Logger.ERROR, "Failed to take prize: " + e.getMessage());
            throw e;
        }
    }

    public void skipQuestForCrystals(ClientEntity lobbyManager, String questId, int skipPrice) {
        try {
            User user = lobbyManager.user;
            for (Quest userQuest : user.getQuests()) {
                if (userQuest.questId.equals(questId)) {
                    lobbyManager.addCrystals(-skipPrice);
                    this.skipQuest(lobbyManager, userQuest);
                    break;
                }
            }
        } catch (Exception e) {
            Logger.log(Logger.ERROR, "Failed to skip quest for crystals: " + e.getMessage());
            throw e;
        }
    }

    public void skipQuestForFree(ClientEntity lobbyManager, String questId) {
        try {
            User user = lobbyManager.user;
            for (Quest userQuest : user.getQuests()) {
                if (userQuest.questId.equals(questId) && user.canSkipQuestForFree) {
                    user.canSkipQuestForFree = false;
                    this.skipQuest(lobbyManager, userQuest);
                    break;
                }
            }
        } catch (Exception e) {
            Logger.log(Logger.ERROR, "Failed to skip quest for free: " + e.getMessage());
            throw e;
        }
    }

    private void skipQuest(ClientEntity lobbyManager, Quest userQuest) {
        try {
            User user = lobbyManager.user;
            Logger.log(Logger.INFO, "Skipping quest " + userQuest.questId + " for user " + user.username);
            user.getQuests().remove(userQuest);
            Repositories.userRepository.deleteUserQuest(userQuest);
            String questLevel = QuestsParser.getQuestLevelById(userQuest.questId);
            QuestEntity quest = questLevel == "normal" ? QuestsParser.getRandomNormalQuest() :
                    questLevel == "hard" ? QuestsParser.getRandomHardQuest() :
                            QuestsParser.getRandomEasyQuest();
            if (quest != null) {
                Quest newQuest = new Quest(quest.id, user);
                user.getQuests().add(newQuest);
                Repositories.persistObject(newQuest);
                Repositories.userRepository.updateUser(user);
                Logger.log(Logger.INFO, "Added new quest " + newQuest.questId + " for user " + user.username);
                new Command(Commands.SkipDailyQuest, userQuest.questId, JSON.parseSkipQuest(newQuest)).send(lobbyManager);
            } else {
                Logger.log(Logger.WARNING, "No replacement quest found for level: " + questLevel);
                Repositories.userRepository.updateUser(user);
            }
        } catch (Exception e) {
            Logger.log(Logger.ERROR, "Failed to skip quest: " + e.getMessage());
            throw e;
        }
    }

    public void updateQuestsProgress(String questId, int progress, User user) {
        if (progress <= 0) {
            return;
        }
        try {
            for (Quest quest : user.getQuests()) {
                QuestEntity questEntity = QuestsParser.getQuestById(questId);
                if (questEntity.id.equals(quest.questId) && quest.targetProgress != questEntity.targetProgress) {
                    quest.setProgress(Math.min(quest.targetProgress + progress, questEntity.targetProgress));
                    Repositories.mergeObject(quest);
                    Repositories.userRepository.updateUser(user);
                    break;
                }
            }
        } catch (Exception e) {
            Logger.log(Logger.ERROR, "Failed to update quest progress: " + e.getMessage());
            throw e;
        }
    }
}