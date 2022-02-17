package net.pascal.terminal.component;

import net.pascal.terminal.application.TDisplayDrawer;
import net.pascal.terminal.application.TerminalScreen;
import net.pascal.terminal.key.KeyInput;
import net.pascal.terminal.text.BackgroundColor;
import net.pascal.terminal.text.ForegroundColor;
import net.pascal.terminal.util.Cancellable;
import net.pascal.terminal.util.DisplayDriver;
import net.pascal.terminal.util.TVector;
import sun.reflect.Reflection;

public abstract class TComponent {

    private TerminalScreen currentDisplayingScreen;

    private TVector size;
    private volatile TVector absoluteSize;
    private ForegroundColor foregroundColor;
    private BackgroundColor backgroundColor;
    private boolean selectable;
    private boolean fixedPosition;
    private boolean fixedSize;
    private boolean noOutScreenMoving;

    private TVector minimumSize;
    private TVector maximumSize;

    public TComponent(TVector size) {
        this.size = size;
        this.absoluteSize = size;
        this.selectable = false;
        this.fixedSize = false;
        this.fixedPosition = false;
        this.noOutScreenMoving = false;
        minimumSize = size;
        maximumSize = new TVector(1024, 1024);
    }

    public TVector getMaximumSize() {
        return maximumSize;
    }

    public TVector getMinimumSize() {
        return minimumSize;
    }

    public void setMaximumSize(TVector maximumSize) {
        this.maximumSize = maximumSize;
    }

    public void setMinimumSize(TVector minimumSize) {
        this.minimumSize = minimumSize;
    }

    public void setNoOutScreenMoving(boolean noOutScreenMoving) {
        this.noOutScreenMoving = noOutScreenMoving;
    }

    public boolean isNoOutScreenMoving() {
        return noOutScreenMoving;
    }

    public boolean isPositionFixed() {
        return fixedPosition;
    }

    public boolean isSizeFixed() {
        return fixedSize;
    }

    public void setPositionFixed(boolean fixedPosition) {
        this.fixedPosition = fixedPosition;
    }

    public void setSizeFixed(boolean fixedSize) {
        this.fixedSize = fixedSize;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public ForegroundColor getForegroundColor() {
        if(foregroundColor == null) {
            if(currentDisplayingScreen != null) {
                return foregroundColor = currentDisplayingScreen.getForegroundColor();
            } else {
                return ForegroundColor.WHITE;
            }
        }
        return foregroundColor;
    }

    public BackgroundColor getBackgroundColor() {
        if(backgroundColor == null) {
            if(currentDisplayingScreen != null) {
                return backgroundColor = currentDisplayingScreen.getBackgroundColor();
            } else {
                return BackgroundColor.BLACK;
            }
        }
        return backgroundColor;
    }

    public void setForegroundColor(ForegroundColor foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public void setBackgroundColor(BackgroundColor backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public TVector getAbsoluteSize() {
        return absoluteSize;
    }

    @Deprecated
    public void setAbsoluteSize(TVector aSize) {
        this.absoluteSize = aSize;
    }

    public TVector getSize() {
        return size;
    }

    protected void setSize(TVector s) {
        this.absoluteSize = s;
    }

    public abstract void draw(TDisplayDrawer drawer, TerminalScreen screen);

    public abstract void select(TDisplayDrawer drawer, TerminalScreen screen);

    public abstract void deselect(TDisplayDrawer drawer, TerminalScreen screen);

    public abstract void keyInput(TDisplayDrawer drawer, TerminalScreen screen, KeyInput keyInput, Cancellable outKeyInputCancelling);

    public TerminalScreen getCurrentDisplayingScreen() {
        return currentDisplayingScreen;
    }

    @Deprecated
    public void setDisplayingScreen(TerminalScreen screen) {
        this.currentDisplayingScreen = screen;

    }

    public void onResize(TDisplayDrawer drawer, TerminalScreen screen, TVector newSize, TVector newPosition) {

    }

    public abstract boolean isStretchable();

    public boolean isDisplaying() {
        if(currentDisplayingScreen == null) return false;
        if(!currentDisplayingScreen.isDisplayed()) return false;
        return currentDisplayingScreen.hasComponent(this);
    }




}
