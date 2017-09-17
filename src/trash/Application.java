package trash;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.ScalableGame;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import trash.states.Game;
import trash.states.MainMenu;

public class Application extends StateBasedGame {
    public static final String GAME_NAME = "Trash Jam 2017";
    public static final int FPS = 60;
    public static final int WIDTH = 1080, HEIGHT = 720;

    // game state IDs
    public static final int MAINMENU = 0;
    public static final int GAME = 1;

    public Application() {
        super(GAME_NAME);
    }

    @Override
    public void initStatesList(GameContainer gc) throws SlickException {
        this.addState(new MainMenu());
        this.addState(new Game());
    }
    
    public static int DISPLAY_WIDTH, DISPLAY_HEIGHT;

    public static void main(String[] args) {
        try {
            AppGameContainer appgc;
            appgc = new AppGameContainer(new ScalableGame(new Application(), WIDTH, HEIGHT));
            String sf = System.getenv("SCALE_FACTOR");
            int scale_factor = sf == null || sf.length() == 0 ? 1 : Integer.parseInt(sf);
            DISPLAY_WIDTH = WIDTH * scale_factor;
            DISPLAY_HEIGHT = HEIGHT * scale_factor;
            appgc.setDisplayMode(DISPLAY_WIDTH, DISPLAY_HEIGHT, false);
            appgc.setTargetFrameRate(FPS);
            appgc.setShowFPS(false);
            appgc.start();
        } catch (SlickException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
