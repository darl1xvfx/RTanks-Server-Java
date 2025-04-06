package rt.server.garage;

public class ItemInfo {
    public String id;
    public int count;
    public boolean addable;

    public ItemInfo(String id, int count, boolean addable) {
        this.id = id;
        this.count = count;
        this.addable = addable;
    }
}
