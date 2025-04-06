package rt.server.battles.maps.parser.parser.map;

import rt.server.battles.maps.parser.parser.*;
import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "ctf-flags")
class FlagsPositions
{
    private Vector3d redFlag;
    private Vector3d blueFlag;
    
    public Vector3d getRedFlag() {
        return this.redFlag;
    }
    
    @XmlElement(name = "flag-red")
    public void setRedFlag(final Vector3d redFlag) {
        this.redFlag = redFlag;
    }
    
    public Vector3d getBlueFlag() {
        return this.blueFlag;
    }
    
    @XmlElement(name = "flag-blue")
    public void setBlueFlag(final Vector3d blueFlag) {
        this.blueFlag = blueFlag;
    }
    
    @Override
    public String toString() {
        return "red flag: " + this.redFlag + " blue: " + this.blueFlag;
    }
}
