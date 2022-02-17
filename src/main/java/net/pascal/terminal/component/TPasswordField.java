package net.pascal.terminal.component;

import net.pascal.terminal.application.TDisplayDrawer;
import net.pascal.terminal.application.TerminalScreen;
import net.pascal.terminal.key.ControlKeyInput;
import net.pascal.terminal.key.ControlKeyType;
import net.pascal.terminal.key.KeyInput;
import net.pascal.terminal.text.TextDecoration;
import net.pascal.terminal.util.Cancellable;
import net.pascal.terminal.util.TVector;

public class TPasswordField extends TComponent {

    private char[] chars;
    private int pointer;
    private boolean selected;

    public TPasswordField(int length) {
        super(new TVector(length, 1));
        this.chars = new char[length];
        pointer = 0;
        setSelectable(true);
        selected = false;
    }

    public TPasswordField(int length, String text) {
        super(new TVector(length, 1));
        this.chars = new char[length];
        pointer = 0;
        setSelectable(true);
        selected = false;
        char[] tc = text.toCharArray();
        for(int i = 0;i<chars.length;i++) {
            if(tc.length > i) {
                chars[i] = tc[i];
            } else chars[i] = '\u0000';
        }
    }

    public String getPassword() {
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

    public int getCharacterSize() {
        for(int i = 0;i<chars.length;i++) {
            if(chars[i] == '\u0000') return i;
        }
        return chars.length-1;
    }
    public boolean isPointerAtLast() {
        for(int i = pointer;i<chars.length;i++) {
            if(chars[i] != '\u0000') return false;
        }
        return true;
    }


    @Override
    public void draw(TDisplayDrawer drawer, TerminalScreen screen) {
        StringBuilder sb = new StringBuilder();
        for (char aChar : chars) {
            char c = aChar;
            if (c == '\u0000') c = ' ';
            else c = '*';
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
        screen.getApplication().getTerminal().setCursorVisible(true);
        drawer.point(drawer.getCurrentPosition().clone().addWidth(pointer));
        drawer.saveCursorPosition();

        selected = true;
        draw(drawer, screen);
        drawer.point(drawer.getCurrentPosition().clone().addWidth(pointer));

    }

    @Override
    public void deselect(TDisplayDrawer drawer, TerminalScreen screen) {
        screen.getApplication().getTerminal().setCursorVisible(false);
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
                    drawer.point(drawer.getCurrentPosition().clone().addWidth(pointer));
                    drawer.saveCursorPosition();
                }
                if(pointer > 0) {
                    outKeyInputCancelling.setCancel(true);
                }
            } else if(type == ControlKeyType.ARROW_RIGHT) {
                if(movePointerRight()) {
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
