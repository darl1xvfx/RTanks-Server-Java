package rt.server.services.ban.block;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="blocked_users")
public class BlockGame {
   @Id
   @GeneratedValue(strategy=GenerationType.IDENTITY)
   private long id;
   @Column(unique=true,nullable=false)
   public String username;
   @Column(nullable=false)
   public int reason;
   
   public BlockGame() {
	   
   }
   
   public BlockGame(String u, int r) {
	   this.username = u;
	   this.reason = r;
   }
}
