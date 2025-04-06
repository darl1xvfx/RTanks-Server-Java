//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package rt.server.services.ban;

public class BanChat {
    public static final String BAN_FIVE_MINUTES = "banminutes";
    public static final String BAN_ONE_HOUR = "banhour";
    public static final String BAN_ONE_DAY = "banday";
    public static final String BAN_ONE_WEEK = "banweek";
    public static final String BAN_ONE_MONTH = "banmonth";
    public static final String BAN_HALF_YEAR = "banhalfyear";
    public static final String BAN_FOREVER = "banforever";

    public BanChat() {
    }

    public static BanTimeType getTimeType(String cmd) {
        BanTimeType time = null;
        switch (cmd) {
            case "banmonth":
                time = BanTimeType.ONE_MONTH;
                break;
            case "banminutes":
                time = BanTimeType.FIVE_MINUTES;
                break;
            case "banday":
                time = BanTimeType.ONE_DAY;
                break;
            case "banhour":
                time = BanTimeType.ONE_HOUR;
                break;
            case "banweek":
                time = BanTimeType.ONE_WEEK;
                break;
            case "banforever":
                time = BanTimeType.FOREVER;
                break;
            case "banhalfyear":
                time = BanTimeType.HALF_YEAR;
        }

        return time;
    }
}
