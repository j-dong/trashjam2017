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
    
    public Goon(Player play)
    {
        player=play;
    }

    public abstract void init(int x,int y);

    public abstract AABB getHitbox();

    public abstract float getDrawX();

    public abstract float getDrawY();
    
    public abstract void move(ArrayList<Building> buildings);
    
}
