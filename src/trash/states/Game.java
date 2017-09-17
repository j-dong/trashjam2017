package trash.states;

import java.util.ArrayList;
import java.util.Iterator;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Renderable;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import trash.Application;
import trash.objects.BasicGoon;
import trash.objects.Building;
import trash.objects.Bullet;
import trash.objects.Goon;
import trash.objects.Player;
import trash.util.AABB;

public class Game extends BasicGameState {
    public static final double GRAVITY = 0.4;

    public static final double CAMERA_ACC = 1.0;
    public static final double CAMERA_VEL = 100.0;

    public static final int SCROLL_LEFT = 200;
    public static final int SCROLL_RIGHT = Application.WIDTH / 2;
    public static final int SCROLL_TOP = 200;

    public static final int RUN_ANIMATION_SPEED = 100;
    public static final int BULLET_ANIMATION_SPEED = 40;

    private ArrayList<Building> buildings;
    private ArrayList<Goon> goons;
    private Player player;
    private ArrayList<Bullet> bullets;

    private double camx, camy, camvx;

    // resources
    private Image playerImage;
    private Image cannonImage;
    private Animation playerRunRight;
    private Animation playerRunLeft;
    private Animation bulletAnim;
    private Image buildingTexture;
    private Image buildingLastRow;

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        buildings.clear();
        int initialGroundY = 500;
        buildings.add(new Building(100, 500, initialGroundY));
        buildings.add(new Building(700, 800, initialGroundY + 100));
        player.init(200, initialGroundY);
        Goon goon=new BasicGoon(player);
        goon.init(400,0);
        goons.add(goon);
    }

    @Override
    public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {
        player = new Player();
        buildings = new ArrayList<>();
        goons = new ArrayList<>();
        bullets = new ArrayList<>();
        playerImage = new Image("res/player.png");
        playerImage.setFilter(Image.FILTER_NEAREST);
        cannonImage = new Image("res/cannon.png");
        cannonImage.setFilter(Image.FILTER_LINEAR);
        cannonImage.setCenterOfRotation(Player.CANNON_CENTER_X, Player.CANNON_CENTER_Y);
        SpriteSheet playerSS = new SpriteSheet("res/player_run.png", 60, 75);
        playerRunRight = new Animation(playerSS, RUN_ANIMATION_SPEED);
        playerRunLeft  = new Animation();
        int ssw = playerSS.getHorizontalCount(), ssh = playerSS.getVerticalCount();
        for (int y = 0; y < ssh; y++) {
            for (int x = 0; x < ssw; x++) {
                playerRunLeft.addFrame(playerSS.getSprite(x, y).getFlippedCopy(true, false), RUN_ANIMATION_SPEED);
            }
        }
        bulletAnim = new Animation(new SpriteSheet("res/bullet.png", 30, 30), BULLET_ANIMATION_SPEED);
        buildingTexture = new Image("res/building.png");
        buildingLastRow = buildingTexture.getSubImage(0, buildingTexture.getHeight() - 1, buildingTexture.getWidth(), 1);
        buildingTexture.setFilter(Image.FILTER_NEAREST);
        buildingLastRow.setFilter(Image.FILTER_NEAREST);
        camx = 0;
        camy = 0;
        camvx = 0;
    }

    private Color backgroundColor = new Color(114, 135, 112);

    @Override
    public void render(GameContainer arg0, StateBasedGame arg1, Graphics g) throws SlickException {
        g.setBackground(backgroundColor);
        g.clear();
        g.translate(-(float)camx, -(float)camy);
        Renderable playerToDraw;
        switch (player.getAnimationState()) {
        case IDLE:
        case JUMP:
            playerToDraw = playerImage;
            break;
        case RUN_LEFT:
            playerToDraw = playerRunLeft;
            break;
        case RUN_RIGHT:
            playerToDraw = playerRunRight;
            break;
        default:
            playerToDraw = playerImage;
        }
        playerToDraw.draw(player.getDrawX(), player.getDrawY());
        // playerImage.draw(player.getDrawX(), player.getDrawY());
        g.setColor(Color.white);
        for (Building b : buildings)
            drawBuilding(g, b);
        g.setColor(Color.cyan);
        for (Bullet b : bullets) {
            //g.fillOval(b.getDrawX() + Bullet.HITBOX_X, b.getDrawY() + Bullet.HITBOX_Y, Bullet.HITBOX_WIDTH, Bullet.HITBOX_HEIGHT);
            bulletAnim.draw(b.getDrawX(), b.getDrawY());
        }
        g.setColor(Color.red);
        for (Goon go:goons) {
            g.fillRect(go.getDrawX() + go.getHitboxX(), go.getDrawY() + go.getHitboxY(), go.getHitboxWidth(), go.getHitboxHeight());
        }
        cannonImage.setRotation((float)Math.toDegrees(player.getShootAngle()));
        cannonImage.draw(player.getDrawX() + Player.CENTER_X - Player.CANNON_CENTER_X + (float)Math.cos(player.getShootAngle()) * Player.CANNON_RADIUS,
                         player.getDrawY() + Player.CENTER_Y - Player.CANNON_CENTER_Y + (float)Math.sin(player.getShootAngle()) * Player.CANNON_RADIUS);
        g.setColor(Color.white);
        g.drawString("Health: "+player.getHealth(),10,10);
    }

    @Override
    public void update(GameContainer gc, StateBasedGame arg1, int arg2) throws SlickException {
        Input input = gc.getInput();
        player.setRunDirection((input.isKeyDown(Input.KEY_LEFT)||input.isKeyDown(Input.KEY_A)  ? -1 : 0) +
                               (input.isKeyDown(Input.KEY_RIGHT)||input.isKeyDown(Input.KEY_D) ?  1 : 0));
        int mx = input.getMouseX(), my = input.getMouseY();
        player.setShootAngle(mx + camx, my + camy);
        if (input.isKeyDown(Input.KEY_SPACE)||input.isKeyDown(Input.KEY_UP)||input.isKeyDown(Input.KEY_W))
            player.setShouldJump(true);
        player.move(buildings,goons);
        {
            Iterator<Bullet> it = bullets.iterator();
            while (it.hasNext()) {
                Bullet b = it.next();
                b.move();
                b.collide(buildings,goons);
                if (b.shouldExplodeAndDie()) {
                    it.remove();
                }
            }
        }
        {
        Iterator<Goon> it=goons.iterator();
            while(it.hasNext())
            {
                Goon go=it.next();
                go.move(buildings,bullets);
                if(go.isDead())
                     it.remove();
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
        playerRunRight.update(1);
        playerRunLeft.update(1);
        bulletAnim.update(1);
    }

    @Override
    public int getID() {
        return Application.GAME;
    }

    private void drawBuilding(Graphics g, Building b) {
        float bw = (float)(b.getX2() - b.getX1());
        float bh = Application.HEIGHT - (float)b.getY();
        buildingTexture.draw((float)b.getX1(), (float)b.getY(), bw, buildingTexture.getHeight());
        buildingLastRow.draw((float)b.getX1(), (float)b.getY() + buildingTexture.getHeight() - 1, bw, 1 + bh - buildingTexture.getHeight());
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
