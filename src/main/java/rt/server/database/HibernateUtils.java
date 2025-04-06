package rt.server.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import rt.server.ServerProperties;
import rt.server.services.ban.block.BlockGame;
import rt.server.clans.Clan;
import rt.server.friends.Friend;
import rt.server.friends.IncomingFriend;
import rt.server.friends.OutgoingFriend;
import rt.server.garage.OwnedGarageItem;
import rt.server.garage.mountable.Resistance;
import rt.server.logger.Logger;
import rt.server.user.Equipment;
import rt.server.user.Quest;
import rt.server.user.User;

public class HibernateUtils {
    private static SessionFactory sessionFactory;

    public static void setupSessionFactory() {
        Configuration config = getConfiguration();
        Logger.log(Logger.INFO, "Configured: " + config.getProperty("hibernate.connection.username") + ":" + config.getProperty("hibernate.connection.url"));
        sessionFactory = config.buildSessionFactory();
    }

    private static Configuration getConfiguration() {
        Configuration config = new Configuration();

        config.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        config.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/" + ServerProperties.HIBERNATE_DATABASE_NAME);
        config.setProperty("hibernate.connection.username", ServerProperties.HIBERNATE_CONNECTION_USERNAME);
        config.setProperty("hibernate.connection.password", ServerProperties.HIBERNATE_CONNECTION_PASSWORD);
        config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        config.setProperty("hibernate.show_sql", "false");
        config.setProperty("hibernate.format_sql", "true");
        config.setProperty("hibernate.hbm2ddl.auto", "update");
        
        config.addAnnotatedClass(User.class);
        config.addAnnotatedClass(Equipment.class);
        config.addAnnotatedClass(Friend.class);
        config.addAnnotatedClass(IncomingFriend.class);
        config.addAnnotatedClass(OutgoingFriend.class);
        config.addAnnotatedClass(Quest.class);
        config.addAnnotatedClass(BlockGame.class);
        config.addAnnotatedClass(OwnedGarageItem.class);
        config.addAnnotatedClass(Resistance.class);
        //config.addAnnotatedClass(Clan.class);
        
        return config;
    }

    public static Session getSession() {
        return sessionFactory.openSession();
    }
}
