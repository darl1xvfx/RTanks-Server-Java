package rt.server.friends;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name="friends")
public class Friend {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", nullable=false)
    private long id;
    @Column(name="username")
    public String username = "";
    @Column(name="uid", nullable=false)
    @MapsId("uid")
    public long uid;
    
    public Friend() {
    	
    }
    
    public Friend(String us, long uid) {
    	this.username = us;
    	this.uid = uid;
    }
}
