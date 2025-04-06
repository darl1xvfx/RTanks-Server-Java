package rt.server.math;

public class Vector3
{
    public float x;
    public float y;
    public float z;
    public double rot;
    
    public Vector3(final float x, final float y, final float z) {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        this.rot = 0.0;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public double distanceTo(final Vector3 to) {
        return Math.sqrt(this.pow2(this.x - to.x) + this.pow2(this.y - to.y) + this.pow2(this.z - to.z));
    }
    
    public double distanceToWithoutZ(final Vector3 to) {
        return Math.sqrt(this.pow2(this.x - to.x) + this.pow2(this.y - to.y));
    }
    
    private double pow2(final double value) {
        return Math.pow(value, 2.0);
    }
}
