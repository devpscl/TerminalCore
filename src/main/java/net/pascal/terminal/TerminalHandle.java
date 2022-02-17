package net.pascal.terminal;

import net.pascal.terminal.util.OperationSystemUtil;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class TerminalHandle {

    public static File terminal32DLL = new File("terminal32.dll");
    public static File terminalUnixSO = new File("terminal.so");
    private static boolean loaded = false;


    public TerminalHandle() {
        if(loaded) return;
        loaded = true;
        if(OperationSystemUtil.isWindows()) {
            System.load(terminal32DLL.getAbsolutePath());
        } else if(OperationSystemUtil.isLinux()) {
            System.load(terminalUnixSO.getAbsolutePath());
        }
    }

    public native int getch();

    public native int[] getSize();

    public native void resetInputBuffer();

    public native void setTitle(String title);

    public native void setCursorVisible(boolean b);

    public static boolean isLibraryLoaded() {
        return loaded;
    }
}
