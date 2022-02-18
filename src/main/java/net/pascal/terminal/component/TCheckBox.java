package net.pascal.terminal.component;

import net.pascal.terminal.application.DrawQueue;
import net.pascal.terminal.application.TDisplayDrawer;
import net.pascal.terminal.application.TerminalScreen;
import net.pascal.terminal.key.ControlKeyInput;
import net.pascal.terminal.key.ControlKeyType;
import net.pascal.terminal.key.KeyInput;
import net.pascal.terminal.text.TextDecoration;
import net.pascal.terminal.util.Cancellable;
import net.pascal.terminal.util.TVector;

/**
 * The type Terminal check box.
 * Stretchable: no
 */
public class TCheckBox extends TComponent {

    private final String text;
    private boolean checked;
    private boolean selected;

    /**
     * Instantiates a new Terminal check box.
     *
     * @param text the text
     */
    public TCheckBox(String text) {
        super(new TVector(text.length()+4, 1));
        this.text = text;
        this.checked = false;
        this.selected = false;
        setSelectable(true);
    }

    /**
     * Gets state of box (is checked).
     *
     * @return the state
     */
    public boolean getState() {
        return checked;
    }

    /**
     * Sets state of box.
     *
     * @param b the b
     */
    public void setState(boolean b) {
        checked = b;
        if(isDisplaying()) {
            TerminalScreen ts = getCurrentDisplayingScreen();
            if(ts != null) {
                ts.getDrawer(this).addToQueue(new DrawQueue() {
                    @Override
                    public void run(TDisplayDrawer drawer) {
                        draw(ts.getDrawer(TCheckBox.this), ts);
                    }
                });

            }
        }
    }

    /**
     * Gets text of checkbox.
     *
     * @return the text
     */
    public String getText() {
        return text;
    }

    @Override
    public void draw(TDisplayDrawer drawer, TerminalScreen screen) {
        String s = checked ? "[▪]" : "[ ]";
        String s2 = selected ? TextDecoration.UNDERLINE.getAsciiCode() : "";
        drawer.point();
        drawer.reset();
        drawer.loadColors();
        drawer.writeAtPosition(s + " " + s2 + text);
        drawer.reset();
        drawer.dispose();
    }

    @Override
    public void select(TDisplayDrawer drawer, TerminalScreen screen) {
        selected = true;
        draw(drawer, screen);
    }

    @Override
    public void deselect(TDisplayDrawer drawer, TerminalScreen screen) {
        selected = false;
        draw(drawer, screen);
    }

    @Override
    public void keyInput(TDisplayDrawer drawer, TerminalScreen screen, KeyInput keyInput, Cancellable outKeyInputCancelling) {
        if(keyInput instanceof ControlKeyInput) {
            if(((ControlKeyInput) keyInput).getType() == ControlKeyType.ENTER) {
                outKeyInputCancelling.setCancel(true);
                checked = !checked;
                draw(drawer, screen);
            }
        }
    }

    @Override
    public boolean isStretchable() {
        return false;
    }
}
