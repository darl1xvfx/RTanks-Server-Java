package rt.server.battles.maps.parser.parser;

import rt.server.math.*;

public class Vector3d
{
    private float x;
    private float y;
    private float z;
    
    public Vector3d(final float x, final float y, final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector3d() {
    }
    
    public float getX() {
        return this.x;
    }
    
    public void setX(final float x) {
        this.x = x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public void setY(final float y) {
        this.y = y;
    }
    
    public float getZ() {
        return this.z;
    }
    
    public void setZ(final float z) {
        this.z = z;
    }
    
    public Vector3 toVector3() {
        return new Vector3(this.x, this.y, this.z);
    }
    
    @Override
    public String toString() {
        return "x = " + this.x + " y = " + this.y + " z = " + this.z;
    }
}
