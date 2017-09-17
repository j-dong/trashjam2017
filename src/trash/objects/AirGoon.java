package trash.objects;

public abstract class AirGoon extends Goon{
    public static final int INTERSECT_MARGIN = 10;
	
    protected double targetY;
    
	public AirGoon(Player play)
    {
		super(play);
    }
	
}
