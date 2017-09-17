package trash.states;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import trash.Application;

public class MainMenu extends BasicGameState {
    private boolean shouldToggleFullscreen;

    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        // TODO Auto-generated method stub
    }

    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        g.clear();
        g.drawString("Main Menu", 10, 10);
        g.drawString("Press ENTER to start", 10, 50);
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
            sbg.enterState(Application.GAME);
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
