package rt.server.garage.lootbox;

public class LootboxPrize {
    private String category;
    private int count;
    private int preview;
    private String name;

    public LootboxPrize(String category, int count, int preview, String name) {
        this.category = category;
        this.count = count;
        this.preview = preview;
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public int getCount() {
        return count;
    }

    public int getPreview() {
        return preview;
    }

    public String getName() {
        return name;
    }
}