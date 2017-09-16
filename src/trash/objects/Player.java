package trash.objects;

import java.util.ArrayList;

import trash.Application;
import trash.states.Game;
import trash.util.AABB;

public class Player {
    public static final int IMAGE_WIDTH = 60, IMAGE_HEIGHT = 75;
    public static final int HITBOX_X = 10, HITBOX_Y = 10;
    public static final int HITBOX_WIDTH = 40, HITBOX_HEIGHT = IMAGE_HEIGHT - HITBOX_Y;
    public static final int CENTER_X = HITBOX_X + HITBOX_WIDTH / 2;
    public static final int CENTER_Y = HITBOX_Y + HITBOX_HEIGHT / 2;
    
    public static final double TARGET_X = Game.PLAYER_X - HITBOX_X;

    // vx is multiplied by this when we hit the ground
    public static final double RUN_SPEED = 100.0;
    public static final double RUN_ACCEL = 1.0;
    public static final double JUMP_VEL = 8;
    public static final double RECOIL_VEL = 12;
    // position
    private double x, y;
    // velocity
    private double vx, vy;
    // inputs
    private boolean shouldJump;
    private boolean shouldShoot;
    private double shootAngle; // in radians

    public Player() {
    }

    public void init(int groundY) {
        x = TARGET_X;
        y = groundY - IMAGE_HEIGHT;
        shouldJump = false;
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

    public void setShouldJump(boolean b) {
        shouldJump = b;
    }
    
    public void shootAt(double x, double y) {
    	shootAngle = Math.atan2(y - (this.y + CENTER_Y), x - (this.x + CENTER_X));
    	shouldShoot = true;
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
            y = groundY - IMAGE_HEIGHT;
        }
        if (y + IMAGE_HEIGHT + 1 >= groundY) {
            // if on ground
            // run towards player x
            double target_vel = Math.copySign(RUN_SPEED, TARGET_X - x);
            if (RUN_SPEED > Math.abs(TARGET_X - x)) {
            	target_vel = TARGET_X - x;
            }
            double accel = Math.copySign(RUN_ACCEL, target_vel - vx);
            if (RUN_ACCEL > Math.abs(target_vel - vx))
            	vx = target_vel;
            else
            	vx += accel;
            if (shouldJump) {
                vy = -JUMP_VEL;
                shouldJump = false;
            }
        }
        if (shouldShoot) {
        	vx -= Math.cos(shootAngle) * RECOIL_VEL;
        	vy -= Math.sin(shootAngle) * RECOIL_VEL;
        	shouldShoot = false;
        }
    }
}
