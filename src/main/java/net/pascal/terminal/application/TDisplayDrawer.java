package net.pascal.terminal.application;

import net.pascal.terminal.Terminal;
import net.pascal.terminal.component.TComponent;
import net.pascal.terminal.text.Color;
import net.pascal.terminal.text.TextDecoration;
import net.pascal.terminal.util.DisplayDriver;
import net.pascal.terminal.util.TVector;
import net.pascal.terminal.util.TaskNotifier;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The type TerminalDisplayDrawer.
 * this writes text characters on the component
 */
@DisplayDriver
public class TDisplayDrawer {

    private static boolean backupCursorPosition = false;
    private static final BlockingQueue<Queue> QUEUE = new LinkedBlockingQueue<>();
    private static Thread currentQueueThread;

    static {
        currentQueueThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Queue q = QUEUE.take();

                        q.drawQueue.run(q.drawer);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        currentQueueThread.start();

    }

    private final TVector bufferedOriginalSize;
    private final TVector bufferedOriginalPosition;
    private final TVector bufferedOriginalDisplaySize;
    private final int x1Difference;
    private final int x2Difference;
    private final int y1Difference;
    private final int y2Difference;

    private boolean open;

    private TVector currentPosition;

    private final TComponent component;
    private final TerminalScreen screen;
    private final Terminal terminal;

    /**
     * Instantiates a new drawer
     *
     * @param component the component
     * @param pos       the pos
     * @param screen    the screen
     */
    public TDisplayDrawer(TComponent component, TVector pos, TerminalScreen screen) {
        TVector max = screen.getApplication().getCachedTerminalSize();
        TVector componentSize = component.getAbsoluteSize();
        this.bufferedOriginalPosition = pos.c();
        this.bufferedOriginalSize = componentSize.c();
        this.bufferedOriginalDisplaySize = max;

        x1Difference = pos.x;
        x2Difference = max.x - pos.x;

        y1Difference = pos.y;
        y2Difference = max.y - pos.y;
        this.component = component;
        this.screen = screen;
        this.terminal = screen.getApplication().getTerminal();
        this.currentPosition = pos;
    }

    /**
     * Gets current position in screen.
     *
     * @return the current position
     */
    public TVector getCurrentPosition() {
        return currentPosition.c();
    }

    /**
     * Sets current position of component.
     * the screen must be displayed
     *
     * @param currentPosition the current position as vector
     */
    public void setCurrentPosition(TVector currentPosition) {
        this.currentPosition = currentPosition;
    }

    /**
     * Gets original position of component.
     *
     * @return the original position as vector
     */
    public TVector getOriginalPosition() {
        return bufferedOriginalPosition.c();
    }

    /**
     * Gets original size of component.
     *
     *
     * @return the original size
     */
    public TVector getBufferedOriginalSize() {
        return bufferedOriginalSize.c();
    }

    /**
     * Gets display size of component add.
     *
     * @return the size  where the component was added
     */
    public TVector getBufferedOriginalDisplaySize() {
        return bufferedOriginalDisplaySize;
    }

    /**
     * Gets the handling component.
     *
     * @return the component
     */
    public TComponent getComponent() {
        return component;
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
     * Gets screen.
     *
     * @return the screen
     */
    public TerminalScreen getScreen() {
        return screen;
    }

    /**
     * Gets x difference to 0,0 position.
     *
     * @return the x difference to min position
     */
    public int getXDifferenceToMinPosition() {
        return x1Difference;
    }

    /**
     * Gets y difference to 0,0 position.
     *
     * @return the y difference to min position
     */
    public int getYDifferenceToMinPosition() {
        return y1Difference;
    }

    /**
     * Gets x difference to end position of display.
     *
     * @return the x difference to max position
     */
    public int getXDifferenceToMaxPosition() {
        return x2Difference;
    }

    /**
     * Gets y difference to end position of display.
     *
     * @return the y difference to max position
     */
    public int getYDifferenceToMaxPosition() {
        return y2Difference;
    }

    /**
     * Is displaying.
     *
     * @return the boolean
     */
    public boolean isDisplaying() {
        return screen.isDisplayed();
    }

    /**
     * Point the cursor on component position to write on this position.
     */
    public void point() {
        terminal.setConsoleCursor(currentPosition);
    }

    /**
     * Point the cursor on custom position to write on this position.
     *
     * @param vec the vec
     */
    public void point(TVector vec) {
        terminal.setConsoleCursor(vec);
    }

    /**
     * Write text.
     *
     * @param s the text
     */
    public void write(String s) {
        if(s == null) s = "null";
        terminal.write(s);
    }

    /**
     * Sets colors to terminal.
     *
     * @param colors the colors
     */
    public void setColors(Color...colors) {
        terminal.setColor(colors);
    }

    /**
     * Load colors from component or default.
     */
    public void loadColors() {
        setColors(component.getForegroundColor(), component.getBackgroundColor());
    }

    /**
     * Load colors from specific component.
     *
     * @param component the component
     */
    public void loadColors(TComponent component) {
        setColors(component.getForegroundColor(), component.getBackgroundColor());
    }

    /**
     * Load default colors from screen.
     */
    public void loadDefaultColors() {
        setColors(screen.getForegroundColor(), screen.getBackgroundColor());
    }

    /**
     * Reset colors and text decorations.
     */
    public void reset() {
        terminal.write(TextDecoration.RESET.getAsciiCode());
    }

    /**
     * Write at position.
     *
     * @param s the text
     */
    public void writeAtPosition(String s) {
        writeAtPosition(s, currentPosition);
    }

    /**
     * Write at position.
     *
     * @param s   the text
     * @param vec the position as vector
     */
    public void writeAtPosition(String s, TVector vec) {
        terminal.write("\033[" + vec.getRows() + ";" + vec.getColumns() + "H" + s);
    }

    /**
     * Has saved a cursor position.
     *
     * @return the boolean
     */
    public boolean hasSavedCursorPosition() {
        return backupCursorPosition;
    }

    /**
     * Restore cursor position.
     */
    public void restoreCursorPosition() {
        terminal.restoreCursorPosition();
    }

    /**
     * Save cursor position.
     */
    public void saveCursorPosition() {
        terminal.saveCursorPosition();
    }

    /**
     * Remove cursor position save.
     */
    public void removeCursorPositionSave() {
        backupCursorPosition = false;
    }

    /**
     * Close drawing.
     */
    public void dispose() {
        if(backupCursorPosition) {
            restoreCursorPosition();
        }
        write(TextDecoration.RESET.getAsciiCode());
        loadDefaultColors();
    }

    /**
     * Remove paint of component.
     */
    public void remove() {
        TVector pos = currentPosition.c();
        TVector size = component.getAbsoluteSize().clone();
        terminal.setColor(screen.getForegroundColor(), screen.getBackgroundColor());
        int x = pos.x;
        int y = pos.y;
        for(int i = 0;i<size.y;i++) {
            for(int i2 = 0;i2<size.x;i2++) {
                writeAtPosition(" ", new TVector(x+i2,y+i));
            }
        }
    }

    @Deprecated
    private synchronized void openTask() {

    }

    @Deprecated
    private synchronized void closeTask() {

    }

    /**
     * Add drawertask to queue.
     * is recommended to keep this synchronous
     *
     * @param task the task
     */
    public void addToQueue(DrawQueue task) {
        if(isCalledFromQueue()) {
            task.run(this);
            return;
        }
        QUEUE.add(new Queue(this, task));
    }

    /**
     * Add custom task to queue.
     * is recommended to keep this synchronous
     *
     *
     *
     * @param task the task (can be null)
     */
    public static void addTaskToQueue(DrawQueue task) {
        if(isCalledFromQueue()) {
            task.run(null);
            return;
        }
        QUEUE.add(new Queue(null, task));
    }

    /**
     * The type Queue.
     */
    public static class Queue {

        private TDisplayDrawer drawer;
        private DrawQueue drawQueue;

        /**
         * Instantiates a new Queue.
         *
         * @param drawer    the drawer
         * @param drawQueue the draw queue
         */
        public Queue(TDisplayDrawer drawer, DrawQueue drawQueue) {
            this.drawer = drawer;
            this.drawQueue = drawQueue;
        }

        /**
         * Gets drawer.
         *
         * @return the drawer
         */
        public TDisplayDrawer getDrawer() {
            return drawer;
        }

        /**
         * Gets draw queue.
         *
         * @return the draw queue
         */
        public DrawQueue getDrawQueue() {
            return drawQueue;
        }
    }

    /**
     * Is called from queue boolean.
     *
     * @return the boolean
     */
    public static boolean isCalledFromQueue() {
        return Thread.currentThread().equals(currentQueueThread);
    }

    /**
     * Dispose renderer.
     */
    @Deprecated
    public static void disposeRenderer() {
        if(currentQueueThread != null) {
            if(currentQueueThread.isAlive()) currentQueueThread.stop();
        }
    }
}
