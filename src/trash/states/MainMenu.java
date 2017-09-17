package trash.states;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import trash.Application;

public class MainMenu extends BasicGameState {
    private boolean shouldToggleFullscreen;
    private Image title;
    private Music mainMenu;
    private Sound click;

    public static float CLICK_VOLUME = 1.0f;

    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        mainMenu = new Music("res/menu.ogg");
        click = new Sound("res/menuclick.ogg");
        title = new Image("res/title.png");
        title.setFilter(Image.FILTER_NEAREST);
    }

    @Override
    public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
        mainMenu.loop();
    }

    @Override
    public void leave(GameContainer gc, StateBasedGame sbg) throws SlickException {
        mainMenu.stop();
    }

    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        title.draw(0.0f, 0.0f, Application.WIDTH, Application.HEIGHT);
    }

    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int i) throws SlickException {
        if (shouldToggleFullscreen) {
            shouldToggleFullscreen = false;
            boolean fs = !gc.isFullscreen();
            AppGameContainer agc = (AppGameContainer)gc;
            agc.setDisplayMode(fs ? agc.getScreenWidth() : Application.DISPLAY_WIDTH,
                               fs ? agc.getScreenHeight() : Application.DISPLAY_HEIGHT, fs);
        }
        if (gc.getInput().isKeyPressed(Input.KEY_ENTER)) {
            click.play(1.0f, CLICK_VOLUME);
            mainMenu.fade(1000, 0.0f, true);
            sbg.enterState(Application.GAME, new FadeOutTransition(Color.black, 1000), new EmptyTransition());
        }
        if (gc.getInput().isKeyPressed(Input.KEY_C)) {
            sbg.enterState(Application.CREDITS);
        }
    }

    @Override
    public int getID() {
        return Application.MAINMENU;
    }

    @Override
    public void keyPressed(int key, char c) {
        if (key == Input.KEY_F) {
            shouldToggleFullscreen = true;
        }
    }
}
