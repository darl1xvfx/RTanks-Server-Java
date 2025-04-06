package rt.server.battles.maps.parser.parser.map.bonus;

import rt.server.battles.maps.parser.parser.*;
import java.util.*;
import jakarta.xml.bind.annotation.*;
import rt.server.math.*;

public class BonusRegion
{
    private Vector3d max;
    private Vector3d min;
    private ArrayList<BonusType> type;
    
    public BonusRegion() {
        this.type = new ArrayList<BonusType>();
    }
    
    public Vector3d getMax() {
        return this.max;
    }
    
    @XmlElement(name = "max")
    public void setMax(final Vector3d max) {
        this.max = max;
    }
    
    public Vector3d getMin() {
        return this.min;
    }
    
    @XmlElement(name = "min")
    public void setMin(final Vector3d min) {
        this.min = min;
    }
    
    @XmlElement(name = "bonus-type")
    public void setBonusType(final String value) {
        this.type.add(BonusType.getType(value));
    }
    
    public ArrayList<BonusType> getType() {
        return this.type;
    }
    
    @Override
    public String toString() {
        return "BONUS-REGION[TYPE = " + this.type + "] max: " + this.max + " min: " + this.min;
    }
    
    public rt.server.battles.bonus.BonusRegion toServerBonusRegion() {
        final String[] convert = new String[this.type.size()];
        for (int i = 0; i < this.type.size(); ++i) {
            convert[i] = this.type.get(i).getValue();
        }
        return new rt.server.battles.bonus.BonusRegion(this.toVector3(this.max), this.toVector3(this.min), convert);
    }
    
    public Vector3 toVector3(final Vector3d v) {
        return new Vector3(v.getX(), v.getY(), v.getZ());
    }
}
