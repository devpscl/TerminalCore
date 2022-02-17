package net.pascal.terminal.application;

import net.pascal.terminal.Terminal;
import net.pascal.terminal.TerminalException;
import net.pascal.terminal.key.KeyInput;
import net.pascal.terminal.text.BackgroundColor;
import net.pascal.terminal.text.ForegroundColor;
import net.pascal.terminal.util.TVector;
import net.pascal.terminal.util.event.KeyEventHandler;
import net.pascal.terminal.util.event.ResizeEventHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TerminalApplication {

    public static TerminalApplication application;

    private final Terminal terminal;
    private TVector cachedTerminalSize;

    private Thread keyWorkerThread;
    private ScheduledFuture<?> sizeControllerScheduler;
    private final ScheduledExecutorService executorService;
    private final List<KeyEventHandler> keyEventHandlers;

    private TerminalScreen currentScreen;


    private ResizeEventHandler resizeEventHandler;


    public TerminalApplication(Terminal terminal) {
        if(terminal == null) throw new NullPointerException("Terminal is null");
        this.terminal = terminal;
        if(application != null) {
            throw new TerminalException("Application instance already exists!");
        }
        application = this;
        this.keyEventHandlers = new ArrayList<>();
        this.executorService = Executors.newScheduledThreadPool(1);
        this.cachedTerminalSize = terminal.getSize();
        init();
    }

    private void init() {
        keyWorkerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    KeyInput input = terminal.readInput();
                    List<KeyEventHandler> list = new ArrayList<>(keyEventHandlers);
                    for(KeyEventHandler h : list) {
                        h.onKey(input);
                    }
                }
            }
        });
        keyWorkerThread.start();
        sizeControllerScheduler = executorService.scheduleWithFixedDelay(new Runnable() {

            private int ox = cachedTerminalSize.x;
            private int oy = cachedTerminalSize.y;

            @Override
            public void run() {
                if(resizeEventHandler == null) return;
                TVector s = terminal.getSize();
                int nx = s.x;
                int ny = s.y;
                if(ox != nx || oy != ny) {
                    cachedTerminalSize = new TVector(nx, ny);
                    resizeEventHandler.onResize(ox, oy, nx, ny);
                    ox = nx;
                    oy = ny;
                    terminal.setCursorVisible(terminal.getCursorVisible());
                }
            }
        }, 100, 500, TimeUnit.MILLISECONDS);
    }

    public void resetDisplay() {
        terminal.setColor(ForegroundColor.WHITE, BackgroundColor.BLACK);
        try {
            terminal.clearScreen();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setResizeEventHandler(ResizeEventHandler resizeEventHandler) {
        this.resizeEventHandler = resizeEventHandler;
    }

    public TerminalScreen getCurrentScreen() {
        return currentScreen;
    }

    public void openScreen(TerminalScreen screen) {
        if(screen == null) {
            closeScreen();
            return;
        }
        if(this.currentScreen != null) {
            throw new TerminalException("Another screen is already running!");
        }
        this.currentScreen = screen;
        this.currentScreen.open();
    }

    public void closeScreen() {
        if(this.currentScreen == null) {
            throw new TerminalException("No screen is running!");
        }
        this.currentScreen.preClose();
        this.currentScreen.close();
        resetDisplay();
        setResizeEventHandler(null);
        terminal.setCursorVisible(true);
    }

    public void switchScreen(TerminalScreen screen) {
        if(screen == null) {
            closeScreen();
            return;
        }
        if(this.currentScreen == null) {
            throw new TerminalException("No screen is running!");
        }
        currentScreen.preClose();
        screen.open();
        TerminalScreen oldScreen = this.currentScreen;
        this.currentScreen = screen;
        oldScreen.close();
    }

    public boolean isScreenOpen() {
        return currentScreen != null;
    }

    public void dispose() {
        sizeControllerScheduler.cancel(true);
        keyWorkerThread.stop();
        if(isScreenOpen()) {
            closeScreen();
        }
        application = null;
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public TVector getCachedTerminalSize() {
        return cachedTerminalSize.c();
    }

    public void addKeyListener(KeyEventHandler eventHandler) {
        this.keyEventHandlers.add(eventHandler);
    }

    public void removeKeyListener(KeyEventHandler eventHandler) {
        this.keyEventHandlers.remove(eventHandler);
    }
}
