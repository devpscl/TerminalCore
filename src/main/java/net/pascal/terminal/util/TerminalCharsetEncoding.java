package net.pascal.terminal.util;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class TerminalCharsetEncoding {

    private static boolean enabledUTF8 = false;

    public static void enableUTF8() throws IOException {
        System.setOut(new PrintStream(System.out, true, "UTF-8"));
        if(OperationSystemUtil.isWindows()) {
            try {
                new ProcessBuilder("cmd", "/c", "chcp 65001").inheritIO().start().waitFor();
            } catch (InterruptedException e) {
                throw new IOException("Failed to set UTF-8 on windows");
            }
        }
        enabledUTF8 = true;
    }

    public static boolean isEnabledUTF8() {
        return enabledUTF8;
    }
}
