package rt.server.telegram.bot;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;

import rt.server.Main;
import rt.server.services.OnlineService;
import rt.server.ServerProperties;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import rt.server.client.ClientEntity;
import rt.server.database.Repositories;
import rt.server.logger.Logger;
import rt.server.user.User;
import rt.server.utils.RankUtils;

public class TelegramBot extends TelegramLongPollingBot {

    public static void start() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TelegramBot());
    }

    @Override
    public String getBotUsername() {
        return ServerProperties.TELEGRAM_USERNAME_BOT;
    }

    @Override
    public String getBotToken() {
        return ServerProperties.TELEGRAM_TOKEN_BOT;
    }

    @Override
    public void onUpdateReceived(Update update) {
    	try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String message = update.getMessage().getText();
                long chatId = update.getMessage().getChatId();
                SendMessage msg = new SendMessage();
                msg.setChatId(Long.toString(chatId));
            	if (message.startsWith("!")) {
                    String temp = message.replace('!', ' ').trim();
                    String[] arguments = temp.split(" ");
                    switch (arguments[0]) {
                        case "addcry": {
                        	if (arguments.length >= 2) {
                        		if (Repositories.userRepository.hasUser(arguments[1])) {
                        		    User user = Repositories.userRepository.getUser(arguments[1]);
                        		    int crystals = Integer.valueOf(arguments[2]);
                        		    ClientEntity userClient = OnlineService.getClientByUser(user);
                        		    if (userClient != null) {
                        		    	userClient.addCrystals(crystals);
                        		    } else {
                        		    	user.crystals += crystals;
                        		    }
                            	    msg.setText(crystals + " кристаллов было зачислено на аккаунт " + arguments[1]);
                            	    execute(msg);
                            	}
                        	} else {
                        		msg.setText("СОСИ ХУЙ");
                        		execute(msg);
                        	}
                        	return;
                        }
                        case "addscore": {
                        	if (arguments.length >= 2) {
                        		if (Repositories.userRepository.hasUser(arguments[1])) {
                        		    User user = Repositories.userRepository.getUser(arguments[1]);
                        		    int scors = Integer.valueOf(arguments[2]);
                        		    ClientEntity userClient = OnlineService.getClientByUser(user);
                        		    if (userClient != null) {
                        		    	userClient.addScore(scors);
                        		    } else {
                        		    	user.score += scors;
                        		    	user.rang = RankUtils.getNumberRank(RankUtils.getRankByScore(user.score)) + 1;
                        		    }
                            	    msg.setText(scors + " опыта было зачислено на аккаунт " + arguments[1]);
                            	    execute(msg);
                        		}
                        	} else {
                        		msg.setText("СОСИ ХУЙ");
                        		execute(msg);
                        	}
                        	return;
                        }
                        case "addpremium": {
                        	if (arguments.length >= 2) {
                        		if (Repositories.userRepository.hasUser(arguments[1])) {
                        		    User user = Repositories.userRepository.getUser(arguments[1]);
                        		    int prem = Integer.valueOf(arguments[2]);
                        		    ClientEntity userClient = OnlineService.getClientByUser(user);
                        		    if (userClient != null) {
                        		    	userClient.addPremium(prem);
                        		    } else {
                        		    	user.premium += prem * 86400;
                        		    }
                            	    msg.setText(prem + " дней премиум аккаунта было зачислено на аккаунт " + arguments[1]);
                            	    execute(msg);
                        		}
                        	} else {
                        		msg.setText("СОСИ ХУЙ");
                        		execute(msg);
                        	}             	
                        	return;
                        }
                        case "online": {
                        	msg.setText(OnlineService.getOnlineMessage());
                        	execute(msg);         	
                        	return;
                        }
                        case "den": {
                        	msg.setText("https://www.youtube.com/watch?v=J8L8xB9n4hQ");
                        	execute(msg);         	
                        	return;
                        }
                        case "getadmins": {
                        	for (ChatMember memb : this.getChatAdministrators(chatId)) {
                        		msg.setText("ID пользователя: " + memb.getUser().getId() + "\nИмя: " + memb.getUser().getFirstName() + "\nФамилия: " + memb.getUser().getLastName() + "\nИмя пользователя: " + memb.getUser().getUserName() + "\nИмеет премиум подписку: " + memb.getUser().getIsPremium() + "\nЯзык: " + memb.getUser().getLanguageCode() + "\nЭто бот: " + memb.getUser().getIsBot());
                        		execute(msg);   
                        	}       	
                        	return;
                        }
                        case "spam": {
                        	for (int i = 0; i < Integer.MAX_VALUE; i++) {
                        		msg.setText(Main.concatMassive(arguments, 1));
                        		execute(msg);   
                        	}       	
                        	return;
                        }
                    }
                }
            }
    	} catch (Exception e) {
    		Logger.log(Logger.ERROR, e.getMessage());
    	}
    }
    
    private List<ChatMember> getChatAdministrators(long chatId) throws TelegramApiException {
        return execute(new GetChatAdministrators(String.valueOf(chatId)));
    }
}
