package trash.objects;

import java.util.ArrayList;

import trash.Application;
import trash.states.Game;
import trash.util.AABB;

public class BasicGoon extends GroundGoon{

	public static final int IMAGE_WIDTH=50, IMAGE_HEIGHT=50;
    public static final int HITBOX_X=50, HITBOX_Y=50;
    public static final int HITBOX_WIDTH=50, HITBOX_HEIGHT = IMAGE_HEIGHT - HITBOX_Y;
    public static final int CENTER_X = HITBOX_X + HITBOX_WIDTH / 2;
    public static final int CENTER_Y = HITBOX_Y + HITBOX_HEIGHT / 2;

    public static final double RUN_SPEED=25;
    public static final double RUN_ACCEL=1;
    
    public static final int DAMAGE=10;
    public static final int KNOCKBACK=50;
	
    public BasicGoon(Player play) {
		super(play);
		health=1;
	}
	
	public void init(int startX,int groundY) {
        x = startX;
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

    public int getDamage()
    {
    	return DAMAGE;
    }

    public int getKnockback()
    {
    	return KNOCKBACK;
    }
    
    public float getDrawY() {
        return (float)y;
    }
	
	public void move(ArrayList<Building> buildings) {
		targetX=player.getDrawX()+Player.CENTER_X;
		if(Math.abs(vx)>Math.abs(targetX-x))
		{
			x=targetX;
			vx=0;
		}
		else
		{
			x += vx;
		}
		if(getHitbox().intersects(player.getHitbox()))
		{
			attacking=true;
		}
		else
		{
			attacking=false;
		}
        double groundY = Application.HEIGHT;
        {
            Building hitBuilding = null;
            double x1 = x + HITBOX_X;
            double x2 = x1 + HITBOX_WIDTH;
            for (Building b : buildings) {
                if (!(x2 < b.getX1() || b.getX2() < x1) && b.getY() < groundY) {
                    hitBuilding = b;
                    groundY = b.getY();
                }
            }
            if (y + IMAGE_HEIGHT > groundY + INTERSECT_MARGIN) {
                if (hitBuilding != null) {
                    if (vx < 0) {
                        x = hitBuilding.getX2() - HITBOX_X + 1;
                    } else {
                        x = hitBuilding.getX1() - HITBOX_X - HITBOX_WIDTH - 1;
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
        y += vy;
        vy += Game.GRAVITY;
        if (y + IMAGE_HEIGHT > groundY) {
            vy = 0;
            y = groundY - IMAGE_HEIGHT;
        }
        if (y + IMAGE_HEIGHT + 1 >= groundY) {
            // if on ground
            double target_vel=targetX-x;
            double accel=vx-target_vel;
            if(Math.abs(accel)>RUN_ACCEL)
            {
            	accel=Math.copySign(RUN_ACCEL,accel);
            }
            vx+=accel;
            if(Math.abs(vx)>RUN_SPEED)
            {
            	vx=Math.copySign(RUN_SPEED,vx);
            }
        }
    }
}
