package rt.server.battles.statistics;

public class UserStatistics {
   public int score;
   public int kills;
   public int deaths;
   
   public UserStatistics(int score, int kills, int deaths) {
	   this.score = score;
	   this.kills = kills;
	   this.deaths = deaths;
   }
}
