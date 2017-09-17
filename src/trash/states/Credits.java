package trash.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import trash.Application;

public class Credits extends BasicGameState {
    private Music credits;

    private static class CreditLine {
        public String text;
        public int x, y;
        public double start, end;

        public CreditLine(String text, int x, int y, double start, double end) {
            super();
            this.text = text;
            this.x = x;
            this.y = y;
            this.start = start;
            this.end = end;
        }
    }

    private double endTime;

    private static final CreditLine[] creditLines = {
            new CreditLine("\"IGNITE\" by:", 10, 10, 0.0, 8.0),
            new CreditLine("James Dong", 50, 50, 1.0, 8.0),
            new CreditLine("Zara Sharaf (sugarhyped)", 50, 70, 2.0, 8.0),
            new CreditLine("Aeddon Chipman", 50, 90, 3.0, 8.0),
            new CreditLine("Marcus Mao", 50, 110, 4.0, 8.0),
            new CreditLine("Tommy Rousey", 50, 130, 5.0, 8.0),

            new CreditLine("Programming:", 10, 10, 9.0, 25.0),
            new CreditLine("James Dong", 50, 50, 10.0, 25.0),
            new CreditLine("Marcus Mao", 50, 70, 11.0, 25.0),
            new CreditLine("Tommy Rousey", 50, 90, 12.0, 25.0),

            new CreditLine("Art:", 250, 10, 14.0, 25.0),
            new CreditLine("Zara Sharaf (sugarhyped)", 290, 50, 15.0, 25.0),

            new CreditLine("Music:", 550, 10, 17.0, 25.0),
            new CreditLine("Aeddon Chipman", 590, 50, 18.0, 25.0),

            new CreditLine("Thanks for playing!", 450, 200, 20.0, 25.0),
    };

    double curTime;

    @Override
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        credits = null;//new Music("res/gameover.ogg");
        endTime = 0.0;
        for (CreditLine line : creditLines) {
            if (line.end > endTime)
                endTime = line.end;
        }
    }

    @Override
    public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
        if (credits != null)
            credits.play();
        curTime = 0.0;
    }

    @Override
    public void leave(GameContainer gc, StateBasedGame sbg) throws SlickException {
        if (credits != null)
            credits.stop();
    }

    @Override
    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        g.clear();
        for (CreditLine line : creditLines) {
            if (line.start <= curTime && curTime <= line.end) {
                g.drawString(line.text, line.x, line.y);
            }
        }
    }

    @Override
    public void update(GameContainer gc, StateBasedGame sbg, int i) throws SlickException {
        if (gc.getInput().isKeyPressed(Input.KEY_ENTER) || curTime >= endTime) {
            sbg.enterState(Application.MAINMENU);
        }
        curTime += i / 1000.0;
    }

    @Override
    public int getID() {
        return Application.CREDITS;
    }
}
