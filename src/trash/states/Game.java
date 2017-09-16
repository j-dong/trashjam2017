package trash.states;

import java.util.ArrayList;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import trash.Application;
import trash.objects.Building;
import trash.objects.Player;

public class Game extends BasicGameState {
    public static final double GRAVITY = 0.4;
    public static final double PLAYER_X = 200;

    private ArrayList<Building> buildings;
    private Player player;

    // resources
    private Image playerImage;

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        buildings.clear();
        int initialGroundY = 500;
        buildings.add(new Building(0, 500, initialGroundY));
        player.init(initialGroundY);
    }

    @Override
    public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {
        player = new Player();
        buildings = new ArrayList<>();
        playerImage = new Image("res/player.png");
        playerImage.setFilter(Image.FILTER_NEAREST);
    }

    @Override
    public void render(GameContainer arg0, StateBasedGame arg1, Graphics g) throws SlickException {
        playerImage.draw(player.getDrawX(), player.getDrawY());
        for (Building b : buildings)
            drawBuilding(g, b);
    }

    @Override
    public void update(GameContainer arg0, StateBasedGame arg1, int arg2) throws SlickException {
        player.move(buildings);
    }

    @Override
    public int getID() {
        return Application.GAME;
    }

    private void drawBuilding(Graphics g, Building b) {
        g.fillRect((float)b.getX1(), (float)b.getY(),
                (float)b.getX2() - (float)b.getX1(), (float)(Application.HEIGHT - b.getY()));
    }

    @Override
    public void keyPressed(int key, char c) {
        if (key == Input.KEY_SPACE) {
            player.setShouldJump(true);
        }
    }

	@Override
	public void mousePressed(int button, int x, int y) {
		player.shootAt(x, y);
	}
}
