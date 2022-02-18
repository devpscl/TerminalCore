package net.pascal.terminal.component;

import net.pascal.terminal.application.DrawQueue;
import net.pascal.terminal.application.TDisplayDrawer;
import net.pascal.terminal.application.TerminalScreen;
import net.pascal.terminal.key.ControlKeyInput;
import net.pascal.terminal.key.ControlKeyType;
import net.pascal.terminal.key.KeyInput;
import net.pascal.terminal.text.BackgroundColor;
import net.pascal.terminal.text.Color;
import net.pascal.terminal.text.ForegroundColor;
import net.pascal.terminal.text.TextDecoration;
import net.pascal.terminal.util.Cancellable;
import net.pascal.terminal.util.TVector;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Terminal list box.
 * Stretchable: yes
 */
public class TListBox extends TComponent{

    private List<String> elements;
    private int pointer;
    private int elementOffset;
    private int displayPointer;
    private boolean selected;

    private int elementLength;
    private int elementSize;

    private Color[] pointedColor;

    private SelectEvent selectEvent;

    /**
     * Instantiates a new Terminal list box.
     * Stretchable: yes
     *
     * @param vec      the size
     * @param elements the elements
     */
    public TListBox(TVector vec, List<String> elements) {
        super(new TVector());
        this.elements = elements;
        pointer = 0;
        if(vec.y < 5) vec.setHeight(5);
        if(vec.x < 5) vec.setWidth(5);
        setAbsoluteSize(vec);
        elementLength = vec.x-2;
        elementSize = vec.y-2;
        pointer = 0;
        displayPointer = 0;
        elementOffset = 0;
        pointedColor = new Color[]{ForegroundColor.BLACK, BackgroundColor.RGB_GREEN};
        selected = false;
        setSelectable(true);
    }

    /**
     * Sets select event.
     *
     * @param selectEvent the select event
     */
    public void setSelectEvent(SelectEvent selectEvent) {
        this.selectEvent = selectEvent;
    }

    /**
     * Sets selected color.
     *
     * @param pointedColor the pointed color
     */
    public void setSelectedColor(Color...pointedColor) {
        this.pointedColor = pointedColor;
    }

    /**
     * Get selected color color [ ].
     *
     * @return the color [ ]
     */
    public Color[] getSelectedColor() {
        return pointedColor;
    }

    @Override
    public void draw(TDisplayDrawer drawer, TerminalScreen screen) {
        StringBuilder phd = new StringBuilder();
        for(int i = 0;i<elementLength;i++) {
            phd.append("═");
        }
        drawer.point();
        drawer.reset();
        drawer.loadColors();
        drawer.writeAtPosition("╔" + phd.toString() + "╗");
        for(int y = 0;y<elementSize;y++) {
            int elementIndex = y + elementOffset;
            boolean pointed = this.pointer == elementIndex && elements.size() != 0;
            String element;
            if(elements.size() <= elementIndex) element = "";
            else if(elements.get(elementIndex) == null) element = "";
            else element = elements.get(elementIndex);

            StringBuilder sb = new StringBuilder();
            sb.append("║");
            if(pointed) {
                for(Color c : pointedColor) {
                    sb.append(c.getAsciiCode());
                }
            }
            char[] c = element.toCharArray();
            for(int i = 0;i<elementLength;i++) {
                if(c.length > i) {
                    sb.append(c[i]);
                } else sb.append(" ");
            }
            if(selected || pointed) {
                sb.append(TextDecoration.RESET);
                sb.append(getForegroundColor());
                sb.append(getBackgroundColor());
            }
            sb.append("║");
            drawer.reset();
            drawer.loadColors();
            drawer.writeAtPosition(sb.toString(), drawer.getCurrentPosition().c().addHeight(y+1));
        }
        drawer.writeAtPosition("╚" + phd.toString() + "╝", drawer.getCurrentPosition().c().addHeight(elementSize+1));
        drawer.dispose();
    }

    @Override
    public void onResize(TDisplayDrawer drawer, TerminalScreen screen, TVector newSize, TVector newPosition) {
        displayPointer = 0;
        elementOffset = 0;
        elementSize = newSize.y-2;
        elementLength = newSize.x-2;
    }

    @Override
    public void select(TDisplayDrawer drawer, TerminalScreen screen) {
        selected = true;
        draw(drawer, screen);
    }

    @Override
    public void deselect(TDisplayDrawer drawer, TerminalScreen screen) {
        selected = false;
        draw(drawer, screen);
    }

    @Override
    public void keyInput(TDisplayDrawer drawer, TerminalScreen screen, KeyInput keyInput, Cancellable outKeyInputCancelling) {
        if(elements.size() == 0) {
            outKeyInputCancelling.setCancel(false);
            return;
        }
        if(keyInput instanceof ControlKeyInput) {
            ControlKeyInput cki = (ControlKeyInput) keyInput;
            ControlKeyType type = cki.getType();
            if(type == ControlKeyType.ARROW_UP) {
                if(pointer > 0) {
                    if(isTopPointing()) {
                        if(elementOffset > 0)
                        elementOffset--;
                    } else {
                        displayPointer--;
                    }
                    if(elements.size() <= elementSize && elementOffset != 0) {
                        elementOffset = 0;
                        displayPointer = elementSize-1;
                    }
                    pointer--;
                    draw(drawer, screen);
                    if(selectEvent != null) {
                        selectEvent.onSelect(elements.get(pointer), pointer);
                    }
                    outKeyInputCancelling.setCancel(true);
                }

            } else if(type == ControlKeyType.ARROW_DOWN) {
                if(pointer+1 < elements.size()) {
                    if(isBottomPointing()) {
                        elementOffset++;
                    } else {
                        displayPointer++;
                    }
                    pointer++;
                    draw(drawer, screen);
                    if(selectEvent != null) {
                        selectEvent.onSelect(elements.get(pointer), pointer);
                    }
                    outKeyInputCancelling.setCancel(true);
                }

            }
        }
    }

    private boolean isBottomPointing() {
        return displayPointer >= elementSize-1;
    }

    private boolean isTopPointing() {
        return displayPointer <= 0;
    }

    @Override
    public boolean isStretchable() {
        return true;
    }

    /**
     * Gets elements.
     *
     * @return the elements
     */
    public List<String> getElements() {
        return new ArrayList<>(elements);
    }

    /**
     * Sets elements.
     *
     * @param elements the elements
     */
    public void setElements(List<String> elements) {
        this.elements = elements;
        pointer = 0;
        displayPointer = 0;
        elementOffset = 0;
        if(isDisplaying()) {
            TerminalScreen screen = getCurrentDisplayingScreen();
            TDisplayDrawer drawer = getCurrentDisplayingScreen().getDrawer(this);
            drawer.addToQueue(new DrawQueue() {
                @Override
                public void run(TDisplayDrawer drawer) {
                    draw(drawer, screen);
                }
            });
        }
    }

    /**
     * The interface Select event.
     */
    public static interface SelectEvent {

        /**
         * On select.
         *
         * @param s     the element
         * @param index the index
         */
        void onSelect(String s, int index);

    }


}
