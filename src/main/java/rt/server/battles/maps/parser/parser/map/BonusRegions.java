package rt.server.battles.maps.parser.parser.map;

import java.util.*;
import rt.server.battles.maps.parser.parser.map.bonus.*;
import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "bonus-regions")
class BonusRegions
{
    private List<BonusRegion> bonusRegions;
    
    public List<BonusRegion> getBonusRegions() {
        return this.bonusRegions;
    }
    
    @XmlElement(name = "bonus-region")
    public void setBonusRegions(final List<BonusRegion> bonusRegions) {
        this.bonusRegions = bonusRegions;
    }
}
