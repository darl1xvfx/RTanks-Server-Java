package rt.server.battles.maps.parser;

import rt.server.battles.maps.parser.parser.map.keypoints.CPKeypoint;
import java.util.*;
import rt.server.math.*;
import rt.server.battles.bonus.*;

public class Map
{
    public String name;
    public String id;
    public String skyboxId;
    public String themeId;
    public String mapTheme;
    public int previewId;
    public int mapResource;
    public int minRank;
    public int maxRank;
    public int maxPlayers;
    public boolean tdm;
    public boolean ctf;
    public boolean dom;
    public ArrayList<Vector3> spawnPositonsDM;
    public ArrayList<Vector3> spawnPositonsBlue;
    public ArrayList<Vector3> spawnPositonsRed;
    public ArrayList<BonusRegion> goldsRegions;
    public ArrayList<BonusRegion> crystallsRegions;
    public ArrayList<BonusRegion> healthsRegions;
    public ArrayList<BonusRegion> armorsRegions;
    public ArrayList<BonusRegion> damagesRegions;
    public ArrayList<BonusRegion> nitrosRegions;
    public List<CPKeypoint> cpKeypoints;
    public int totalCountDrops;
    public Vector3 flagRedPosition;
    public Vector3 flagBluePosition;
    public String md5Hash;
    
    public Map() {
        this.tdm = false;
        this.ctf = false;
        this.dom = false;
        this.spawnPositonsDM = new ArrayList<Vector3>();
        this.spawnPositonsBlue = new ArrayList<Vector3>();
        this.spawnPositonsRed = new ArrayList<Vector3>();
        this.goldsRegions = new ArrayList<BonusRegion>();
        this.crystallsRegions = new ArrayList<BonusRegion>();
        this.healthsRegions = new ArrayList<BonusRegion>();
        this.armorsRegions = new ArrayList<BonusRegion>();
        this.damagesRegions = new ArrayList<BonusRegion>();
        this.nitrosRegions = new ArrayList<BonusRegion>();
        this.cpKeypoints = new ArrayList<CPKeypoint>();
    }
    
    public Map(final String name, final String id, final String skyboxId, final ArrayList<Vector3> spawnPositionsDM, final ArrayList<Vector3> spawnPositionsBlue, final ArrayList<Vector3> spawnPositionsRed, final ArrayList<BonusRegion> goldsRegions, final ArrayList<BonusRegion> crystallsRegions, final ArrayList<BonusRegion> dropRegions, final int min, final int max, final int maxPlayers, final boolean tdm, final boolean ctf, final boolean dom, final int previewId, final int mapResource) {
        this.tdm = false;
        this.ctf = false;
        this.dom = false;
        this.spawnPositonsDM = new ArrayList<Vector3>();
        this.spawnPositonsBlue = new ArrayList<Vector3>();
        this.spawnPositonsRed = new ArrayList<Vector3>();
        this.goldsRegions = new ArrayList<BonusRegion>();
        this.crystallsRegions = new ArrayList<BonusRegion>();
        this.healthsRegions = new ArrayList<BonusRegion>();
        this.armorsRegions = new ArrayList<BonusRegion>();
        this.damagesRegions = new ArrayList<BonusRegion>();
        this.nitrosRegions = new ArrayList<BonusRegion>();
        this.cpKeypoints = new ArrayList<CPKeypoint>();
        this.name = name;
        this.id = id;
        this.skyboxId = skyboxId;
        this.spawnPositonsDM = spawnPositionsDM;
        this.spawnPositonsBlue = spawnPositionsBlue;
        this.spawnPositonsRed = spawnPositionsRed;
        this.goldsRegions = goldsRegions;
        this.crystallsRegions = crystallsRegions;
        this.minRank = min;
        this.maxRank = max;
        this.tdm = tdm;
        this.ctf = ctf;
        this.dom = dom;
        this.maxPlayers = maxPlayers;
        this.previewId = previewId;
        this.mapResource = mapResource;
    }
}
