package rt.server.battles.maps.parser;

import rt.server.battles.maps.parser.parser.Parser;
import rt.server.battles.maps.parser.parser.map.bonus.BonusRegion;
import rt.server.battles.maps.parser.parser.map.spawn.SpawnPosition;
import rt.server.battles.maps.parser.parser.map.spawn.SpawnPositionType;
import rt.server.logger.Logger;
import rt.server.math.Vector3;
import rt.server.services.resource.Resource;
import jakarta.xml.bind.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import java.util.*;
import java.io.*;

public class MapsLoader
{
    public static HashMap<String, Map> maps;
    private static ArrayList<IMapConfigItem> configItems;
    private static Parser parser;
    
    static {
        MapsLoader.maps = new HashMap<String, Map>();
        MapsLoader.configItems = new ArrayList<IMapConfigItem>();
    }
    
    public static void initFactoryMaps() {
        Logger.log(Logger.INFO, "Loading maps...");
        MapsLoader.parser = new Parser();
        loadConfig();
    }
    
    private static void loadConfig() {
        try {
            final JSONParser mapsParser = new JSONParser();
            final Object items = mapsParser.parse(new FileReader(Resource.get("maps/config.json").toFile()));
            final JSONObject obj = (JSONObject)items;
            final JSONArray jarray = (JSONArray) obj.get("maps");
            for (final Object objItem : jarray) {
                final JSONObject item = (JSONObject)objItem;
                final String id = (String) item.get("id");
                final String name = (String) item.get("name");
                final String skyboxId = (String) item.get("skybox_id");
                final Object ambientSoundId = item.get("ambient_sound_id");
                final Object gameModeId = item.get("gamemode_id");
                final int minRank = Integer.parseInt((String) item.get("min_rank"));
                final int maxRank = Integer.parseInt((String) item.get("max_rank"));
                final int maxPlayers = Integer.parseInt((String) item.get("max_players"));
                final int previewId = Integer.parseInt((String) item.get("preview_id"));
                final int mapResource = Integer.parseInt((String) item.get("map_resource"));
                final boolean tdm = (boolean) item.get("tdm");
                final boolean ctf = (boolean) item.get("ctf");
                final boolean dom = (boolean) item.get("dom");
                final Object themeId = item.get("theme_id");
                final IMapConfigItem __item = (ambientSoundId == null || gameModeId == null) ? new IMapConfigItem(id, name, skyboxId, minRank, maxRank, maxPlayers, tdm, ctf, dom, previewId, mapResource) : new IMapConfigItem(id, name, skyboxId, minRank, maxRank, maxPlayers, tdm, ctf, dom, previewId, mapResource, (String)ambientSoundId, (String)gameModeId);
                if (themeId != null) {
                    __item.themeName = (String)themeId;
                }
                MapsLoader.configItems.add(__item);
            }
            parseMaps();
        }
        catch (ParseException | IOException ex2) {
            ex2.printStackTrace();
        }
    }
    
    private static void parseMaps() {
        final File[] maps = Resource.get("maps").toFile().listFiles();
        File[] array;
        for (int length = (array = maps).length, i = 0; i < length; ++i) {
            final File file = array[i];
            if (!file.isDirectory() && file.getName().endsWith(".xml")) {
                parse(file);
            }
        }
        Logger.log(Logger.INFO, "Loaded all maps!\n");
    }
    
    private static void parse(final File file) {
    	Logger.log(Logger.INFO, "Loading " + file.getName() + "...");
        final IMapConfigItem temp = getMapItem(file.getName().substring(0, file.getName().length() - 4));
        if (temp == null) {
            return;
        }
        Map map = null;
        try {
            map = new Map() {
                {
                    this.name = temp.name;
                    this.id = temp.id;
                    this.skyboxId = temp.skyboxId;
                    this.minRank = temp.minRank;
                    this.maxRank = temp.maxRank;
                    this.maxPlayers = temp.maxPlayers;
                    this.tdm = temp.tdm;
                    this.ctf = temp.ctf;
                    this.dom = temp.dom;
                    this.md5Hash = "";
                    this.mapTheme = null;
                    this.themeId = temp.themeName;
                    this.previewId = temp.previewId;
                    this.mapResource = temp.mapResource;
                }
            };
        }
        catch (Exception e1) {
            e1.printStackTrace();
        }
        rt.server.battles.maps.parser.parser.map.Map parsedMap = null;
        try {
            parsedMap = MapsLoader.parser.parseMap(file);
        }
        catch (JAXBException e2) {
            e2.printStackTrace();
        }
        for (final SpawnPosition sp : parsedMap.getSpawnPositions()) {
            if (sp.getSpawnPositionType() == SpawnPositionType.NONE) {
                map.spawnPositonsDM.add(sp.getVector3());
            }
            if (sp.getSpawnPositionType() == SpawnPositionType.RED) {
                map.spawnPositonsRed.add(sp.getVector3());
            }
            if (sp.getSpawnPositionType() == SpawnPositionType.BLUE) {
                map.spawnPositonsBlue.add(sp.getVector3());
            }
        }
        if (parsedMap.getBonusesRegion() != null) {
            for (final BonusRegion br : parsedMap.getBonusesRegion()) {
                for (final rt.server.battles.maps.parser.parser.map.bonus.BonusType type : br.getType()) {
                    if (type == rt.server.battles.maps.parser.parser.map.bonus.BonusType.CRYSTALL_100) {
                        map.goldsRegions.add(br.toServerBonusRegion());
                    }
                    else if (type == rt.server.battles.maps.parser.parser.map.bonus.BonusType.CRYSTALL) {
                        map.crystallsRegions.add(br.toServerBonusRegion());
                    }
                    else if (type == rt.server.battles.maps.parser.parser.map.bonus.BonusType.ARMOR) {
                        map.armorsRegions.add(br.toServerBonusRegion());
                    }
                    else if (type == rt.server.battles.maps.parser.parser.map.bonus.BonusType.DAMAGE) {
                        map.damagesRegions.add(br.toServerBonusRegion());
                    }
                    else if (type == rt.server.battles.maps.parser.parser.map.bonus.BonusType.HEAL) {
                        map.healthsRegions.add(br.toServerBonusRegion());
                    }
                    else {
                        if (type != rt.server.battles.maps.parser.parser.map.bonus.BonusType.NITRO) {
                            continue;
                        }
                        map.nitrosRegions.add(br.toServerBonusRegion());
                    }
                }
            }
        }
        map.flagBluePosition = ((parsedMap.getPositionBlueFlag() != null) ? parsedMap.getPositionBlueFlag().toVector3() : null);
        map.flagRedPosition = ((parsedMap.getPositionRedFlag() != null) ? parsedMap.getPositionRedFlag().toVector3() : null);
        if (map.flagBluePosition != null) {
            final Vector3 flagBluePosition = map.flagBluePosition;
            flagBluePosition.z += 50.0f;
            final Vector3 flagRedPosition = map.flagRedPosition;
            flagRedPosition.z += 50.0f;
        }
        if(parsedMap.getPoints() != null) {
			map.cpKeypoints = parsedMap.getDOMKeypoints();
		}
        MapsLoader.maps.put(map.id, map);
    }
    
    private static IMapConfigItem getMapItem(final String id) {
        for (final IMapConfigItem item : MapsLoader.configItems) {
            if (item.id.equals(id)) {
                return item;
            }
        }
        return null;
    }
}
