package rt.server.challenges;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Random;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import rt.server.logger.Logger;
import rt.server.services.resource.Resource;

public class QuestsParser {
	
	public static ArrayList<QuestEntity> easyQuests = new ArrayList<QuestEntity>();
	public static ArrayList<QuestEntity> normalQuests = new ArrayList<QuestEntity>();
	public static ArrayList<QuestEntity> hardQuests = new ArrayList<QuestEntity>();
	
    public static void parse() { //TODO(TitanoMachina) парсер заданий (хуйня ебаная)
    	File easyQuestsFolder = Resource.get("challenges/easy").toFile();
    	File normalQuestsFolder = Resource.get("challenges/normal").toFile();
    	File hardQuestsFolder = Resource.get("challenges/hard").toFile();
    	for (File questFile : easyQuestsFolder.listFiles()) {
    		try {
				JSONObject quest = (JSONObject) new JSONParser().parse(new String(Files.readAllBytes(questFile.toPath())));
				easyQuests.add(questEntityFromObject(quest));
				Logger.log(Logger.INFO, "Loaded easy quest: " + questFile.getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	for (File questFile : normalQuestsFolder.listFiles()) {
    		try {
				JSONObject quest = (JSONObject) new JSONParser().parse(new String(Files.readAllBytes(questFile.toPath())));
				normalQuests.add(questEntityFromObject(quest));
				Logger.log(Logger.INFO, "Loaded normal quest: " + questFile.getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	for (File questFile : hardQuestsFolder.listFiles()) {
    		try {
				JSONObject quest = (JSONObject) new JSONParser().parse(new String(Files.readAllBytes(questFile.toPath())));
				hardQuests.add(questEntityFromObject(quest));
				Logger.log(Logger.INFO, "Loaded hard quest: " + questFile.getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    }

	private static QuestEntity questEntityFromObject(JSONObject quest) {
		int image = (int)(long)quest.get("image");
		String level = (String)quest.get("level");
		ArrayList<String> prizes = (ArrayList<String>)quest.get("prizes");
		int targetProgress = (int)(long)quest.get("target_progress");
		String description = (String)quest.get("description");
		String id = (String)quest.get("id");
		return (new QuestEntity(image, level, prizes, targetProgress, description, id));
	}
	
	public static QuestEntity getRandomEasyQuest() {
		return easyQuests.get(new Random().nextInt(easyQuests.size()));
	}
	
	public static QuestEntity getRandomNormalQuest() {
		return normalQuests.get(new Random().nextInt(normalQuests.size()));
	}
	
	public static QuestEntity getRandomHardQuest() {
		return hardQuests.get(new Random().nextInt(hardQuests.size()));
	}
	
	public static QuestEntity getQuestById(String quest) {
		for (QuestEntity easy : easyQuests) {
			if (easy.id.equals(quest)) {
				return easy;
			}
		}
		for (QuestEntity normal : normalQuests) {
			if (normal.id.equals(quest)) {
				return normal;
			}
		}
		for (QuestEntity hard : hardQuests) {
			if (hard.id.equals(quest)) {
				return hard;
			}
		}
		return null;
	}
	
	public static String getQuestLevelById(String quest) {
		for (QuestEntity easy : easyQuests) {
			if (easy.id.equals(quest)) {
				return "easy";
			}
		}
		for (QuestEntity normal : normalQuests) {
			if (normal.id.equals(quest)) {
				return "normal";
			}
		}
		for (QuestEntity hard : hardQuests) {
			if (hard.id.equals(quest)) {
				return "hard";
			}
		}
		return null;
	}
}
