package trash.objects;

import java.util.ArrayList;

import trash.Application;
import trash.util.AABB;

public class Bullet {
    public static final int IMAGE_WIDTH = 30, IMAGE_HEIGHT = 30;
    public static final int HITBOX_X = 0, HITBOX_Y = 0;
    public static final int HITBOX_WIDTH = 30, HITBOX_HEIGHT = 30;
    public static final int CENTER_X = HITBOX_X + HITBOX_WIDTH / 2;
    public static final int CENTER_Y = HITBOX_Y + HITBOX_HEIGHT / 2;

    public static final double VELOCITY = 10;

    private double x, y, vx, vy;
    private int life = Application.FPS * 3;

    private boolean dead;

    public Bullet(double x, double y, double angle) {
        this.x = x;
        this.y = y;
        this.vx = Math.cos(angle) * VELOCITY;
        this.vy = Math.sin(angle) * VELOCITY;
    }

    public void move() {
        x += vx;
        y += vy;
        life--;
    }

    public void collide(ArrayList<Building> buildings) {
        AABB hitbox = getHitbox();
        for (Building b : buildings) {
            if (b.getHitbox().intersects(hitbox)) {
                dead = true;
                break;
            }
        }
        if (hitbox.y1 > Application.HEIGHT || hitbox.x2 < -1000 || hitbox.y2 < -1000) {
            dead = true;
        }
        if (life <= 0)
            dead = true;
    }

    public float getDrawX() {
        return (float)x;
    }
    public float getDrawY() {
        return (float)y;
    }

    public AABB getHitbox() {
        return new AABB(x + HITBOX_X, y + HITBOX_Y,
                        x + HITBOX_X + HITBOX_WIDTH,
                        y + HITBOX_Y + HITBOX_HEIGHT);
    }

    public boolean shouldExplodeAndDie() {
        return dead;
    }
}
