package rt.server.clans;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="users")
public class Clan {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long id;
    @Column(name="name", unique=true, nullable=false)
    public String name;
    @Column(name="tag", unique=true, nullable=false)
    public String tag;
    @Column(name="flagId", nullable=false)
    public int flagId;
    @Column(name="creatorId", unique=true, nullable=false)
    public String creatorId;
    
    public Clan() {
    	
    }
    
    public Clan(String name, String tag, int flagId, String creatorId) {
    	this.name = name;
    	this.tag = tag;
    	this.flagId = flagId;
    	this.creatorId = creatorId;
    }
}
