package rt.server;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerProperties {
    public static String IP = "127.0.0.1";
    public static int GAME_PORT = 6969;
    public static int RESOURCE_PORT = 8080;
    public static String DELIM_COMMANDS_SYMBOL = "end~";
    public static String DELIM_ARGUMENTS_SYMBOL = ";";
    public static boolean INVITES_ENABLED = false;
    public static String HIBERNATE_CONNECTION_USERNAME = "root";
    public static String HIBERNATE_CONNECTION_PASSWORD = "";
    public static String HIBERNATE_DATABASE_NAME = "fewtanki";
    public static boolean REGISTRATION_CAPTCHA_ENABLED = true;
    public static int GOLD_CRYSTALS_COUNT = 1000;
    public static int BILLBOARD_IMAGE = 96428;
	public static String DISCORD_TOKEN_BOT = "MTI3Mjg3MTE4NTI0MzMxMjEzOA.Gme9Js.pvGSckeH63SNelfI8gPp0vLRMeJTZi8BYTtLVk";
	public static String TELEGRAM_USERNAME_BOT = "fewtanki_bot";
	public static String TELEGRAM_TOKEN_BOT = "7206569883:AAFwg2UikNn5poTIFeDWYzgUCYSIHsUFNeg";
	public static List<String> UIDS_SUFFIX = new ArrayList<String>(Arrays.asList("_GTO", "_PRO", "_FLASH", "_TEST"));
	public static long MIN_FLOOD_TIMEOUT = 300L;
	public static int MIN_FLOOD_COUNT = 3;
	public static int MIN_DDOS_TIMEOUT = 3500;
	public static int MIN_DDOS_COUNT = 5;
    public static long CHALLENGES_TIME = Duration.between(LocalDateTime.now(), LocalDateTime.of(2025, 9, 5, 12, 0, 0)).toSeconds();
    public static boolean SUPERGOLDS_ENABLED = true;
	public static boolean METEOR_ENABLED = true;
}
