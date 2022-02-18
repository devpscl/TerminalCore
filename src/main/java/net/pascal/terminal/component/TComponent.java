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

/**
 * The type T component.
 */
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

    /**
     * Instantiates a new Terminal component.
     *
     * @param size the size of component
     */
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

    /**
     * Gets maximum size.
     * If this is compatible with this component, the size can be changed in a layout. So this can be narrowed down.
     *
     * @return the maximum size
     */
    public TVector getMaximumSize() {
        return maximumSize;
    }

    /**
     * Gets minimum size.
     * If this is compatible with this component, the size can be changed in a layout. So this can be narrowed down.
     *
     * @return the minimum size
     */
    public TVector getMinimumSize() {
        return minimumSize;
    }

    /**
     * Sets maximum size.
     * If this is compatible with this component, the size can be changed in a layout. So this can be narrowed down.
     *
     * @param maximumSize the maximum size
     */
    public void setMaximumSize(TVector maximumSize) {
        this.maximumSize = maximumSize;
    }

    /**
     * Sets minimum size.
     * If this is compatible with this component, the size can be changed in a layout. So this can be narrowed down.
     *
     * @param minimumSize the minimum size
     */
    public void setMinimumSize(TVector minimumSize) {
        this.minimumSize = minimumSize;
    }

    /**
     * Sets no out screen moving.
     * the components do not overlap the window edge.
     *
     * @param noOutScreenMoving the no out screen moving
     */
    public void setNoOutScreenMoving(boolean noOutScreenMoving) {
        this.noOutScreenMoving = noOutScreenMoving;
    }

    /**
     * Is no out screen moving boolean.
     *
     * @return the boolean
     */
    public boolean isNoOutScreenMoving() {
        return noOutScreenMoving;
    }

    /**
     * Is position fixed.
     *
     *
     * @return the boolean
     */
    public boolean isPositionFixed() {
        return fixedPosition;
    }

    /**
     * Is size fixed boolean.
     *
     * @return the boolean
     */
    public boolean isSizeFixed() {
        return fixedSize;
    }

    /**
     * Sets position fixed.
     * Movement of the position whether a layout may change the position
     *
     * @param fixedPosition the fixed position
     */
    public void setPositionFixed(boolean fixedPosition) {
        this.fixedPosition = fixedPosition;
    }

    /**
     * Sets size fixed.
     * Resize of the size whether a layout may change the size
     *
     * @param fixedSize the fixed size
     */
    public void setSizeFixed(boolean fixedSize) {
        this.fixedSize = fixedSize;
    }

    /**
     * Sets selectable.
     * The component can be selected via arrow keys
     *
     * @param selectable the selectable
     */
    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    /**
     * Is selectable boolean.
     * The component can be selected via arrow keys
     *
     * @return the boolean
     */
    public boolean isSelectable() {
        return selectable;
    }

    /**
     * Gets foreground color.
     *
     * @return the foreground color
     */
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

    /**
     * Gets background color.
     *
     * @return the background color
     */
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

    /**
     * Sets foreground color.
     *
     * @param foregroundColor the foreground color
     */
    public void setForegroundColor(ForegroundColor foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    /**
     * Sets background color.
     *
     * @param backgroundColor the background color
     */
    public void setBackgroundColor(BackgroundColor backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Gets absolute size.
     *
     * @return the absolute size
     */
    public TVector getAbsoluteSize() {
        return absoluteSize;
    }

    /**
     * Sets absolute size.
     *
     * @param aSize the a size
     */
    @Deprecated
    public void setAbsoluteSize(TVector aSize) {
        this.absoluteSize = aSize;
    }

    /**
     * Gets size.
     *
     * @return the size
     */
    public TVector getSize() {
        return size;
    }

    /**
     * Sets size.
     *
     * @param s the s
     */
    protected void setSize(TVector s) {
        this.absoluteSize = s;
    }

    /**
     * @apiNote This method is executed by the layout in the synctask.
     *
     * @param drawer the drawer
     * @param screen the screen
     */
    public abstract void draw(TDisplayDrawer drawer, TerminalScreen screen);

    /**
     * @apiNote This method is executed by the layout in the synctask.
     *
     * @param drawer the drawer
     * @param screen the screen
     */
    public abstract void select(TDisplayDrawer drawer, TerminalScreen screen);

    /**
     * @apiNote This method is executed by the layout in the synctask.
     *
     * @param drawer the drawer
     * @param screen the screen
     */
    public abstract void deselect(TDisplayDrawer drawer, TerminalScreen screen);

    /**
     * @apiNote This method is executed by the layout in the synctask.
     *
     * @param drawer                the drawer
     * @param screen                the screen
     * @param keyInput              the key input
     * @param outKeyInputCancelling the out key input cancelling
     */
    public abstract void keyInput(TDisplayDrawer drawer, TerminalScreen screen, KeyInput keyInput, Cancellable outKeyInputCancelling);

    /**
     * Gets current displaying screen.
     *
     * @return the current displaying screen
     */
    public TerminalScreen getCurrentDisplayingScreen() {
        return currentDisplayingScreen;
    }

    /**
     * Sets displaying screen.
     *
     * @deprecated This function is only used in the screen
     * @param screen the screen
     */
    @Deprecated
    public void setDisplayingScreen(TerminalScreen screen) {
        this.currentDisplayingScreen = screen;

    }

    /**
     * @apiNote This method is executed by the layout in the synctask.
     *
     * @param drawer      the drawer
     * @param screen      the screen
     * @param newSize     the new size
     * @param newPosition the new position
     */
    public void onResize(TDisplayDrawer drawer, TerminalScreen screen, TVector newSize, TVector newPosition) {

    }

    /**
     * Is stretchable boolean.
     * allows the resizing
     * @return the boolean
     */
    public abstract boolean isStretchable();

    /**
     * Is displaying boolean.
     *
     * @return the boolean
     */
    public boolean isDisplaying() {
        if(currentDisplayingScreen == null) return false;
        if(!currentDisplayingScreen.isDisplayed()) return false;
        return currentDisplayingScreen.hasComponent(this);
    }




}
