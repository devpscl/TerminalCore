package net.pascal.terminal.component;

import net.pascal.terminal.application.DrawQueue;
import net.pascal.terminal.application.TDisplayDrawer;
import net.pascal.terminal.application.TerminalScreen;
import net.pascal.terminal.key.KeyInput;
import net.pascal.terminal.layout.TLayout;
import net.pascal.terminal.util.Cancellable;
import net.pascal.terminal.util.TVector;

/**
 * The type Terminal label.
 * Stretchable: no
 */
public class TLabel extends TComponent {

    private String text;

    /**
     * Instantiates a new Terminal label.
     *
     * @param text the text
     */
    public TLabel(String text) {
        super(getSize(text));
        setSelectable(false);
        this.text = text;
    }

    /**
     * Gets text.
     *
     * @return the text
     */
    public String getText() {
        return text;
    }


    /**
     * Sets text.
     *
     * @param text the text
     */
    public void setText(String text) {
        if(!isDisplaying()) {
            this.text = text;
            setAbsoluteSize(new TVector(text.length(), 1));
            return;
        }
        TerminalScreen screen = getCurrentDisplayingScreen();
        TDisplayDrawer drawer = getCurrentDisplayingScreen().getDrawer(this);
        drawer.addToQueue(new DrawQueue() {
            @Override
            public void run(TDisplayDrawer drawer) {
                int x = getAbsoluteSize().x;
                screen.getLayout().remove(TLabel.this, screen, drawer);

                TLabel.this.text = text;
                setAbsoluteSize(getSize(text));

                screen.getLayout().render(TLabel.this, screen, drawer);
                drawer.dispose();
            }
        });

    }

    @Override
    public void draw(TDisplayDrawer drawer, TerminalScreen screen) {
        drawer.point();
        drawer.reset();
        drawer.loadColors();
        String[] t = text.replaceAll("\t", "").split("\n");
        TVector pos = drawer.getCurrentPosition();
        for(String line : t) {
            drawer.writeAtPosition(line, pos);
            pos.addHeight(1);
        }

        drawer.dispose();
    }

    @Override
    public void select(TDisplayDrawer drawer, TerminalScreen screen) {

    }

    @Override
    public void deselect(TDisplayDrawer drawer, TerminalScreen screen) {

    }

    @Override
    public void keyInput(TDisplayDrawer drawer, TerminalScreen screen, KeyInput keyInput, Cancellable outKeyInputCancelling) {

    }

    @Override
    public boolean isStretchable() {
        return false;
    }

    /**
     * Gets size.
     *
     * @deprecated internal method
     *
     * @param message the message
     * @return the size
     */
    public static TVector getSize(String message) {
        String[] msgh = message.replaceAll("\t", "").split("\n");
        int height = msgh.length;
        int width = 0;
        for(String m : msgh) {
            if(width < m.length()) width = m.length();
        }
        return new TVector(width, height);
    }
}
