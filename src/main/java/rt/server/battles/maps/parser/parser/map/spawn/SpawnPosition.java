package rt.server.battles.maps.parser.parser.map.spawn;

import rt.server.battles.maps.parser.parser.*;
import jakarta.xml.bind.annotation.*;
import rt.server.math.*;

@XmlRootElement(name = "spawn-point")
public class SpawnPosition
{
    private Vector3d position;
    private Vector3d rotation;
    private String type;
    
    public SpawnPosition() {
    }
    
    public SpawnPosition(final Vector3d position, final Vector3d rotation, final String type) {
        this.position = position;
        this.rotation = rotation;
        this.type = type;
    }
    
    @XmlElement(name = "position")
    public Vector3d getPosition() {
        return this.position;
    }
    
    public void setType(final String value) {
        this.type = value;
    }
    
    @XmlAttribute(name = "type")
    public String getType() {
        return this.type;
    }
    
    public SpawnPositionType getSpawnPositionType() {
        return SpawnPositionType.getType(this.type);
    }
    
    public void setPosition(final Vector3d position) {
        this.position = position;
    }
    
    public Vector3d getRotation() {
        return this.rotation;
    }
    
    public void setRotation(final Vector3d rotation) {
        this.rotation = rotation;
    }
    
    @Override
    public String toString() {
        return this.position + " direction:" + this.rotation;
    }
    
    public rt.server.battles.spawn.SpawnPosition toServerSpawnPosition() {
        return new rt.server.battles.spawn.SpawnPosition(this.toVector3(this.position), this.toVector3(this.rotation));
    }
    
    public Vector3 toVector3(final Vector3d v) {
        return new Vector3(v.getX(), v.getY(), v.getZ());
    }
    
    public Vector3 getVector3() {
        return new Vector3(this.position.getX(), this.position.getY(), this.position.getZ()) {
            {
                this.rot = SpawnPosition.this.rotation.getZ();
            }
        };
    }
}
