package net.pascal.terminal.text;

import net.pascal.terminal.Terminal;
import net.pascal.terminal.util.OperationSystemUtil;

public class ForegroundColor extends Color{

    public static final ForegroundColor BLACK = new ForegroundColor("\u001b[30m");
    public static final ForegroundColor RED = new ForegroundColor("\u001b[31m");
    public static final ForegroundColor GREEN = new ForegroundColor("\u001b[32m");
    public static final ForegroundColor YELLOW = new ForegroundColor("\u001b[33m");
    public static final ForegroundColor BLUE = new ForegroundColor("\u001b[34m");
    public static final ForegroundColor MAGENTA = new ForegroundColor("\u001b[35m");
    public static final ForegroundColor CYAN = new ForegroundColor("\u001b[36m");
    public static final ForegroundColor WHITE = new ForegroundColor("\u001b[37m");
    public static final ForegroundColor RESET = new ForegroundColor("\u001b[0m");


    public static final ForegroundColor BLACK_BRIGHT = new ForegroundColor("\u001b[30;1m");
    public static final ForegroundColor RED_BRIGHT = new ForegroundColor("\u001b[31;1m");
    public static final ForegroundColor GREEN_BRIGHT = new ForegroundColor("\u001b[32;1m");
    public static final ForegroundColor YELLOW_BRIGHT = new ForegroundColor("\u001b[33;1m");
    public static final ForegroundColor BLUE_BRIGHT = new ForegroundColor("\u001b[34;1m");
    public static final ForegroundColor MAGENTA_BRIGHT = new ForegroundColor("\u001b[35;1m");
    public static final ForegroundColor CYAN_BRIGHT = new ForegroundColor("\u001b[36;1m");
    public static final ForegroundColor WHITE_BRIGHT = new ForegroundColor("\u001b[37;1m");


    public static final ForegroundColor RGB_RED = getRGB(255, 0, 0, 160);
    public static final ForegroundColor RGB_ORANGE = getRGB(255, 128, 0, 208);
    public static final ForegroundColor RGB_YELLOW = getRGB(255, 255, 0, 226);
    public static final ForegroundColor RGB_LIGHT_GREEN = getRGB(128, 255, 0, 82);
    public static final ForegroundColor RGB_GREEN = getRGB(0, 255, 0, 34);
    public static final ForegroundColor RGB_AQUA = getRGB(0, 255, 255, 45);
    public static final ForegroundColor RGB_LIGHT_BLUE = getRGB(0, 128, 255,14);
    public static final ForegroundColor RGB_BLUE = getRGB(0, 0, 255, 21);
    public static final ForegroundColor RGB_MAGENTA = getRGB(127, 0, 255, 165);
    public static final ForegroundColor RGB_PINK = getRGB(255, 51, 255, 201);
    public static final ForegroundColor RGB_WHITE = getRGB(255, 255, 255, 15);
    public static final ForegroundColor RGB_BLACK = getRGB(0, 0, 0, 0);
    public static final ForegroundColor RGB_GRAY = getRGB(128, 128, 128, 243);


    public static final ForegroundColor[] COLORS = {BLACK, RED, GREEN, YELLOW, BLUE, MAGENTA, CYAN, WHITE, BLACK_BRIGHT
            , RED_BRIGHT, GREEN_BRIGHT, YELLOW_BRIGHT, BLUE_BRIGHT, MAGENTA_BRIGHT, CYAN_BRIGHT, WHITE_BRIGHT};

    public static final ForegroundColor[] RGB_COLORS = {RGB_RED, RGB_ORANGE, RGB_YELLOW, RGB_LIGHT_GREEN, RGB_GREEN
            , RGB_AQUA, RGB_LIGHT_BLUE, RGB_BLUE, RGB_MAGENTA, RGB_PINK, RGB_WHITE, RGB_BLACK, RGB_GRAY};

    protected ForegroundColor(String asciiCode) {
        super(false, asciiCode);
    }

    private static ForegroundColor getRGB(int r, int g, int b, int altv) {
        if(OperationSystemUtil.isLinux()) return getColorFrom256ColorSet(altv);
        return getColorFromRGB(r, g, b);
    }

    public static ForegroundColor getColorFromRGB(int r, int g, int b) {

        return new ForegroundColor("\u001b[38;2;" + r + ";" + g + ";" + b + "m");
    }

    public static ForegroundColor getColorFrom256ColorSet(int i) {
        return new ForegroundColor("\u001b[38;5;" + i + "m");
    }

    public BackgroundColor toBackgroundColor() {
        return new BackgroundColor(getAsciiCode().replaceAll("\\[3", "[4"));
    }
}
