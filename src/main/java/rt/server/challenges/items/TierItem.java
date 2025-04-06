package rt.server.challenges.items;

public class TierItem {
   public int preview;
   public int amount;
   public String name;
   public boolean received;
   
   public TierItem(int preview, int amount, String name, boolean received) {
	   this.preview = preview;
	   this.amount = amount;
	   this.name = name;
	   this.received = received;
   }
}
