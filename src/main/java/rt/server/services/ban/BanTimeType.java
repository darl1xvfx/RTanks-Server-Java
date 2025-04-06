//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package rt.server.services.ban;

public class BanTimeType {
    public static final BanTimeType FIVE_MINUTES = new BanTimeType("НА 5 МИНУТ.", 12, 5);
    public static final BanTimeType ONE_HOUR = new BanTimeType("НА ЧАС.", 10, 1);
    public static final BanTimeType ONE_DAY = new BanTimeType("НА СУТКИ.", 5, 1);
    public static final BanTimeType ONE_WEEK = new BanTimeType("НА НЕДЕЛЮ.", 4, 1);
    public static final BanTimeType ONE_MONTH = new BanTimeType("НА МЕСЯЦ.", 2, 1);
    public static final BanTimeType HALF_YEAR = new BanTimeType("НА ПОЛ ГОДА.", 2, 6);
    public static final BanTimeType FOREVER = new BanTimeType("НАВСЕГДА.", 1, 2);
    private final String nameType;
    private int field;
    private int amount;

    private BanTimeType(String nameType, int field, int amount) {
        this.nameType = nameType;
        this.field = field;
        this.amount = amount;
    }

    public String getNameType() {
        return this.nameType;
    }

    public int getField() {
        return this.field;
    }

    public int getAmount() {
        return this.amount;
    }

    public String toString() {
        return "BanTimeType [" + this.nameType + "]";
    }

    public boolean equals(Object obj) {
        BanTimeType _obj;
        try {
            _obj = (BanTimeType)obj;
        } catch (Exception var4) {
            return false;
        }

        return this.getNameType().equals(_obj.getNameType());
    }
}
