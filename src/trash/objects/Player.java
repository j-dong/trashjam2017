package trash.objects;

import java.util.ArrayList;

import trash.Application;
import trash.states.Game;
import trash.util.AABB;

public class Player {
    public static final int IMAGE_WIDTH = 60, IMAGE_HEIGHT = 75;
    public static final int HITBOX_X = 10, HITBOX_Y = 10;
    public static final int HITBOX_WIDTH = 40, HITBOX_HEIGHT = IMAGE_HEIGHT - HITBOX_Y;

    // vx is multiplied by this when we hit the ground
    public static final double INSTANTANEOUS_FRICTION = 0.5;
    public static final double RUN_SPEED = 4.0;
    public static final double RUN_ACCEL = 1.0;
    // position
    private double x, y;
    // velocity
    private double vx, vy;

    public Player() {
    }

    public void init(int groundY) {
        x = Game.PLAYER_X - HITBOX_X;
        y = groundY - IMAGE_HEIGHT;
    }

    public AABB getHitbox() {
        return new AABB(x + HITBOX_X, y + HITBOX_Y,
                        x + HITBOX_X + HITBOX_WIDTH,
                        y + HITBOX_Y + HITBOX_HEIGHT);
    }

    public float getDrawX() {
        return (float)x;
    }

    public float getDrawY() {
        return (float)y;
    }

    public void move(ArrayList<Building> buildings) {
        x += vx;
        y += vy;
        vy += Game.GRAVITY;
        double groundY = Application.HEIGHT;
        for (Building b : buildings) {
            if (true /* our x is in the building */) {
                groundY = Math.min(b.getY(), groundY);
            }
        }
        if (y + IMAGE_HEIGHT > groundY) {
            vy = 0;
            vx *= INSTANTANEOUS_FRICTION;
            y = groundY - IMAGE_HEIGHT;
        }
        if (y + IMAGE_HEIGHT + 1 >= groundY) {
            // if on ground
            // run towards player x
            double target_vel = Math.copySign(RUN_SPEED, Game.PLAYER_X - x);
            vx += Math.copySign(RUN_ACCEL, target_vel - vx);
        }
    }
}
