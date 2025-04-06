package rt.server.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="daily_quests")
public class Quest {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    @Column(name="quest_id",nullable=false)
    public String questId;
    @ManyToOne
    public User user;
    @Column(name="target_progress",nullable=false)
	public int targetProgress;
    
    public Quest() {}
    
    public Quest(String qi, User user) {
    	this.questId = qi;
    	this.user = user;
    }

	public void setProgress(int min) {
		this.targetProgress = min;
	}

	public long getId() {
		return id;
	}
}
