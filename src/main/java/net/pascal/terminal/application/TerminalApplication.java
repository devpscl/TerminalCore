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

/**
 * The type Terminal application.
 */
public class TerminalApplication {

    /**
     * The constant application.
     */
    public static TerminalApplication application;

    private final Terminal terminal;
    private static TVector cachedTerminalSize;

    private Thread keyWorkerThread;
    private ScheduledFuture<?> sizeControllerScheduler;
    private final ScheduledExecutorService executorService;
    private final List<KeyEventHandler> keyEventHandlers;

    private TerminalScreen currentScreen;


    private ResizeEventHandler resizeEventHandler;


    /**
     * Instantiates a new Terminal application.
     *
     * @param terminal the terminal
     */
    public TerminalApplication(Terminal terminal) {
        if(terminal == null) throw new NullPointerException("Terminal is null");
        this.terminal = terminal;
        if(application != null) {
            throw new TerminalException("Application instance already exists!");
        }
        application = this;
        this.keyEventHandlers = new ArrayList<>();
        this.executorService = Executors.newScheduledThreadPool(1);
        cachedTerminalSize = terminal.getSize();
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

    /**
     * Reset display to clear screen and default colors.
     */
    public void resetDisplay() {
        terminal.setColor(ForegroundColor.WHITE, BackgroundColor.BLACK);
        try {
            terminal.clearScreen();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets resize event handler.
     * this works is only used by the screen and when a new screen is opened a new ResizeHandler is added.
     * But only one can run.
     *
     * @param resizeEventHandler the resize event handler
     */
    public void setResizeEventHandler(ResizeEventHandler resizeEventHandler) {
        this.resizeEventHandler = resizeEventHandler;
    }

    /**
     * Gets current screen.
     *
     * @return the current screen
     */
    public TerminalScreen getCurrentScreen() {
        return currentScreen;
    }

    /**
     * Open screen.
     *
     * @param screen the screen
     */
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

    /**
     * Close screen.
     *
     */
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

    /**
     * Switch screen.
     *
     * @param screen the screen
     */
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

    /**
     * Is screen open.
     *
     * @return the boolean
     */
    public boolean isScreenOpen() {
        return currentScreen != null;
    }

    /**
     * Dispose application.
     */
    public void dispose() {
        sizeControllerScheduler.cancel(true);
        keyWorkerThread.stop();
        if(isScreenOpen()) {
            closeScreen();
        }
        application = null;
    }

    /**
     * Gets terminal instance.
     *
     * @return the terminal
     */
    public Terminal getTerminal() {
        return terminal;
    }

    /**
     * Gets cached terminal size.
     *
     * @return the cached terminal size
     */
    public static TVector getCachedTerminalSize() {
        return cachedTerminalSize.c();
    }

    /**
     * Add key listener.
     *
     * @param eventHandler the event handler
     */
    public void addKeyListener(KeyEventHandler eventHandler) {
        this.keyEventHandlers.add(eventHandler);
    }

    /**
     * Remove key listener.
     *
     * @param eventHandler the event handler
     */
    public void removeKeyListener(KeyEventHandler eventHandler) {
        this.keyEventHandlers.remove(eventHandler);
    }
}
