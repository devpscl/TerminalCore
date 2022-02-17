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

    public TVector getCurrentPosition() {
        return currentPosition.c();
    }

    public void setCurrentPosition(TVector currentPosition) {
        this.currentPosition = currentPosition;
    }

    public TVector getOriginalPosition() {
        return bufferedOriginalPosition.c();
    }

    public TVector getBufferedOriginalSize() {
        return bufferedOriginalSize.c();
    }

    public TVector getBufferedOriginalDisplaySize() {
        return bufferedOriginalDisplaySize;
    }

    public TComponent getComponent() {
        return component;
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public TerminalScreen getScreen() {
        return screen;
    }

    public int getXDifferenceToMinPosition() {
        return x1Difference;
    }

    public int getYDifferenceToMinPosition() {
        return y1Difference;
    }

    public int getXDifferenceToMaxPosition() {
        return x2Difference;
    }

    public int getYDifferenceToMaxPosition() {
        return y2Difference;
    }

    public boolean isDisplaying() {
        return screen.isDisplayed();
    }

    public void point() {
        terminal.setConsoleCursor(currentPosition);
    }

    public void point(TVector vec) {
        terminal.setConsoleCursor(vec);
    }

    public void write(String s) {
        if(s == null) s = "null";
        terminal.write(s);
    }

    public void setColors(Color...colors) {
        terminal.setColor(colors);
    }

    public void loadColors() {
        setColors(component.getForegroundColor(), component.getBackgroundColor());
    }

    public void loadColors(TComponent component) {
        setColors(component.getForegroundColor(), component.getBackgroundColor());
    }

    public void loadDefaultColors() {
        setColors(screen.getForegroundColor(), screen.getBackgroundColor());
    }

    public void reset() {
        terminal.write(TextDecoration.RESET.getAsciiCode());
    }

    public void writeAtPosition(String s) {
        writeAtPosition(s, currentPosition);
    }

    public void writeAtPosition(String s, TVector vec) {
        terminal.write("\033[" + vec.getRows() + ";" + vec.getColumns() + "H" + s);
    }

    public boolean hasSavedCursorPosition() {
        return backupCursorPosition;
    }

    public void restoreCursorPosition() {
        terminal.restoreCursorPosition();
    }

    public void saveCursorPosition() {
        terminal.saveCursorPosition();
    }

    public void removeCursorPositionSave() {
        backupCursorPosition = false;
    }

    public void dispose() {
        if(backupCursorPosition) {
            restoreCursorPosition();
        }
        write(TextDecoration.RESET.getAsciiCode());
        loadDefaultColors();
    }

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

    public void addToQueue(DrawQueue task) {
        if(isCalledFromQueue()) {
            task.run(this);
            return;
        }
        QUEUE.add(new Queue(this, task));
    }

    public static void addTaskToQueue(DrawQueue task) {
        if(isCalledFromQueue()) {
            task.run(null);
            return;
        }
        QUEUE.add(new Queue(null, task));
    }

    public static class Queue {

        private TDisplayDrawer drawer;
        private DrawQueue drawQueue;

        public Queue(TDisplayDrawer drawer, DrawQueue drawQueue) {
            this.drawer = drawer;
            this.drawQueue = drawQueue;
        }

        public TDisplayDrawer getDrawer() {
            return drawer;
        }

        public DrawQueue getDrawQueue() {
            return drawQueue;
        }
    }

    public static boolean isCalledFromQueue() {
        return Thread.currentThread().equals(currentQueueThread);
    }

    @Deprecated
    public static void disposeRenderer() {
        if(currentQueueThread != null) {
            if(currentQueueThread.isAlive()) currentQueueThread.stop();
        }
    }
}
