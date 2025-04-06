package rt.server.friends;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name="outgoing_friends")
public class OutgoingFriend {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", nullable=false)
    public long id;
    @Column(name="username")
    public String username = "";
    @Column(name="uid", nullable=false)
    @MapsId("uid")
    public long uid;
    
    public OutgoingFriend() {
    	
    }
    
    public OutgoingFriend(String us, long uid) {
    	this.username = us;
    	this.uid = uid;
    }
}
