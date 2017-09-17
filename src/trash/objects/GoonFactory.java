package trash.objects;

public class GoonFactory {
	public Goon makeGoon(String name, Player player,int x, int y){
		Goon goon=null;
		switch(name){
		case "basic":goon=new BasicGoon(player);break;
		case "strong":goon=new StrongGoon(player);break;
		case "flying":goon=new FlyingGoon(player);break;
		}
		goon.init(x, y);
		return goon;
	}
}
