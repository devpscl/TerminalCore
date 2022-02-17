package net.pascal.terminal.text;

import net.pascal.terminal.Terminal;
import net.pascal.terminal.util.OperationSystemUtil;

public class BackgroundColor extends Color{

    public static final BackgroundColor BLACK = new BackgroundColor("\u001b[40m");
    public static final BackgroundColor RED = new BackgroundColor("\u001b[41m");
    public static final BackgroundColor GREEN = new BackgroundColor("\u001b[42m");
    public static final BackgroundColor YELLOW = new BackgroundColor("\u001b[43m");
    public static final BackgroundColor BLUE = new BackgroundColor("\u001b[44m");
    public static final BackgroundColor MAGENTA = new BackgroundColor("\u001b[45m");
    public static final BackgroundColor CYAN = new BackgroundColor("\u001b[46m");
    public static final BackgroundColor WHITE = new BackgroundColor("\u001b[47m");
    public static final BackgroundColor RESET = new BackgroundColor("\u001b[0m");

    public static final BackgroundColor BLACK_BRIGHT = new BackgroundColor("\u001b[40;1m");
    public static final BackgroundColor RED_BRIGHT = new BackgroundColor("\u001b[41;1m");
    public static final BackgroundColor GREEN_BRIGHT = new BackgroundColor("\u001b[42;1m");
    public static final BackgroundColor YELLOW_BRIGHT = new BackgroundColor("\u001b[43;1m");
    public static final BackgroundColor BLUE_BRIGHT = new BackgroundColor("\u001b[44;1m");
    public static final BackgroundColor MAGENTA_BRIGHT = new BackgroundColor("\u001b[45;1m");
    public static final BackgroundColor CYAN_BRIGHT = new BackgroundColor("\u001b[46;1m");
    public static final BackgroundColor WHITE_BRIGHT = new BackgroundColor("\u001b[47;1m");

    public static final BackgroundColor RGB_RED = getRGB(255, 0, 0, 160);
    public static final BackgroundColor RGB_ORANGE = getRGB(255, 128, 0, 208);
    public static final BackgroundColor RGB_YELLOW = getRGB(255, 255, 0, 226);
    public static final BackgroundColor RGB_LIGHT_GREEN = getRGB(128, 255, 0, 82);
    public static final BackgroundColor RGB_GREEN = getRGB(0, 255, 0, 34);
    public static final BackgroundColor RGB_AQUA = getRGB(0, 255, 255, 45);
    public static final BackgroundColor RGB_LIGHT_BLUE = getRGB(0, 128, 255,14);
    public static final BackgroundColor RGB_BLUE = getRGB(0, 0, 255, 21);
    public static final BackgroundColor RGB_MAGENTA = getRGB(127, 0, 255, 165);
    public static final BackgroundColor RGB_PINK = getRGB(255, 51, 255, 201);
    public static final BackgroundColor RGB_WHITE = getRGB(255, 255, 255, 15);
    public static final BackgroundColor RGB_BLACK = getRGB(0, 0, 0, 0);
    public static final BackgroundColor RGB_GRAY = getRGB(128, 128, 128, 243);

    public static final BackgroundColor[] COLORS = {BLACK, RED, GREEN, YELLOW, BLUE, MAGENTA, CYAN, WHITE, BLACK_BRIGHT
            , RED_BRIGHT, GREEN_BRIGHT, YELLOW_BRIGHT, BLUE_BRIGHT, MAGENTA_BRIGHT, CYAN_BRIGHT, WHITE_BRIGHT};

    public static final BackgroundColor[] RGB_COLORS = {RGB_RED, RGB_ORANGE, RGB_YELLOW, RGB_LIGHT_GREEN, RGB_GREEN
            , RGB_AQUA, RGB_LIGHT_BLUE, RGB_BLUE, RGB_MAGENTA, RGB_PINK, RGB_WHITE, RGB_BLACK, RGB_GRAY};

    protected BackgroundColor(String asciiCode) {
        super(true, asciiCode);
    }

    private static BackgroundColor getRGB(int r, int g, int b, int altv) {
        if(OperationSystemUtil.isLinux()) return getColorFrom256ColorSet(altv);
        return getColorFromRGB(r, g, b);
    }


    public static BackgroundColor getColorFromRGB(int r, int g, int b) {
        return new BackgroundColor("\u001b[48;2;" + r + ";" + g + ";" + b + "m");
    }

    public static BackgroundColor getColorFrom256ColorSet(int i) {
        return new BackgroundColor("\u001b[48;5;" + i + "m");
    }

    public BackgroundColor toForegroundColor() {
        return new BackgroundColor(getAsciiCode().replaceAll("\\[4", "[3"));
    }

}
