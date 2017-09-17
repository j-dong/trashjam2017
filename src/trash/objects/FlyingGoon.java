package trash.objects;

import java.util.ArrayList;

import trash.Application;
import trash.states.Game;
import trash.util.AABB;
public class FlyingGoon extends AirGoon{

	public static final int IMAGE_WIDTH=50, IMAGE_HEIGHT=50;
    public static final int HITBOX_X=0, HITBOX_Y=0;
    public static final int HITBOX_WIDTH=50, HITBOX_HEIGHT = IMAGE_HEIGHT - HITBOX_Y;
    public static final int CENTER_X = HITBOX_X + HITBOX_WIDTH / 2;
    public static final int CENTER_Y = HITBOX_Y + HITBOX_HEIGHT / 2;

    public static final double RUN_SPEED=1;
    public static final double RUN_ACCEL=1;
    
    public static final int DAMAGE=10;
    public static final double KNOCKBACK=2.5;
	
    public FlyingGoon(Player play) {
		super(play);
		health=1;
	}
	
	public void init(int startX,int groundY) {
        x = startX;
        y = groundY - IMAGE_HEIGHT;
        targetX=(((int)(Math.random()*9999)&1)==0) ? 0 : Application.WIDTH;
    }
	
	public double getTargetX()
	{
	    return targetX;
	}

    public AABB getHitbox() {
        return new AABB(x + HITBOX_X, y + HITBOX_Y,
                        x + HITBOX_X + HITBOX_WIDTH,
                        y + HITBOX_Y + HITBOX_HEIGHT);
    }
    
    public int getHitboxX()
    {
        return HITBOX_X;
    }
    
    public int getHitboxY()
    {
        return HITBOX_Y;
    }
    
    public int getHitboxWidth()
    {
        return HITBOX_WIDTH;
    }
    
    public int getHitboxHeight()
    {
        return HITBOX_HEIGHT;
    }

    public float getDrawX() {
        return (float)x;
    }

    public int getDamage()
    {
    	return DAMAGE;
    }

    public double getKnockback()
    {
    	return KNOCKBACK;
    }
    
    public float getDrawY() {
        return (float)y;
    }

	public boolean canMoveLeft(Building b){
		if(getDrawX()>b.getX1())
			return true;
		return false;
	}
	public boolean canMoveRight(Building b){
		if(getDrawX()+HITBOX_WIDTH<b.getX2())
			return true;
		return false;
	}
	public Building whichBuilding(ArrayList<Building> buildings){
        double x1 = x + HITBOX_X;
        double x2 = x1 + HITBOX_WIDTH;
        for (Building b : buildings) {
            if (!(x2 < b.getX1() || b.getX2() < x1) && b.getY() < Application.HEIGHT) {
                return b;
            }
        }
        return null;
	}
	
	public void move(ArrayList<Building> buildings,ArrayList<Bullet> bullets) {
	    if(health<1)
	    {
	        dead=true;
	        return;
	    }
		Building hitBuilding = whichBuilding(buildings);
    	    if(player.getInvuln()<1)
    	    {
    	        targetX=player.getDrawX()+Player.CENTER_X;
    	        targetY=player.getDrawY()+Player.CENTER_Y;
    	    }
    	    else
    	    {
    	        targetX=x;
    	        targetY=y;
    	    }
		if(Math.abs(vx)>=Math.abs(targetX-x))
		{
			x=targetX;
	        vx=0;
		}
		else
		{
			x += vx;
		}
		if(Math.abs(vy)>=Math.abs(targetY-y))
		{
			y=targetY;
	        vy=0;
		}
		else
		{
			y += vy;
		}
		if(getHitbox().intersects(player.getHitbox()))
		{
			attacking=true;
		}
		else
		{
			attacking=false;
		}
		for(Bullet bill:bullets)
		{
		    if(getHitbox().intersects(bill.getHitbox()))
		    {
		        health--;
                x+=Math.copySign(50,x-bill.getDrawX());
                y+=Math.copySign(50,y-bill.getDrawY());
		    }
		}
        double groundY = Application.HEIGHT;
        
        double x1 = x + HITBOX_X;
        double x2 = x1 + HITBOX_WIDTH;
	    if(hitBuilding!=null)
	    	groundY = hitBuilding.getY();
        
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
        y += vy;
        if (y + IMAGE_HEIGHT > groundY) {
            vy = 0;
            y = groundY - IMAGE_HEIGHT;
        }
        double target_Xvel=targetX-x;
        double accelX=target_Xvel-vx;
        if(Math.abs(accelX)>RUN_ACCEL)
        {
        	accelX=Math.copySign(RUN_ACCEL,accelX);
        }
        	vx+=accelX;
        if(Math.abs(vx)>RUN_SPEED)
        {
        	vx=Math.copySign(RUN_SPEED,vx);
        }
        double target_Yvel=targetY-y;
        double accelY=target_Yvel-vy;
        if(Math.abs(accelY)>RUN_ACCEL)
        {
        	accelY=Math.copySign(RUN_ACCEL,accelY);
        }
        	vy+=accelY;
        if(Math.abs(vy)>RUN_SPEED)
        {
        	vy=Math.copySign(RUN_SPEED,vy);
        }
    }
}
