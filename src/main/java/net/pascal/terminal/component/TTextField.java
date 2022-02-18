package net.pascal.terminal.component;

import net.pascal.terminal.application.TDisplayDrawer;
import net.pascal.terminal.application.TerminalScreen;
import net.pascal.terminal.key.ControlKeyInput;
import net.pascal.terminal.key.ControlKeyType;
import net.pascal.terminal.key.KeyInput;
import net.pascal.terminal.text.BackgroundColor;
import net.pascal.terminal.text.Color;
import net.pascal.terminal.text.ForegroundColor;
import net.pascal.terminal.text.TextDecoration;
import net.pascal.terminal.util.Cancellable;
import net.pascal.terminal.util.TVector;
import net.pascal.terminal.util.event.KeyEventHandler;

/**
 * The type Terminal text field.
 * Stretchable: no
 */
public class TTextField extends TComponent {

    private char[] chars;
    private int pointer;
    private boolean selected;
    private Color[] selectColors;

    /**
     * Instantiates a new Terminal text field.
     *
     * @param length the maximum length
     */
    public TTextField(int length) {
        super(new TVector(length, 1));
        this.chars = new char[length];
        pointer = 0;
        setSelectable(true);
        selected = false;
        selectColors = new Color[]{ForegroundColor.BLACK, BackgroundColor.RGB_GREEN};
    }

    /**
     * Sets select colors.
     * the cursor colors
     *
     * @param selectColors the select colors
     */
    public void setSelectColors(Color...selectColors) {
        this.selectColors = selectColors;
    }

    /**
     * Get select colors color [...].
     * the cursor colors
     *
     * @return the color [...]
     */
    public Color[] getSelectColors() {
        return selectColors;
    }

    /**
     * Instantiates a new Terminal text field with text.
     *
     * @param length the maximum length
     * @param text   the text
     */
    public TTextField(int length, String text) {
        super(new TVector(length, 1));
        this.chars = new char[length];
        pointer = 0;
        setSelectable(true);
        selected = false;
        char[] tc = text.toCharArray();
        for(int i = 0;i<chars.length;i++) {
            if(tc.length > i) {
                chars[i] = tc[i];
                pointer++;
            } else chars[i] = '\u0000';
        }
    }

    /**
     * Gets text.
     *
     * @return the text
     */
    public String getText() {
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            if (c == '\u0000') break;
            sb.append(c);
        }
        return sb.toString();
    }

    private boolean putCharacter(char c) {
        if(c == '\t') c = ' ';
        if(pointer<chars.length && chars[chars.length-1] == '\u0000') {
            char cv = c;
            for(int i = pointer;i<chars.length;i++) {
                char old = chars[i];
                chars[i] = cv;
                cv = old;
                if(cv == '\u0000') break;
            }
            pointer++;
            return true;
        }
        return false;
    }

    private boolean movePointerLeft() {
        if(pointer>0) {
            pointer--;
            return true;
        }
        return false;
    }

    private boolean movePointerRight() {
        if(pointer<getCharacterSize()) {
            pointer++;
            return true;
        }
        return false;
    }

    /**
     * Gets length size.
     *
     * @return the character size
     */
    public int getCharacterSize() {
        for(int i = 0;i<chars.length;i++) {
            if(chars[i] == '\u0000') return i;
        }
        return chars.length-1;
    }

    /**
     * Is pointer at last.
     *
     * @return the boolean
     */
    public boolean isPointerAtLast() {
        for(int i = pointer;i<chars.length;i++) {
            if(chars[i] != '\u0000') return false;
        }
        return true;
    }


    @Override
    public void draw(TDisplayDrawer drawer, TerminalScreen screen) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i<chars.length;i++) {
            char c = chars[i];
            boolean pointed = selected && pointer == i;
            if (c == '\u0000') c = ' ';
            if(pointed) {
                for(Color sc : selectColors) {
                    sb.append(sc.getAsciiCode());
                }
                sb.append(c);
                sb.append(TextDecoration.RESET.getAsciiCode()).append(getForegroundColor().getAsciiCode()).append(getBackgroundColor().getAsciiCode());
                sb.append(TextDecoration.UNDERLINE);
                continue;
            }
            sb.append(c);
        }
        drawer.point();
        drawer.reset();
        if(selected) {
            drawer.write(TextDecoration.UNDERLINE.getAsciiCode());
        }
        drawer.loadColors();
        drawer.writeAtPosition(sb.toString());
        drawer.dispose();
    }

    @Override
    public void select(TDisplayDrawer drawer, TerminalScreen screen) {
        //screen.getApplication().getTerminal().setCursorVisible(true);
        drawer.point(drawer.getCurrentPosition().clone().addWidth(pointer));
        drawer.saveCursorPosition();

        selected = true;
        draw(drawer, screen);
        drawer.point(drawer.getCurrentPosition().clone().addWidth(pointer));

    }

    @Override
    public void deselect(TDisplayDrawer drawer, TerminalScreen screen) {
        //screen.getApplication().getTerminal().setCursorVisible(false);
        selected = false;
        draw(drawer, screen);
    }

    @Override
    public void keyInput(TDisplayDrawer drawer, TerminalScreen screen, KeyInput input, Cancellable outKeyInputCancelling) {
        if(input instanceof ControlKeyInput) {
            ControlKeyInput cki = (ControlKeyInput) input;
            ControlKeyType type = cki.getType();
            if(type == ControlKeyType.DELETE) {
                if(pointer > 0) {
                    if(pointer<=chars.length) {
                        pointer--;
                        chars[pointer] = '\u0000';
                        for(int i = pointer;i<chars.length-1;i++) {
                            if(chars[i+1] == '\u0000') {
                                chars[i] = '\u0000';
                            }
                            chars[i] = chars[i+1];
                            chars[i+1] = '\u0000';
                        }

                        draw(drawer, screen);
                        drawer.point(drawer.getCurrentPosition().clone().addWidth(pointer));
                        drawer.saveCursorPosition();
                    }
                }
            } else if(type == ControlKeyType.ARROW_LEFT) {
                if(movePointerLeft()) {
                    draw(drawer, screen);
                    drawer.point(drawer.getCurrentPosition().clone().addWidth(pointer));
                    drawer.saveCursorPosition();
                }
                if(pointer > 0) {
                    outKeyInputCancelling.setCancel(true);
                }
            } else if(type == ControlKeyType.ARROW_RIGHT) {
                if(movePointerRight()) {
                    draw(drawer, screen);
                    drawer.point(drawer.getCurrentPosition().clone().addWidth(pointer));
                    drawer.saveCursorPosition();
                }
                if(!isPointerAtLast()) {
                    outKeyInputCancelling.setCancel(true);
                }
            } else if(type == ControlKeyType.SPACE) {
                putCharacter(' ');
                draw(drawer, screen);
                drawer.point(drawer.getCurrentPosition().clone().addWidth(pointer));
                drawer.saveCursorPosition();
            }
        } else {
            if(input.isLetterKey() || input.isNonSpecialKey() || input.isNumeric()) {
                char c = input.getCharacter();
                if(c == '\t') c = ' ';
                putCharacter(c);
                draw(drawer, screen);
                drawer.point(drawer.getCurrentPosition().clone().addWidth(pointer));
                drawer.saveCursorPosition();
            }
        }
    }

    @Override
    public boolean isStretchable() {
        return false;
    }
}
