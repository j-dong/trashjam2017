package trash.util;

public class AABB {
    public double x1, y1, x2, y2;

    public AABB(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public boolean intersects(AABB other) {
        return !(x2 < other.x1 || other.x2 < x1
              || y2 < other.y1 || other.y2 < y1);
    }
}
