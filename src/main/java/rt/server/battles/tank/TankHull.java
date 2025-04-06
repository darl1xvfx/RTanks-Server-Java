package rt.server.battles.tank;

import rt.server.services.resource.Resource;
import org.json.JSONObject;
import rt.server.user.Equipment;
import rt.server.garage.mountable.HullItemsDataParser;

public class TankHull {
    public float speed;
    public float turnSpeed;
    public float acceleration;
    public float reverseAcceleration;
    public float sideAcceleration;
    public float turnAcceleration;
    public float reverseTurnAcceleration;
    public float damping;
    public float mass;
    public float power;
    public float hp;
    public int object3ds;

    public TankHull(float speed, float turnSpeed, float acceleration, float reverseAcceleration,
                    float sideAcceleration, float turnAcceleration, float reverseTurnAcceleration,
                    float damping, float mass, float power, float hp, int object3ds) {
        this.speed = speed;
        this.turnSpeed = turnSpeed;
        this.acceleration = acceleration;
        this.reverseAcceleration = reverseAcceleration;
        this.sideAcceleration = sideAcceleration;
        this.turnAcceleration = turnAcceleration;
        this.reverseTurnAcceleration = reverseTurnAcceleration;
        this.damping = damping;
        this.mass = mass;
        this.power = power;
        this.hp = hp;
        this.object3ds = object3ds;
    }

    public static TankHull fromJson(String baseHullName, Equipment equipment) {
        try {
            // Загружаем базовый файл корпуса (без модификации в имени файла)
            String jsonString = Resource.fileToString("garage/items/hull/" + baseHullName + ".json");
            JSONObject physicsJson = getJsonObject(baseHullName, equipment, jsonString);

            String modification = equipment.getHullModification();
            HullItemsDataParser.HullItemData hullData = HullItemsDataParser.get(modification);
            int object3ds = (hullData != null) ? hullData.object3ds : 0;

            return new TankHull(
                    (float) physicsJson.getDouble("speed"),
                    (float) physicsJson.getDouble("turnSpeed"),
                    (float) physicsJson.getDouble("acceleration"),
                    (float) physicsJson.getDouble("reverseAcceleration"),
                    (float) physicsJson.getDouble("sideAcceleration"),
                    (float) physicsJson.getDouble("turnAcceleration"),
                    (float) physicsJson.getDouble("reverseTurnAcceleration"),
                    (float) physicsJson.getDouble("damping"),
                    (float) physicsJson.getDouble("mass"),
                    (float) physicsJson.getDouble("power"),
                    (float) physicsJson.getDouble("hp"),
                    object3ds
            );
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка парсинга конфигурации корпуса для " + baseHullName, e);
        }
    }

    private static JSONObject getJsonObject(String baseHullName, Equipment equipment, String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            throw new IllegalStateException("Не удалось загрузить конфигурацию корпуса для " + baseHullName);
        }
        JSONObject json = new JSONObject(jsonString);
        String modification = equipment.getHullModification();
        JSONObject modJson = json.optJSONObject(modification);
        if (modJson == null) {
            throw new IllegalStateException("Модификация " + modification + " не найдена в файле " + baseHullName + ".json");
        }
        return modJson.has("physics") ? modJson.getJSONObject("physics") : modJson;
    }
}