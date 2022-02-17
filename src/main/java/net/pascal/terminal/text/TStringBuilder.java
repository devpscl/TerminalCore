package net.pascal.terminal.text;


import net.pascal.terminal.util.MoveDirection;

public class TStringBuilder {

    private StringBuilder sb;

    public TStringBuilder() {
       sb = new StringBuilder();
    }

    public TStringBuilder(String s) {
        sb = new StringBuilder(s);
    }

    public TStringBuilder foreground(ForegroundColor color) {
        sb.append(color.getAsciiCode());
        return this;
    }

    public TStringBuilder background(BackgroundColor color) {
        sb.append(color.getAsciiCode());
        return this;
    }

    public TStringBuilder foreground(int r, int g, int b) {
        sb.append(ForegroundColor.getColorFromRGB(r, g, b));
        return this;
    }

    public TStringBuilder background(int r, int g, int b) {
        sb.append(BackgroundColor.getColorFromRGB(r, g, b));
        return this;
    }

    public TStringBuilder foreground(int color8) {
        sb.append(ForegroundColor.getColorFrom256ColorSet(color8));
        return this;
    }

    public TStringBuilder background(int color8) {
        sb.append(BackgroundColor.getColorFrom256ColorSet(color8));
        return this;
    }

    public TStringBuilder color(Color color) {
        sb.append(color.getAsciiCode());
        return this;
    }

    public TStringBuilder moveCursor(MoveDirection moveDirection, int i) {
        if(moveDirection == MoveDirection.UP) {
            sb.append("\033[").append(i).append("A");
        } else if(moveDirection == MoveDirection.DOWN) {
            sb.append("\033[").append(i).append("B");
        } else if(moveDirection == MoveDirection.LEFT) {
            sb.append("\033[").append(i).append("D");
        } else if(moveDirection == MoveDirection.RIGHT) {
            sb.append("\033[").append(i).append("C");
        }
        return this;
    }

    public TStringBuilder cursorLeft(int i) {
        return moveCursor(MoveDirection.LEFT, i);
    }

    public TStringBuilder cursorRight(int i) {
        return moveCursor(MoveDirection.RIGHT, i);
    }

    public TStringBuilder cursorUp(int i) {
        return moveCursor(MoveDirection.UP, i);
    }

    public TStringBuilder cursorDown(int i) {
        return moveCursor(MoveDirection.DOWN, i);
    }


    public TStringBuilder reset() {
        sb.append(ForegroundColor.RESET.getAsciiCode());
        return this;
    }

    public TStringBuilder underline() {
        sb.append(TextDecoration.UNDERLINE.getAsciiCode());
        return this;
    }

    public TStringBuilder nextLine() {
        sb.append("\n");
        return this;
    }

    public TStringBuilder bold() {
        sb.append(TextDecoration.BOLD.getAsciiCode());
        return this;
    }

    public TStringBuilder reversed() {
        sb.append(TextDecoration.REVERSED.getAsciiCode());
        return this;
    }

    public TStringBuilder append(int i) {
        sb.append(i);
        return this;
    }

    public TStringBuilder append(char x) {
        sb.append(x);
        return this;
    }

    public TStringBuilder append(double x) {
        sb.append(x);
        return this;
    }

    public TStringBuilder append(float x) {
        sb.append(x);
        return this;
    }

    public TStringBuilder append(boolean x) {
        sb.append(x);
        return this;
    }

    public TStringBuilder append(long x) {
        sb.append(x);
        return this;
    }

    public TStringBuilder append(short x) {
        sb.append(x);
        return this;
    }

    public TStringBuilder append(byte x) {
        sb.append(x);
        return this;
    }

    public TStringBuilder append(Object x) {
        sb.append(x);
        return this;
    }

    public TStringBuilder append(String s) {
        sb.append(s);
        return this;
    }

    public String toString() {
        return sb.toString();
    }

}
