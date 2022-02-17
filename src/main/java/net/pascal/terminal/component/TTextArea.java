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

public class TTextArea extends TComponent{

    private int lineLength;
    private int lineDisplayCount;
    private LineTextBuffer buffer;

    private int lineOffset;
    private int displayLine;

    private boolean selected;
    private Color[] selectColors;


    public TTextArea(int x, int y) {
        super(new TVector());
        if(y < 5) y = 5;
        if(x < 5) x = 5;
        this.lineLength = x-2;
        this.lineDisplayCount = y;
        setAbsoluteSize(new TVector(x, y));
        buffer = new LineTextBuffer();
        buffer.setLineBufferLength(lineLength);
        lineOffset = 1;
        displayLine = 1;
        setSelectable(true);
        selectColors = new Color[]{ForegroundColor.BLACK, BackgroundColor.RGB_GREEN};
        selected = false;
    }

    public void setSelectColors(Color...selectColors) {
        this.selectColors = selectColors;
    }

    public Color[] getSelectColors() {
        return selectColors;
    }

    @Override
    public void draw(TDisplayDrawer drawer, TerminalScreen screen) {
        drawer.point();
        TVector pos = drawer.getCurrentPosition();
        TVector cursor = buffer.toPointerPosition(pos, displayLine);

        char[] scrollbar = ScrollbarBuilder.buildVerticalScrollbar(lineDisplayCount-2, buffer.getCurrentLine(), buffer.count());
        for(int i = 0;i<lineDisplayCount;i++) {
            int line = lineOffset + i;
            boolean linePointed = buffer.getCurrentLine() == line && selected;
            String s = buffer.getLine(line);
            StringBuilder sb = new StringBuilder();
            if(s == null) {
                for(int c = 0;c<lineLength;c++) {
                    boolean b = false;
                    if(linePointed) {
                        if(cursor.x == pos.x + c) {
                            for(Color cc : selectColors) {
                                sb.append(cc.getAsciiCode());
                            }
                            b = true;
                        }
                    }
                    sb.append(" ");
                    if(b) {
                        sb.append(TextDecoration.RESET.getAsciiCode()).append(getForegroundColor().toString()).append(getBackgroundColor().toString());
                    }

                }
            } else {
                char[] chars = s.toCharArray();
                for(int c = 0;c<lineLength;c++) {
                    boolean b = false;
                    if(linePointed) {
                        if(cursor.x == pos.x + c) {
                            b = true;
                            for(Color cc : selectColors) {
                                sb.append(cc.getAsciiCode());
                            }
                        }
                    }
                    if(chars.length>c) {
                        sb.append(chars[c]);
                    } else sb.append(" ");
                    if(b) {
                        sb.append(TextDecoration.RESET.getAsciiCode()).append(getForegroundColor().toString()).append(getBackgroundColor().toString());
                    }
                }
            }
            drawer.point(pos);
            drawer.reset();
            drawer.loadColors();
            drawer.write(sb.toString() + " " + scrollbar[i]);
            pos.addHeight(1);
        }
        drawer.dispose();
    }

    @Override
    public void select(TDisplayDrawer drawer, TerminalScreen screen) {
        screen.getApplication().getTerminal().setCursorVisible(false);
        drawer.point(buffer.toPointerPosition(drawer.getCurrentPosition(), displayLine));
        drawer.saveCursorPosition();
        selected = true;
        draw(drawer, screen);
    }

    @Override
    public void deselect(TDisplayDrawer drawer, TerminalScreen screen) {
        screen.getApplication().getTerminal().setCursorVisible(false);
        selected = false;
        draw(drawer, screen);

    }

    private boolean isBottomPointing() {
        return displayLine >= lineDisplayCount;
    }

    private boolean isTopPointing() {
        return displayLine <= 1;
    }

    @Override
    public void keyInput(TDisplayDrawer drawer, TerminalScreen screen, KeyInput input, Cancellable c) {
        c.setCancel(true);

        if(input instanceof ControlKeyInput) {
            ControlKeyInput cki = (ControlKeyInput) input;
            ControlKeyType type = cki.getType();
            if(type == ControlKeyType.DELETE) {
                if(buffer.getCurrentPointer() == 0) {
                    if(buffer.getCurrentLine() > 1) {
                        buffer.pointerRemoveLine();
                        if(isTopPointing()) {
                            lineOffset--;
                        } else {
                            displayLine--;
                            if(buffer.count() <= lineDisplayCount && lineOffset != 1) {
                                lineOffset = 1;
                                displayLine = lineDisplayCount;
                            }
                        }
                    }
                } else {
                    int pointer = buffer.getCurrentPointer();
                    int line = buffer.getCurrentLine();
                    String content = buffer.getLine(line);
                    StringBuilder sb = new StringBuilder(content);

                    sb.deleteCharAt(pointer-1);
                    buffer.setLine(line, sb.toString());
                    buffer.setCurrentPointer(pointer-1);
                }
                draw(drawer, screen);
                drawer.point(buffer.toPointerPosition(drawer.getCurrentPosition(), displayLine));
                drawer.saveCursorPosition();
            } else if(type == ControlKeyType.ENTER) {
                buffer.pointerNewLine();
                if(isBottomPointing()) {
                    lineOffset++;
                } else {
                    displayLine++;
                }

                draw(drawer, screen);
                drawer.point(buffer.toPointerPosition(drawer.getCurrentPosition(), displayLine));
                drawer.saveCursorPosition();
            } else if(type == ControlKeyType.ARROW_LEFT) {
                if(buffer.getCurrentPointer() == 0) {
                    if(buffer.getCurrentLine() == 1) {
                        c.setCancel(false);
                    } else {
                        buffer.setCurrentLine(buffer.getCurrentLine()-1);
                        buffer.setCurrentPointer(buffer.getLineLength(buffer.getCurrentLine()));
                        if(isTopPointing()) {
                            lineOffset--;
                        } else {
                            displayLine--;
                            if(buffer.count() <= lineDisplayCount && lineOffset != 1) {
                                lineOffset = 1;
                                displayLine = lineDisplayCount;
                            }
                        }
                        draw(drawer, screen);
                        drawer.point(buffer.toPointerPosition(drawer.getCurrentPosition(), displayLine));
                        drawer.saveCursorPosition();
                    }
                } else {
                    buffer.setCurrentPointer(buffer.getCurrentPointer()-1);
                    draw(drawer, screen);
                    drawer.point(buffer.toPointerPosition(drawer.getCurrentPosition(), displayLine));
                    drawer.saveCursorPosition();
                }
            } else if(type == ControlKeyType.ARROW_RIGHT) {
                if(buffer.getCurrentPointer() == buffer.getLineLength(buffer.getCurrentLine())) {
                    if(buffer.getCurrentLine() == buffer.count()) {
                        c.setCancel(false);
                    } else {
                        buffer.setCurrentLine(buffer.getCurrentLine()+1);
                        buffer.setCurrentPointer(0);
                        if(isBottomPointing()) {
                            lineOffset++;
                        } else {
                            displayLine++;
                        }
                        draw(drawer, screen);
                        drawer.point(buffer.toPointerPosition(drawer.getCurrentPosition(), displayLine));
                        drawer.saveCursorPosition();
                    }
                } else {
                    buffer.setCurrentPointer(buffer.getCurrentPointer()+1);
                    draw(drawer, screen);
                    drawer.point(buffer.toPointerPosition(drawer.getCurrentPosition(), displayLine));
                    drawer.saveCursorPosition();
                }
            } else if(type == ControlKeyType.SPACE) {
                if(buffer.getCurrentPointer() != lineLength) {
                    putCharacter(' ');
                    draw(drawer, screen);
                    drawer.point(buffer.toPointerPosition(drawer.getCurrentPosition(), displayLine));
                    drawer.saveCursorPosition();
                }
            } else if(type == ControlKeyType.TAB) {
                if(buffer.getCurrentPointer() < lineLength-4) {
                    putCharacter(' ');
                    putCharacter(' ');
                    putCharacter(' ');
                    putCharacter(' ');
                    draw(drawer, screen);
                    drawer.point(buffer.toPointerPosition(drawer.getCurrentPosition(), displayLine));
                    drawer.saveCursorPosition();
                }
            } else if(type == ControlKeyType.ARROW_UP) {
                if(buffer.getCurrentLine() == 1) {
                    c.setCancel(false);
                } else {
                    buffer.setCurrentLine(buffer.getCurrentLine()-1);
                    if(buffer.getCurrentPointer() > buffer.getLineLength(buffer.getCurrentLine())) {
                        buffer.setCurrentPointer(buffer.getLineLength(buffer.getCurrentLine()));
                    }
                    if(isTopPointing()) {
                        lineOffset--;
                    } else {
                        displayLine--;
                        if(buffer.count() <= lineDisplayCount && lineOffset != 1) {
                            lineOffset = 1;
                            displayLine = lineDisplayCount;
                        }
                    }
                    draw(drawer, screen);
                    drawer.point(buffer.toPointerPosition(drawer.getCurrentPosition(), displayLine));
                    drawer.saveCursorPosition();
                }
            } else if(type == ControlKeyType.ARROW_DOWN) {
                if(buffer.getCurrentLine() == buffer.count()) {
                    c.setCancel(false);
                } else {
                    buffer.setCurrentLine(buffer.getCurrentLine()+1);
                    if(buffer.getCurrentPointer() > buffer.getLineLength(buffer.getCurrentLine())) {
                        buffer.setCurrentPointer(buffer.getLineLength(buffer.getCurrentLine()));
                    }
                    if(isBottomPointing()) {
                        lineOffset++;
                    } else {
                        displayLine++;
                    }
                    draw(drawer, screen);
                    drawer.point(buffer.toPointerPosition(drawer.getCurrentPosition(), displayLine));
                    drawer.saveCursorPosition();
                }
            }
        } else {
            if(input.isNumeric() || input.isNonSpecialKey() || input.isLetterKey()) {
                if(buffer.getCurrentPointer() != lineLength) {
                    putCharacter(input.getCharacter());
                    draw(drawer, screen);
                    drawer.point(buffer.toPointerPosition(drawer.getCurrentPosition(), displayLine));
                    drawer.saveCursorPosition();
                }
            }
        }
    }

    public void putCharacter(char c) {
        if(c == '\t') c = ' ';
        int pointer = buffer.getCurrentPointer();
        int line = buffer.getCurrentLine();
        String content = buffer.getLine(line);
        if(content.length() < lineLength) {
            StringBuilder sb = new StringBuilder(content);
            if(sb.length() <= pointer) {
                sb.append(c);
            } else {
                sb.insert(pointer, c);
            }
            buffer.setLine(line, sb.toString());

            buffer.setCurrentPointer(pointer+1);
        }

    }

    @Override
    public boolean isStretchable() {
        return false;
    }
}
