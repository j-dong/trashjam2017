package trash.states;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ParticleIO;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import trash.Application;
import trash.objects.BasicGoon;
import trash.objects.Building;
import trash.objects.Bullet;
import trash.objects.FlyingGoon;
import trash.objects.Goon;
import trash.objects.Player;
import trash.objects.StrongGoon;
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

    public static final double GRAFFITI_MAX_DENSITY = 0.05;
    public static final int NUM_GRAFFITI = 5;
    public static final int MIN_NUM_GRAFFITI = 3;

    public static final float FIRE_CANNON_VOLUME = 1.0f;
    public static final float HIGH_EXPLOSION_VOLUME = 0.8f;
    public static final float SOFT_EXPLOSION_VOLUME = 0.8f;

    private static class Graffito {
        public int x, y, index, rotation;
        public Graffito(int x, int y, int i) {
            this.x = x;
            this.y = y;
            this.index = i;
            rotation = (int)(Math.random() * 4) * 90;
        }
    }

    private ArrayList<Building> buildings;
    private ArrayList<Goon> goons;
    private Player player;
    private ArrayList<Bullet> bullets;
    private ArrayList<Graffito> graffiti;

    private ParticleSystem partsys;
    private ConfigurableEmitter fireExplosion, blueExplosion;

    private double camx, camy, camvx;

    // resources
    private Image playerImage;
    private Image cannonImage;
    private Animation playerRunRight;
    private Animation playerRunLeft;
    private Animation bulletAnim;
    private Image buildingTexture;
    private Image buildingLastRow;
    private Image[] graffitiImages;
    private Image basicGoon, strongGoon;
    private Animation flyingGoon;
    private Image background;

    private Music mainTheme;
    private Sound fireCannon, highExplosion, softExplosion;

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        buildings.clear();
        bullets.clear();
        goons.clear();
        int initialGroundY = 500;
        buildings.add(new Building(100, 500, initialGroundY));
        buildings.add(new Building(700, 800, initialGroundY + 100));
        player.init(200, initialGroundY);
        for (Building b : buildings) {
            int ngraffiti = (int)(Math.random() * Math.ceil(b.getX2() - b.getX1()) * GRAFFITI_MAX_DENSITY);
            if(ngraffiti<MIN_NUM_GRAFFITI)ngraffiti=MIN_NUM_GRAFFITI;
            double x1 = b.getX1();
            double width = b.getX2() - x1;
            double height = Application.HEIGHT - b.getY();
            for (int i = 0; i < ngraffiti; i++) {
                // we have 2 copies of each image
                int index = (int)(Math.random() * NUM_GRAFFITI * 2);
                int gw = graffitiImages[index].getWidth(),
                    gh = graffitiImages[index].getHeight();
                double x = Math.random() * (width - gw)  + x1,
                       y = Math.random() * (height - gh) + b.getY();
                graffiti.add(new Graffito((int)x, (int)y, index));
            }
        }
        mainTheme.loop();
        Goon goon=new StrongGoon(player);
        goon.init(400,0);
        goons.add(goon);
        goon=new FlyingGoon(player);
        goon.init(700,0);
        goons.add(goon);
        goon = new BasicGoon(player);
        goon.init(1000, 0);
        goons.add(goon);
    }

    public double getCamX()
    {
        return camx;
    }

    @Override
    public void leave(GameContainer container, StateBasedGame game) throws SlickException {
        mainTheme.stop();
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
        graffitiImages = new Image[NUM_GRAFFITI * 2];
        for (int i = 0; i < NUM_GRAFFITI; i++) {
            graffitiImages[i] = new Image("res/graffiti" + i + ".png");
            graffitiImages[i + NUM_GRAFFITI] = graffitiImages[i].getFlippedCopy(true, false);
        }
        graffiti = new ArrayList<>();
        mainTheme = new Music("res/main.ogg");
        try {
            partsys = ParticleIO.loadConfiguredSystem("res/particle.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        fireExplosion = (ConfigurableEmitter)partsys.getEmitter(0);
        blueExplosion = (ConfigurableEmitter)partsys.getEmitter(1);
        fireExplosion.setEnabled(false);
        blueExplosion.setEnabled(false);
        fireCannon = new Sound("res/firecannon.ogg");
        highExplosion = new Sound("res/highexplosion.ogg");
        softExplosion = new Sound("res/softexplosion.ogg");
        basicGoon = new Image("res/plasticbag.png");
        strongGoon = new Image("res/trashbag.png");
        flyingGoon = new Animation(new SpriteSheet("res/tshirt.png", 50, 50), 200); // TODO
        background = new Image("res/sunset.png");
        background.setFilter(Image.FILTER_NEAREST);
        camx = 0;
        camy = 0;
        camvx = 0;
    }

    @Override
    public void render(GameContainer arg0, StateBasedGame arg1, Graphics g) throws SlickException {
        background.draw(0.0f, 0.0f, Application.WIDTH, Application.HEIGHT);
        g.translate(-(float)camx, -(float)camy);
        Image playerToDraw;
        switch (player.getAnimationState()) {
        case IDLE:
        case JUMP:
            playerToDraw = playerImage;
            break;
        case RUN_LEFT:
            playerToDraw = playerRunLeft.getCurrentFrame();
            break;
        case RUN_RIGHT:
            playerToDraw = playerRunRight.getCurrentFrame();
            break;
        default:
            playerToDraw = playerImage;
        }
        if (player.shouldFlash())
            playerToDraw.drawFlash(player.getDrawX(), player.getDrawY());
        else
            playerToDraw.draw(player.getDrawX(), player.getDrawY());
        // playerImage.draw(player.getDrawX(), player.getDrawY());
        g.setColor(Color.white);
        for (Building b : buildings)
            drawBuilding(g, b);
        for (Graffito gr : graffiti)
            drawGraffito(g, gr);
        g.setColor(Color.cyan);
        for (Bullet b : bullets) {
            //g.fillOval(b.getDrawX() + Bullet.HITBOX_X, b.getDrawY() + Bullet.HITBOX_Y, Bullet.HITBOX_WIDTH, Bullet.HITBOX_HEIGHT);
            bulletAnim.draw(b.getDrawX(), b.getDrawY());
        }
        for (Goon go:goons) {
            if(go instanceof BasicGoon) {
                basicGoon.draw(go.getDrawX(), go.getDrawY());
            } else if (go instanceof StrongGoon) {
                strongGoon.draw(go.getDrawX(), go.getDrawY());
            } else if (go instanceof FlyingGoon) {
                flyingGoon.draw(go.getDrawX(), go.getDrawY());
            } else {
                g.setColor(Color.magenta);
                g.fillRect(go.getDrawX() + go.getHitboxX(), go.getDrawY() + go.getHitboxY(), go.getHitboxWidth(), go.getHitboxHeight());
            }
        }
        cannonImage.setRotation((float)Math.toDegrees(player.getShootAngle()));
        cannonImage.draw(player.getDrawX() + Player.CENTER_X - Player.CANNON_CENTER_X + (float)Math.cos(player.getShootAngle()) * Player.CANNON_RADIUS,
                         player.getDrawY() + Player.CENTER_Y - Player.CANNON_CENTER_Y + (float)Math.sin(player.getShootAngle()) * Player.CANNON_RADIUS);
        partsys.render();
        g.setColor(Color.white);
        g.drawString("Health: "+player.getHealth(),10 + (float)camx,10 + (float)camy);
//        g.drawString(""+goons.get(goons.size()-1).getTargetX(),700,50);
    }

    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int dt) throws SlickException {
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
                    switch (b.getDeathCause()) {
                    case ENEMY:
                    case WALL:
                        createBlueExplosion(b.getDrawX() + Bullet.CENTER_X,
                                            b.getDrawY() + Bullet.CENTER_Y);
                        highExplosion.play(1.0f, HIGH_EXPLOSION_VOLUME);
                        break;
                    case LIFE:
                    case BOUNDS:
                    default:
                    }
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
                if(go.isDead()) {
                     it.remove();
                     createFireExplosion(go.getDrawX() + go.getHitboxX() + go.getHitboxWidth() / 2,
                                         go.getDrawY() + go.getHitboxY() + go.getHitboxHeight() / 2);
                     softExplosion.play(1.0f, SOFT_EXPLOSION_VOLUME);
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
        playerRunRight.update(dt);
        playerRunLeft.update(dt);
        bulletAnim.update(dt);
        partsys.update(dt);
        if(player.getHealth()<1)
        {
            sbg.enterState(Application.GAMEOVER);
        }
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

    private void drawGraffito(Graphics g, Graffito gr) {
        graffitiImages[gr.index].setRotation(gr.rotation);
        graffitiImages[gr.index].draw(gr.x, gr.y);
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
        fireCannon.play(1.0f, FIRE_CANNON_VOLUME);
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

    private void createFireExplosion(float x, float y) {
        ConfigurableEmitter e = fireExplosion.duplicate();
        e.setEnabled(true);
        e.setPosition(x, y, false);
        partsys.addEmitter(e);
    }

    private void createBlueExplosion(float x, float y) {
        ConfigurableEmitter e = blueExplosion.duplicate();
        e.setEnabled(true);
        e.setPosition(x, y, false);
        partsys.addEmitter(e);
    }
}
