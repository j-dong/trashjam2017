package trash.states;

import java.util.ArrayList;
import java.util.Iterator;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import trash.Application;
import trash.objects.Building;
import trash.objects.Goon;
import trash.objects.Bullet;
import trash.objects.Player;
import trash.util.AABB;

public class Game extends BasicGameState {
    public static final double GRAVITY = 0.4;

    public static final double CAMERA_ACC = 1.0;
    public static final double CAMERA_VEL = 100.0;

    public static final int SCROLL_LEFT = 200;
    public static final int SCROLL_RIGHT = Application.WIDTH - SCROLL_LEFT;
    public static final int SCROLL_TOP = 200;

    private ArrayList<Building> buildings;
    private ArrayList<Goon> goons;
    private Player player;
    private ArrayList<Bullet> bullets;

    private double camx, camy, camvx;

    // resources
    private Image playerImage;

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        buildings.clear();
        int initialGroundY = 500;
        buildings.add(new Building(100, 500, initialGroundY));
        buildings.add(new Building(700, 800, initialGroundY + 100));
        player.init(200, initialGroundY);
    }

    @Override
    public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {
        player = new Player();
        buildings = new ArrayList<>();
        goons = new ArrayList<>();
        bullets = new ArrayList<>();
        playerImage = new Image("res/player.png");
        playerImage.setFilter(Image.FILTER_NEAREST);
        camx = 0;
        camy = 0;
        camvx = 0;
    }

    @Override
    public void render(GameContainer arg0, StateBasedGame arg1, Graphics g) throws SlickException {
        g.translate(-(float)camx, -(float)camy);
        playerImage.draw(player.getDrawX(), player.getDrawY());
        g.setColor(Color.white);
        for (Building b : buildings)
            drawBuilding(g, b);
        g.setColor(Color.cyan);
        for (Bullet b : bullets) {
            g.fillOval(b.getDrawX() + Bullet.HITBOX_X, b.getDrawY() + Bullet.HITBOX_Y, Bullet.HITBOX_WIDTH, Bullet.HITBOX_HEIGHT);
        }
    }

    @Override
    public void update(GameContainer gc, StateBasedGame arg1, int arg2) throws SlickException {
        Input input = gc.getInput();
        player.setRunDirection((input.isKeyDown(Input.KEY_LEFT)||input.isKeyDown(Input.KEY_A)  ? -1 : 0) +
                               (input.isKeyDown(Input.KEY_RIGHT)||input.isKeyDown(Input.KEY_D) ?  1 : 0));
        if (input.isKeyDown(Input.KEY_SPACE)||input.isKeyDown(Input.KEY_UP)||input.isKeyDown(Input.KEY_W))
            player.setShouldJump(true);
        player.move(buildings,goons);
        {
            Iterator<Bullet> it = bullets.iterator();
            while (it.hasNext()) {
                Bullet b = it.next();
                b.move();
                b.collide(buildings);
                if (b.shouldExplodeAndDie()) {
                    it.remove();
                }
            }
        }
        // move camera to hold player
        AABB prect = player.getHitbox();
        if (prect.x1 - camx < SCROLL_LEFT) {
            moveCameraTowards(prect.x1 - SCROLL_LEFT);
        } else if (prect.x1 - camx > SCROLL_RIGHT) {
            moveCameraTowards(prect.x2 - SCROLL_RIGHT);
        } else {
            camvx = 0;
        }
        camy = Math.min(0, prect.y1 - SCROLL_TOP);
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
		player.shootAt(x + camx, y + camy);
		bullets.add(player.createBullet());
	}

	private void moveCameraTowards(double x) {
	    double dx = x - camx;
	    int decelFrames = (int)Math.ceil(Math.abs(camvx) / CAMERA_ACC);
	    double avx = Math.abs(camvx);
	    double deceldx = decelFrames * (avx + avx + decelFrames * CAMERA_ACC) / 2;
	    if (deceldx >= Math.abs(dx) && dx * camvx > 0) {
	        // decelerate
	        if (Math.abs(camvx) < Math.abs(CAMERA_ACC)) {
	            camvx = 0;
	            camx = x;
	        } else {
	            camvx -= Math.copySign(CAMERA_ACC, camvx);
	        }
	    } else {
	        if (Math.abs(dx) <= Math.abs(camvx) || Math.abs(dx) < CAMERA_ACC) {
	            camx = x;
	            camvx = 0;
	        } else {
	            camvx += Math.copySign(CAMERA_ACC, dx);
	            if (Math.abs(camvx) > CAMERA_VEL) {
	                camvx = Math.copySign(CAMERA_VEL, camvx);
	            }
	        }
	    }
	    camx += camvx;
	}
}
