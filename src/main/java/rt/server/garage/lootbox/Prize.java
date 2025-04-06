package rt.server.garage.lootbox;

import java.util.Objects;

public class Prize {
    private final String name;
    private final String rarity;
    private final int preview;
    private final String id;

    public Prize(String name, String rarity, int preview, String id) {
        this.name = name;
        this.rarity = rarity;
        this.preview = preview;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getRarity() {
        return rarity;
    }

    public int getPreview() {
        return preview;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Prize)) return false;
        Prize prize = (Prize) o;
        return preview == prize.preview &&
                Objects.equals(name, prize.name) &&
                Objects.equals(rarity, prize.rarity) &&
                Objects.equals(id, prize.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, rarity, preview, id);
    }

    @Override
    public String toString() {
        return "Prize{" +
                "name=" + name +
                ", rarity='" + rarity + '\'' +
                ", preview=" + preview +
                ", id='" + id + '\'' +
                '}';
    }
}