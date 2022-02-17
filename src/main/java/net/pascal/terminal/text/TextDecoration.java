package net.pascal.terminal.text;

public class TextDecoration {

    private final String asciiCode;

    public static final TextDecoration BOLD = new TextDecoration("\u001b[1m");
    public static final TextDecoration UNDERLINE = new TextDecoration("\u001b[4m");
    public static final TextDecoration REVERSED = new TextDecoration("\u001b[7m");
    public static final TextDecoration RESET = new TextDecoration("\u001b[0m");

    protected TextDecoration(String asciicode) {
        this.asciiCode = asciicode;
    }

    public String getAsciiCode() {
        return asciiCode;
    }

    @Override
    public String toString() {
        return asciiCode;
    }

}
