package trash.objects;

public class GoonFactory {
	public Goon makeGoon(String name, Player player,int x, int y){
		Goon goon=null;
		switch(name){
		case "basic":goon=new BasicGoon(player);break;
		case "strong":goon=new BasicGoon(player);break;
		case "flying":goon=new BasicGoon(player);break;
		}
		goon.init(x, y);
		return goon;
	}
}
