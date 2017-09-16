package trash.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import trash.Application;

public class MainMenu extends BasicGameState {

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
        if (gc.getInput().isKeyPressed(Input.KEY_ENTER)) {
            sbg.enterState(Application.GAME);
        }

    }

    @Override
    public int getID() {
        return Application.MAINMENU;
    }
}
