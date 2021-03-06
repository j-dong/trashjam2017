package trash.objects;

import java.util.ArrayList;

import trash.Application;
import trash.states.Game;
import trash.util.AABB;
//todo: add knockback while invuln so can't run through enemy with invuln
public class Player {
    public static final int IMAGE_WIDTH = 60, IMAGE_HEIGHT = 75;
    public static final int HITBOX_X = 20, HITBOX_Y = 10;
    public static final int HITBOX_WIDTH = IMAGE_WIDTH - HITBOX_X * 2, HITBOX_HEIGHT = IMAGE_HEIGHT - HITBOX_Y;
    public static final int CENTER_X = HITBOX_X + HITBOX_WIDTH / 2;
    public static final int CENTER_Y = HITBOX_Y + HITBOX_HEIGHT / 2;

    public static final int CANNON_WIDTH = 55, CANNON_HEIGHT = 45;
    public static final float CANNON_CENTER_Y = CANNON_HEIGHT / 2.0f, CANNON_CENTER_X = CANNON_CENTER_Y;
    public static final float CANNON_RADIUS = 40;

    public static final double FRICTION_ACCEL = 0.2;
    public static final double RUN_ACCEL = 1.0;
    public static final double RUN_VELOCITY = 1.25;
    public static final double IDLE_VELOCITY = 0.25;
    public static final double JUMP_VEL = 8;
    public static final double RECOIL_VEL = 12;
    public static final int INTERSECT_MARGIN = 10;

    public static final double BULLET_RADIUS = 40;
    public static final int FLASH_FRAMES = 5;

    // position
    private double x, y;
    // velocity
    private double vx, vy;
    // inputs
    private boolean shouldJump;
    private boolean shouldShoot;
    private double shootAngle; // in radians
    private int runDirection;
    private boolean onGround;
    private int invuln;
    private int health;
    private Building building;
    private boolean hurt;

    public Player() {
    }

    public void init(int startX, int groundY) {
        x = startX - HITBOX_X;
        y = groundY - IMAGE_HEIGHT;
        health=100;
        invuln=0;
        vx=0;
        vy=0;
        shouldJump = false;
        onGround = true;
        building=null;
    }

    public Building getBuilding()
    {
        return building;
    }

    public AABB getHitbox() {
        return new AABB(x + HITBOX_X, y + HITBOX_Y,
                        x + HITBOX_X + HITBOX_WIDTH,
                        y + HITBOX_Y + HITBOX_HEIGHT);
    }

    public AABB getBottomHitbox() {
        return new AABB(x + HITBOX_X + HITBOX_WIDTH-5, y + HITBOX_Y + HITBOX_HEIGHT-5,
                        x + HITBOX_X + HITBOX_WIDTH,
                        y + HITBOX_Y + HITBOX_HEIGHT);
    }

    public float getDrawX() {
        return (float)x;
    }

    public float getDrawY() {
        return (float)y;
    }

    public int getHealth()
    {
        return health;
    }

    public int getInvuln()
    {
        return invuln;
    }

    public boolean shouldFlash() {
        return invuln % (2 * FLASH_FRAMES) >= FLASH_FRAMES;
    }

    public double getShootAngle() {
        return shootAngle;
    }

    public void setShouldJump(boolean b) {
        shouldJump = b;
    }

    public void setRunDirection(int i) {
        runDirection = i;
    }

    public void shootAt(double x, double y) {
        shootAngle = Math.atan2(y - (this.y + CENTER_Y), x - (this.x + CENTER_X));
        shouldShoot = true;
    }

    public void setShootAngle(double x, double y) {
        shootAngle = Math.atan2(y - (this.y + CENTER_Y), x - (this.x + CENTER_X));
    }

    public Bullet createBullet() {
        return new Bullet(x + CENTER_X + Math.cos(shootAngle) * BULLET_RADIUS - Bullet.CENTER_X,
                          y + CENTER_Y + Math.sin(shootAngle) * BULLET_RADIUS - Bullet.CENTER_Y,
                          shootAngle);
    }

    public void move(ArrayList<Building> buildings,ArrayList<Goon> goons) {
        x += vx;
    	if(invuln>0)
    	{
    		invuln--;
    	}
        double groundY = Application.HEIGHT;
        {
            building = null;
            double x1 = x + HITBOX_X;
            double x2 = x1 + HITBOX_WIDTH;
            for (Building b : buildings) {
                if (!(x2 < b.getX1() || b.getX2() < x1) && b.getY() < groundY) {
                    building = b;
                    groundY = b.getY();
                }
            }
            if (y + IMAGE_HEIGHT > groundY + INTERSECT_MARGIN) {
                if (building != null) {
                    if (vx < 0) {
                        x = building.getX2() - HITBOX_X + 1;
                    } else {
                        x = building.getX1() - HITBOX_X - HITBOX_WIDTH - 1;
                    }
                    vx = 0;
                    groundY = Application.HEIGHT;
                    x1 = x + HITBOX_X;
                    x2 = x1 + HITBOX_WIDTH;
                    for (Building b : buildings) {
                        if (!(x2 < b.getX1() || b.getX2() < x1) && b.getY() < groundY) {
                            groundY = b.getY();
                        }
                    }
                }
            }
        }
        hurt = false;
        for (Goon g:goons) {
            if(getHitbox().intersects(g.getHitbox()))
            {
                if(invuln<1)
                {
                    if(vy<3)
                    {
                        health-=g.getDamage();
                        invuln=Goon.invuln_given;
                        hurt = true;
                    }
                }
                double kb = g.getKnockback();
                if (Math.abs(vx) < kb) {
                    vx=Math.copySign(g.getKnockback(),x-g.getDrawX());
                } else {
                    // do nothing
                }
                vx+=Math.copySign(g.getKnockback(),x-g.getDrawX());
                if (Math.abs(vy) < kb) {
                    vy=Math.copySign(g.getKnockback(),x-g.getDrawY());
                } else {
                    // do nothing
                }
                vy+=Math.copySign(g.getKnockback(),y-g.getDrawY());
                break;
            }
        }
        y += vy;
        vy += Game.GRAVITY;
        if (y + IMAGE_HEIGHT > groundY) {
            vy = 0;
            y = groundY - IMAGE_HEIGHT;
            if (Math.abs(vx) > RUN_VELOCITY * 5) {
                vx = Math.copySign(RUN_VELOCITY * 5, vx);
            }
            if (groundY >= Application.HEIGHT) {
                health = 0;
            }
        }
        //TODO: Decrease velocity on impact with ground
        onGround = y + IMAGE_HEIGHT + 1 >= groundY;
        if (onGround) {
            // if on ground
            // friction
            double accel = Math.copySign(FRICTION_ACCEL, -vx);
            if (FRICTION_ACCEL > Math.abs(vx))
                vx = 0;
            else
                vx += accel;
            if (shouldJump) {
                vy = -JUMP_VEL;
            }
            if (Math.abs(vx) < RUN_VELOCITY) {
                vx += runDirection * RUN_ACCEL;
            }
        }
        if (shouldShoot) {
            vx -= Math.cos(shootAngle) * RECOIL_VEL;
            vy -= Math.sin(shootAngle) * RECOIL_VEL;
        }
        shouldShoot = false;
        shouldJump = false;
    }

    public boolean hasJustBeenHurt() {
        return hurt;
    }

    public static enum AnimationState {
        IDLE, RUN_RIGHT, RUN_LEFT, JUMP
    }

    public AnimationState getAnimationState() {
        if (onGround) {
            if (Math.abs(vx) <= IDLE_VELOCITY) {
                return AnimationState.IDLE;
            } else {
                return vx < 0 ? AnimationState.RUN_LEFT : AnimationState.RUN_RIGHT;
            }
        } else {
            return AnimationState.JUMP;
        }
    }
}
