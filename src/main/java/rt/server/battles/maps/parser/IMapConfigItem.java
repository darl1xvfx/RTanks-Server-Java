package rt.server.battles.maps.parser;

public class IMapConfigItem
{
    public String id;
    public String name;
    public String skyboxId;
    public String ambientSoundId;
    public String gameMode;
    public String themeName;
    public int previewId;
    public int mapResource;
    public int minRank;
    public int maxRank;
    public int maxPlayers;
    public boolean tdm;
    public boolean ctf;
    public boolean dom;
    
    public IMapConfigItem(final String id, final String name, final String skyboxId, final int minRank, final int maxRank, final int maxPlayers, final boolean tdm, final boolean ctf, final boolean dom, final int previewId, final int mapResource) {
        this.tdm = false;
        this.ctf = false;
        this.dom = false;
        this.id = id;
        this.name = name;
        this.skyboxId = skyboxId;
        this.minRank = minRank;
        this.maxRank = maxRank;
        this.tdm = tdm;
        this.ctf = ctf;
        this.dom = dom;
        this.maxPlayers = maxPlayers;
        this.previewId = previewId;
        this.mapResource = mapResource;
    }
    
    public IMapConfigItem(final String id, final String name, final String skyboxId, final int minRank, final int maxRank, final int maxPlayers, final boolean tdm, final boolean ctf, final boolean dom, final int previewId, final int mapResource, final String soundId, final String gamemodeId) {
        this.tdm = false;
        this.ctf = false;
        this.dom = false;
        this.id = id;
        this.name = name;
        this.skyboxId = skyboxId;
        this.minRank = minRank;
        this.maxRank = maxRank;
        this.tdm = tdm;
        this.ctf = ctf;
        this.dom = dom;
        this.maxPlayers = maxPlayers;
        this.ambientSoundId = soundId;
        this.gameMode = gamemodeId;
        this.previewId = previewId;
        this.mapResource = mapResource;
    }
}
