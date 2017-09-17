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
    
    public abstract boolean canMove(Building b);
    
    public abstract void init(int x,int y);

    public abstract AABB getHitbox();
    
    public abstract int getHitboxX();

    public abstract int getHitboxY();

    public abstract int getHitboxWidth();

    public abstract int getHitboxHeight();

    public abstract float getDrawX();

    public abstract float getDrawY();
    
    public abstract int getDamage();
    
    public abstract int getKnockback();
    
    public abstract void move(ArrayList<Building> buildings);
    
}
