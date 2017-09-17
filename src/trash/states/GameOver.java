package trash.states;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import trash.Application;

public class GameOver extends BasicGameState {
    private boolean shouldToggleFullscreen;
    private Music gameOver;

    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        gameOver = new Music("res/gameover.ogg");
    }
    
    @Override
    public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
        gameOver.play();
    }
    
    @Override
    public void leave(GameContainer gc, StateBasedGame sbg) throws SlickException {
        gameOver.stop();
    }

    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        g.clear();
        g.drawString("GAME OVER", 10, 10);
        g.drawString("Press ENTER to try again", 10, 50);
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
        return Application.GAMEOVER;
    }
    
    @Override
    public void keyPressed(int key, char c) {
        if (key == Input.KEY_F) {
            shouldToggleFullscreen = true;
        }
    }
}
