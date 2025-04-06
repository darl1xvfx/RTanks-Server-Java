package rt.server.challenges;

import java.util.ArrayList;

public class QuestEntity {
	public int image;
	public String level;
	public ArrayList<String> prizes;
	public int targetProgress;
    public int progress = 0;
    public String description;
    public String id;
    
	public QuestEntity(int image2, String level2, ArrayList<String> prizes2, int targetProgress2, String description2, String id2) {
		this.image = image2;
		this.level = level2;
		this.prizes = prizes2;
		this.targetProgress = targetProgress2;
		this.description = description2;
		this.id = id2;
	}
}
