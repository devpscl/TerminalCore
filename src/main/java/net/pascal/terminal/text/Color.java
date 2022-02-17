package net.pascal.terminal.text;

public class Color {


    private final boolean background;
    private final String asciiCode;

    protected Color(boolean background, String asciicode) {
        this.background = background;
        this.asciiCode = asciicode;
    }

    public String getAsciiCode() {
        return asciiCode;
    }

    public boolean isBackgroundColor() {
        return background;
    }

    public boolean isForegroundColor() {
        return !background;
    }

    @Override
    public String toString() {
        return asciiCode;
    }

}
