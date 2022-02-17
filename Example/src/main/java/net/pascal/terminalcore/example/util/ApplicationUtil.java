package net.pascal.terminalcore.example.util;

import net.pascal.terminal.Terminal;
import net.pascal.terminal.application.TerminalApplication;

import java.util.concurrent.Executors;

public class ApplicationUtil {

    public static void exit(TerminalApplication application) {
        Terminal t = application.getTerminal();
        if(application.isScreenOpen()) application.closeScreen();
        application.resetDisplay();
        t.resetInputBuffer();
        t.setTitle("Closed");
        System.exit(1);
    }

}
