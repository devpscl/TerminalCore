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

/**
 * The type Terminal Editor.
 * Stretchable: yes
 * <br/>
 * includes linecount
 */
public class TEditor extends TComponent{

    private int lineLength;
    private int lineCount;
    private int position;

    private int displayLine;

    private int lineOffset;
    private int positionOffset;

    private LineTextBuffer buffer;
    private boolean selected;
    private Color[] selectColors;
    private boolean dark;

    /**
     * Instantiates a new Terminal full text area.
     * Infinity Text height + width
     *
     * @param size the size
     */
    public TEditor(TVector size) {
        super(size);
        if(size.x < 12) size.setWidth(12);
        if(size.y < 5) size.setHeight(5);
        this.lineLength = size.x - 2;
        this.lineCount = size.y - 2;
        setAbsoluteSize(size);
        buffer = new LineTextBuffer();
        buffer.setLineBufferLengthInfinity();
        lineOffset = 1;
        positionOffset = 0;
        displayLine = 1;
        selectColors = new Color[]{ForegroundColor.BLACK, BackgroundColor.RGB_GREEN};
        setSelectable(true);
        selected = false;
        dark = false;
     }

    /**
     * Instantiates a new Terminal Editor with text.
     * Infinity Text height + width
     *
     * @param size the size
     * @param text the text
     */
    public TEditor(TVector size, String...text) {
        super(size);
        if(size.x < 12) size.setWidth(12);
        if(size.y < 5) size.setHeight(5);
        this.lineLength = size.x - 2;
        this.lineCount = size.y - 2;
        setAbsoluteSize(size);
        buffer = new LineTextBuffer(text);
        buffer.setLineBufferLengthInfinity();
        lineOffset = 1;
        positionOffset = 0;
        displayLine = 1;
        selectColors = new Color[]{ForegroundColor.BLACK, BackgroundColor.RGB_GREEN};
        setSelectable(true);
        selected = false;
    }


    /**
     * Gets buffer.
     * Handler of lines
     *
     * @return the buffer
     */
    public LineTextBuffer getBuffer() {
        return buffer;
    }

    /**
     * Sets select colors.
     * Color of textcursor
     *
     * @param selectColors the select colors
     */
    public void setSelectColors(Color...selectColors) {
        this.selectColors = selectColors;
    }

    /**
     * Get text string [ ].
     *
     * @return the string [ ]
     */
    public String[] getText() {
        return buffer.lines().toArray(new String[0]);
    }

    private String getLinePrefix(int count) {
        if(count < 10) {
            return "   " + count + " | ";
        } else if(count < 100) {
            return "  " + count + " | ";
        } else if(count < 1000) {
            return " " + count + " | ";
        } else if(count < 10000) {
            return count + " | ";
        } else {
            int p = 0;
            while (count>=1000) {
                p++;
                count -= 1000;
            }
            if(p > 9) return count + "*~" + "| ";
            return count + "*" + p + "| ";
        }
    }

    /**
     * Get select colors color [...].
     * Color of textcursor
     *
     * @return the color [ ]
     */
    public Color[] getSelectColors() {
        return selectColors;
    }

    @Override
    public void draw(TDisplayDrawer drawer, TerminalScreen screen) {
        int maxOffsets = buffer.getTotalLength()-lineLength;
        if(maxOffsets < 0) maxOffsets = 0;
        TVector pos = drawer.getCurrentPosition();
        TVector cursor = pos.c().addWidth(position).addHeight(displayLine-1);
        char[] scrollbar = ScrollbarBuilder.buildVerticalScrollbar(lineCount-2, buffer.getCurrentLine(), buffer.count());
        char[] scrollbar2 = ScrollbarBuilder.buildHorizontalScrollbar(lineLength-2, (double)position/(double)lineLength+1, (double)buffer.getTotalLength()/(double)lineLength+1);
        for(int i = 0;i<lineCount;i++) {
            int line = lineOffset+i;
            boolean linePointed = buffer.getCurrentLine() == line && selected;
            StringBuilder sb = new StringBuilder();
            String s = buffer.getLine(line);
            sb.append(getLinePrefix(line));
            StringBuilder tmp = new StringBuilder();
            if(s == null) {
                for(int c = 0;c<lineLength;c++) {
                    tmp.append(" ");
                }
            } else {
                char[] chars = s.toCharArray();
                for(int c = 0;c<lineLength;c++) {
                    int pointer = positionOffset + c;
                    boolean b = false;
                    if(linePointed) {
                        if(cursor.x == pos.x + c) {
                            b = true;
                            for(Color cc : selectColors) {
                                tmp.append(cc.getAsciiCode());
                            }
                        }
                    }
                    if(chars.length>pointer) {
                        tmp.append(chars[pointer]);
                    } else tmp.append(" ");
                    if(b) {
                        tmp.append(TextDecoration.RESET.getAsciiCode()).append(getForegroundColor().toString()).append(getBackgroundColor().toString());
                    }

                }
            }
            sb.append(tmp.toString());
            drawer.point(pos);
            drawer.reset();
            drawer.loadColors();
            drawer.write(sb.toString() + " " + scrollbar[i]);
            pos.addHeight(1);
        }
        StringBuilder sb = new StringBuilder();
        for(int c = 0;c<lineLength+9;c++) {
            sb.append(" ");
        }
        drawer.point(pos);
        drawer.reset();
        drawer.loadColors();
        drawer.write(sb.toString());
        pos.addHeight(1);
        sb = new StringBuilder();
        for(int c = 0;c<lineLength+9;c++) {
            if(scrollbar2.length>c) sb.append(scrollbar2[c]);
            else sb.append(" ");
        }
        drawer.point(pos);
        drawer.reset();
        drawer.loadColors();
        drawer.write(sb.toString());
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

    private void movePositionLeft() {
        if(position <= 0) {
            if(positionOffset != 0) {
                positionOffset--;
                for(int i = 0;i<lineLength;i++) {
                    if(positionOffset != 0 && position <= buffer.getLineLength(buffer.getCurrentLine())) {
                        positionOffset--;
                        position++;
                    }
                }
            }
        } else {
            position--;
        }
        if(buffer.getLineLength(buffer.getCurrentLine()) <= lineLength && positionOffset != 0) {
            positionOffset = 0;
            position = buffer.getLineLength(buffer.getCurrentLine());
        }
    }

    private void movePosition(int index) {
        positionOffset = 0;
        position = 0;
        for(int i = 0;i<=index;i++) {
            if(i>lineLength) {
                position = 0;
                positionOffset++;
            } else {
                position++;
            }
        }
    }

    private void movePositionRight() {
        if(position >= lineLength) {
            positionOffset++;
        } else {
            position++;
        }
        if(buffer.getLineLength(buffer.getCurrentLine()) <= lineLength && positionOffset != 0) {
            positionOffset = 0;
            position = buffer.getLineLength(buffer.getCurrentLine());
        }
    }

    private void prepareOffset() {
        int l = buffer.getCurrentPointer();
        while (l > lineLength) {
            l -= lineLength;
            positionOffset++;
        }
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
                        int index = buffer.getLineLength(buffer.getCurrentLine()-1);
                        buffer.pointerRemoveLine();
                        if(isTopPointing()) {
                            if(lineOffset != 1) {
                                lineOffset--;
                            } else {
                                displayLine--;
                                if(buffer.count() <= lineCount && lineOffset != 1) {
                                    lineOffset = 1;
                                    displayLine = lineCount;
                                }
                            }
                        }
                        movePosition(index-1);
                    }

                } else {
                    int pointer = buffer.getCurrentPointer();
                    int line = buffer.getCurrentLine();
                    String content = buffer.getLine(line);
                    StringBuilder sb = new StringBuilder(content);

                    sb.deleteCharAt(pointer-1);
                    buffer.setLine(line, sb.toString());
                    buffer.setCurrentPointer(pointer-1);
                    movePositionLeft();
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
                positionOffset = 0;
                position = 0;
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
                            if(buffer.count() <= lineCount && lineOffset != 1) {
                                lineOffset = 1;
                                displayLine = lineCount;
                            }
                        }
                        movePosition(buffer.getLineLength(buffer.getCurrentLine())-1);
                        draw(drawer, screen);
                        drawer.point(buffer.toPointerPosition(drawer.getCurrentPosition(), displayLine));
                        drawer.saveCursorPosition();
                    }
                } else {
                    buffer.setCurrentPointer(buffer.getCurrentPointer()-1);
                    movePositionLeft();
                    draw(drawer, screen);
                    drawer.point(buffer.toPointerPosition(drawer.getCurrentPosition(), displayLine));
                    drawer.saveCursorPosition();
                }
            } else if(type == ControlKeyType.ARROW_RIGHT) {
                if(buffer.getCurrentPointer() == buffer.getLineLength(buffer.getCurrentLine())) {
                    if(buffer.getCurrentLine() < buffer.count()) {
                        buffer.setCurrentPointer(0);
                        buffer.setCurrentLine(buffer.getCurrentLine()+1);
                        positionOffset = 0;
                        position = 0;
                        if(isBottomPointing()) {
                            lineOffset++;
                        } else {
                            displayLine++;
                        }
                        draw(drawer, screen);
                        drawer.point(buffer.toPointerPosition(drawer.getCurrentPosition(), displayLine));
                        drawer.saveCursorPosition();
                    } else {
                        c.setCancel(false);
                    }
                } else {
                    buffer.setCurrentPointer(buffer.getCurrentPointer()+1);
                    movePositionRight();
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
                        if(buffer.count() <= lineCount && lineOffset != 1) {
                            lineOffset = 1;
                            displayLine = lineCount;
                        }
                    }
                    movePosition(buffer.getCurrentPointer()-1);
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
                    movePosition(buffer.getCurrentPointer()-1);
                    draw(drawer, screen);
                    drawer.point(buffer.toPointerPosition(drawer.getCurrentPosition(), displayLine));
                    drawer.saveCursorPosition();
                }
            }

        } else {
            if(input.isNumeric() || input.isNonSpecialKey() || input.isLetterKey()) {
                putCharacter(input.getCharacter());
                draw(drawer, screen);
                drawer.point(buffer.toPointerPosition(drawer.getCurrentPosition(), displayLine));
                drawer.saveCursorPosition();
            }
        }
    }

    /**
     * Put character.
     *
     * @deprecated internal method
     *
     * @param c the c
     */
    public void putCharacter(char c) {
        if(c == '\t') c = ' ';
        int pointer = buffer.getCurrentPointer();
        int line = buffer.getCurrentLine();
        String content = buffer.getLine(line);
        StringBuilder sb = new StringBuilder(content);
        if(sb.length() <= pointer) {
            sb.append(c);
        } else {
            sb.insert(pointer, c);
        }
        buffer.setLine(line, sb.toString());

        buffer.setCurrentPointer(pointer+1);
        movePositionRight();
    }

    private boolean isBottomPointing() {
        return displayLine >= lineCount;
    }

    private boolean isTopPointing() {
        return displayLine <= 1;
    }

    private boolean isLeftPointing() {
        return position <= 0;
    }

    private boolean isRightPointing() {
        return position >= lineLength;
    }

    @Override
    public void onResize(TDisplayDrawer drawer, TerminalScreen screen, TVector newSize, TVector newPosition) {
        int lengthDifference = newSize.x - drawer.getBufferedOriginalSize().x - 2;
        int heightDifference = newSize.y - drawer.getBufferedOriginalSize().y - 2;
        lineCount += heightDifference;
        lineLength += lengthDifference;
        if(position > lineLength) {
            movePosition(position);
        }
        setAbsoluteSize(newSize);

    }

    @Override
    public boolean isStretchable() {
        return true;
    }

}
