package rt.server;

import java.util.Timer;
import java.util.TimerTask;
import rt.server.battles.BattleModel;
import rt.server.battles.BattleProcessor;
import rt.server.battles.maps.parser.MapsLoader;
import rt.server.battles.system.SystemBattles;
import rt.server.battles.weapons.WeaponUtils;
import rt.server.challenges.QuestsParser;
import rt.server.challenges.TiersParser;
import rt.server.database.HibernateUtils;
import rt.server.database.Repositories;
import rt.server.discord.bot.DiscordBot;
import rt.server.garage.GarageItemParser;
import rt.server.garage.mountable.HullItemsDataParser;
import rt.server.garage.mountable.MountableItemsDataParser;
import rt.server.lobbychat.Channels;
import rt.server.lobbychat.censore.CensoreModel;
import rt.server.localization.LocalizationLoader;
import rt.server.logger.Logger;
import rt.server.services.OnlineService;
import rt.server.services.protocol.commands.Command;
import rt.server.services.protocol.commands.Commands;
import rt.server.services.socialnetworks.SocialNetworkModel;
import rt.server.telegram.bot.TelegramBot;
import rt.server.utils.GraphicUtils;
import rt.server.utils.RankUtils;
import rt.server.utils.SkyboxUtils;

public class Main {

	public static void main(String[] args) {
		try {
			Logger.init();
			Logger.log(Logger.INFO, "こんにちは, TitanoMachina!");
			LocalizationLoader.init();
			SocialNetworkModel.init();
			CensoreModel.parse();
			MountableItemsDataParser.parse();
			HullItemsDataParser.parse();
			GarageItemParser.parse();
			QuestsParser.parse();
			TiersParser.parse();
			SkyboxUtils.parse();
			GraphicUtils.parse();
			MapsLoader.initFactoryMaps();
			Channels.init();
			RankUtils.init();
			HibernateUtils.setupSessionFactory();
			Repositories.init();
			BattleProcessor.init();
			SystemBattles.init();
			WeaponUtils.init();
			/*DiscordBot.start();
			TelegramBot.start();*/
			ResourceServer.start();
			GameServer.start();
		} catch (Exception e) {
			e.printStackTrace();
			Logger.log(Logger.ERROR, "Error during application startup: " + e.getMessage());
		}
	}

	public static void shutdown() {
		OnlineService.CLIENTS.forEach(client -> new Command(Commands.ServerHalt).send(client));
	    new Timer().schedule(new TimerTask() {
	        @Override public void run() {
	            BattleProcessor.battles.values().forEach(BattleModel::finishBattle);
	            new Timer().schedule(new TimerTask() {
	                @Override public void run() {
	                    GameServer.shutdown();
	                    ResourceServer.shutdown();
	                    System.exit(0);
	                }
	            }, 10000);
	        }
	    }, 40000);
	}

    public static String concatStrings(String... str) {
        StringBuffer sbf = new StringBuffer();
        String[] arrayOfString = str;
        int j = str.length;

        for(int i = 0; i < j; ++i) {
            String adder = arrayOfString[i];
            sbf.append(adder);
        }

        return sbf.toString();
    }

    public static String concatMassive(String[] src, int start) {
        StringBuffer sbf = new StringBuffer();

        for(int i = start; i < src.length; ++i) {
            sbf.append(src[i]);
            if (i != src.length - 1) {
                sbf.append(' ');
            }
        }

        return sbf.toString();
    }

}
