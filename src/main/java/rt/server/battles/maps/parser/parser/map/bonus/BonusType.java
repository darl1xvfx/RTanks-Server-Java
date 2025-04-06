package rt.server.battles.maps.parser.parser.map.bonus;

public class BonusType
{
    public static final BonusType NITRO;
    public static final BonusType DAMAGE;
    public static final BonusType ARMOR;
    public static final BonusType HEAL;
    public static final BonusType CRYSTALL;
    public static final BonusType CRYSTALL_100;
    private String value;
    
    static {
        NITRO = new BonusType("nitro");
        DAMAGE = new BonusType("damageup");
        ARMOR = new BonusType("armorup");
        HEAL = new BonusType("medkit");
        CRYSTALL = new BonusType("crystal");
        CRYSTALL_100 = new BonusType("crystal_100");
    }
    
    private BonusType(final String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public static BonusType getType(final String value) {
        if (value.equals("medkit")) {
            return BonusType.HEAL;
        }
        if (value.equals("armorup")) {
            return BonusType.ARMOR;
        }
        if (value.equals("damageup")) {
            return BonusType.DAMAGE;
        }
        if (value.equals("nitro")) {
            return BonusType.NITRO;
        }
        if (value.equals("crystal")) {
            return BonusType.CRYSTALL;
        }
        if (value.equals("crystal_100")) {
            return BonusType.CRYSTALL_100;
        }
        return null;
    }
}
