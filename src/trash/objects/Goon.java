package trash.objects;

import java.util.ArrayList;

import trash.util.AABB;

public abstract class Goon {
    
    protected final Player player;
    protected double targetX;
    // position
    protected double x, y;
    // velocity
    protected double vx, vy;
    
    protected boolean attacking;
    protected int health;
    protected double knockback;
    protected boolean dead; 
      
    public static final int INTERSECT_MARGIN = 10;
    
    public static final int invuln_given=30;
    
    public Goon(Player play)
    {
        player=play;
        attacking=false;
    }
    public boolean isAttacking()
    {
    	return attacking;
    }
    public abstract Building whichBuilding(ArrayList<Building> buildings);
    
    public abstract boolean canMoveLeft(Building b);
    
    public abstract boolean canMoveRight(Building b);
    
    
    public boolean isDead()
    {
        return dead;
    }

    public abstract void init(int x,int y);
    
    public abstract double getTargetX();

    public abstract AABB getHitbox();
    
    public abstract AABB getTopHitbox();
    
    public abstract int getHitboxX();

    public abstract int getHitboxY();

    public abstract int getHitboxWidth();

    public abstract int getHitboxHeight();

    public abstract float getDrawX();

    public abstract float getDrawY();
    
    public abstract int getDamage();
    
    public abstract double getKnockback();
    
    public abstract void move(ArrayList<Building> buildings,ArrayList<Bullet> bullets);
    
}
