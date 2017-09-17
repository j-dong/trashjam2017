package trash.objects;

import trash.Application;
import trash.util.AABB;

public class Building {
    private double x1, x2, y;

    public Building(double x1, double x2, double y) {
        this.x1 = x1;
        this.x2 = x2;
        this.y = y;
    }

    public void move(int amount) {
        x1 -= amount;
        x2 -= amount;
    }

    public double getX1() {
        return x1;
    }

    public double getX2() {
        return x2;
    }

    public double getY() {
        return y;
    }

    public AABB getHitbox() {
        return new AABB(x1, y, x2, Application.HEIGHT);
    }
}
