package rt.server.battles.bonus;

public enum BonusType {
   HEALTH {
	   public String toString() {
		   return "health";
	   }
   },
   ARMORUP {
	   public String toString() {
		   return "armor";
	   }
   },
   DAMAGEUP {
	   public String toString() {
		   return "damage";
	   }
   },
   NITRO {
	   public String toString() {
		   return "nitro";
	   }
   },
   GOLD {
	   public String toString() {
		   return "gold";
	   }
   }
}
