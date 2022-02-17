package net.pascal.terminal.application;

import net.pascal.terminal.Terminal;
import net.pascal.terminal.component.TComponent;
import net.pascal.terminal.key.ControlKeyInput;
import net.pascal.terminal.key.ControlKeyType;
import net.pascal.terminal.key.KeyInput;
import net.pascal.terminal.layout.AbsoluteLayout;
import net.pascal.terminal.layout.TLayout;
import net.pascal.terminal.text.BackgroundColor;
import net.pascal.terminal.text.ForegroundColor;
import net.pascal.terminal.util.*;
import net.pascal.terminal.util.event.KeyEventHandler;
import net.pascal.terminal.util.event.ResizeEventHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

@DisplayDriver
public class TerminalScreen {

    private TerminalApplication application;
    private List<KeyEventHandler> keyEventHandlers;
    private KeyEventHandler mainEventHandler;

    private boolean displayed;

    private ForegroundColor foregroundColor;
    private BackgroundColor backgroundColor;
    private LinkedHashMap<TComponent, TDisplayDrawer> components;
    private TLayout layout;
    private Terminal terminal;
    private ScreenFrame screenFrame;

    private TComponent selectedComponent;


    public TerminalScreen(TerminalApplication application) {
        if(application == null) throw new NullPointerException("Terminalapplication is null");
        this.terminal = application.getTerminal();
        this.application = application;
        keyEventHandlers = new ArrayList<>();
        components = new LinkedHashMap<>();
        mainEventHandler = new KeyEventHandler() {
            @Override
            public void onKey(KeyInput input) {
                key(input);
            }
        };
        displayed = false;
        foregroundColor = ForegroundColor.WHITE;
        backgroundColor = BackgroundColor.BLACK;
        layout = new AbsoluteLayout();
    }

    public void setLayout(TLayout layout) {
        if(layout == null) {
            this.layout = new AbsoluteLayout();
        }
        this.layout = layout;
    }

    public void addComponent(TComponent c, TVector vector) {
        TDisplayDrawer drawer = new TDisplayDrawer(c, vector, this);
        components.put(c, drawer);
        layout.onComponentAdd(c, this);
        if(displayed) {
            c.setDisplayingScreen(this);
            layout.render(c, this, getDrawer(c));
            //display();
        }
    }

    public void removeComponent(TComponent c) {
        if(displayed) layout.remove(c, this, getDrawer(c));
        components.remove(c);
        layout.onComponentRemove(c, this);
        if(displayed) {
            if(selectedComponent == c) {
                selectComponent(null);
            }
            c.setDisplayingScreen(null);

            //display();
        }
    }

    public void updateComponent(TComponent component) {
        if(!displayed) return;
        boolean sel = selectedComponent == component;
        if(components.containsKey(component)) {
            component.setDisplayingScreen(null);
            if(sel) selectComponent(null);
            layout.remove(component, this, components.get(component));
            layout.render(component, this, components.get(component));
            if(sel) selectComponent(component);
        }
    }

    public Set<TComponent> getComponents() {
        return components.keySet();
    }

    public TDisplayDrawer getDrawer(TComponent component) {
        return components.get(component);
    }

    public void updateDrawer(TComponent component, TDisplayDrawer drawer) {
        components.put(component, drawer);
    }




    private void key(KeyInput input) {
        if(selectedComponent != null) {
            Cancellable cancellable = layout.input(selectedComponent, this, getDrawer(selectedComponent), input);
            if(cancellable.isCancelled()) return;
        }
        if(!(input instanceof ControlKeyInput)) return;
        ControlKeyInput c = (ControlKeyInput) input;
        ControlKeyType t = c.getType();
        if(selectedComponent == null) {
            TVector vec = new TVector(1, 1);
            if(t == ControlKeyType.ARROW_LEFT || t == ControlKeyType.ARROW_RIGHT ||
                    t == ControlKeyType.ARROW_DOWN || t == ControlKeyType.ARROW_UP) {
                MoveDirection md = MoveDirection.LEFT;
                TComponent co = getFirst(vec, md);
                if(co != null) {
                    selectComponent(co);
                    return;
                }
                md = MoveDirection.RIGHT;
                co = getFirst(vec, md);
                if(co != null) {
                    selectComponent(co);
                    return;
                }
                md = MoveDirection.DOWN;
                co = getFirst(vec, md);
                if(co != null) {
                    selectComponent(co);
                    return;
                }
                md = MoveDirection.UP;
                co = getFirst(vec, md);
                if(co != null) {
                    selectComponent(co);
                    return;
                }
                return;
            }
        }
        if(getDrawer(selectedComponent) == null) return;
        TVector vec = getDrawer(selectedComponent).getCurrentPosition();
        if(t == ControlKeyType.ARROW_LEFT) {
            MoveDirection md = MoveDirection.LEFT;
            TComponent co = getFirst(vec, md);
            if(co != null) {
                selectComponent(co);
            }
        } else if(t == ControlKeyType.ARROW_RIGHT) {
            MoveDirection md = MoveDirection.RIGHT;
            TComponent co = getFirst(vec, md);
            if(co != null) {
                selectComponent(co);
            }
        } else if(t == ControlKeyType.ARROW_UP) {
            MoveDirection md = MoveDirection.UP;
            TComponent co = getFirst(vec, md);
            if(co != null) {
                selectComponent(co);
            }
        } else if(t == ControlKeyType.ARROW_DOWN) {
            MoveDirection md = MoveDirection.DOWN;
            TComponent co = getFirst(vec, md);
            if(co != null) {
                selectComponent(co);
            }
        }
    }

    public TComponent getSelectedComponent() {
        return selectedComponent;
    }

    public void display() {
        if(displayed) {
            layout.display(this);
            if(screenFrame != null)
            drawFrame(screenFrame, screenFrame.getTitle());
        }
    }

    protected void open() {
        displayed = true;
        terminal.setCursorVisible(false);
        display();
        application.setResizeEventHandler(new ResizeEventHandler() {
            @Override
            public void onResize(int oldX, int oldY, int newX, int newY) {
                layout.resizeAction(TerminalScreen.this, oldX, oldY, newX, newY);
                if(screenFrame != null)
                drawFrame(screenFrame, screenFrame.getTitle());
            }
        });
        application.addKeyListener(mainEventHandler);
        for(TComponent c : getComponents()) {
            c.setDisplayingScreen(this);
        }
        layout.load(this);
    }

    protected void preClose() {
        selectComponent(null);
        layout.unload(this);
        application.setResizeEventHandler(null);
    }

    protected void close() {
        for(KeyEventHandler h : keyEventHandlers) {
            application.removeKeyListener(h);
        }
        application.removeKeyListener(mainEventHandler);

        keyEventHandlers.clear();
        for(TComponent c : getComponents()) {
            c.setDisplayingScreen(null);
        }
    }

    public TLayout getLayout() {
        return layout;
    }

    public BackgroundColor getBackgroundColor() {
        return backgroundColor;
    }

    public ForegroundColor getForegroundColor() {
        return foregroundColor;
    }

    public void setBackgroundColor(BackgroundColor backgroundColor) {
        this.backgroundColor = backgroundColor;
        if(displayed) {
            display();
        }
    }

    public void setForegroundColor(ForegroundColor foregroundColor) {
        this.foregroundColor = foregroundColor;
        if(displayed) {
            display();
        }
    }

    public boolean isDisplayed() {
        return displayed;
    }

    public void addKeyListener(KeyEventHandler eventHandler) {
        this.keyEventHandlers.add(eventHandler);
        application.addKeyListener(eventHandler);
    }

    public void removeKeyListener(KeyEventHandler eventHandler) {
        this.keyEventHandlers.remove(eventHandler);
        application.removeKeyListener(eventHandler);
    }

    public void selectComponent(TComponent component) {
        if(selectedComponent != null) {
            layout.deselect(selectedComponent, this, getDrawer(selectedComponent));
        }
        if(hasComponent(component)) {
            this.selectedComponent = component;
            if(this.selectedComponent != null) {
                layout.select(component, this, getDrawer(component));
            }
        }
    }

    public boolean hasComponent(TComponent component) {
        return this.components.containsKey(component);
    }

    private void drawFrame(ScreenFrame frame, String title) {
        if(frame == null) return;
        Terminal terminal = getApplication().getTerminal();
        TVector size = getApplication().getCachedTerminalSize();
        TDisplayDrawer.addTaskToQueue(new DrawQueue() {
            @Override
            public void run(TDisplayDrawer drawer) {
                TVector[][] vectors = getFrameRectSpecVectors(size, new TVector(0, 0));
                for(TVector hvs : vectors[0]) {
                    terminal.writeAtPosition(String.valueOf(frame.getTopSymbol()), hvs);
                }
                for(TVector vvs : vectors[1]) {
                    terminal.writeAtPosition(String.valueOf(frame.getLeftSymbol()), vvs);
                }
                terminal.writeAtPosition(String.valueOf(frame.getLeftTopCornerSymbol()), vectors[2][0]);
                terminal.writeAtPosition(String.valueOf(frame.getRightTopCornerSymbol()), vectors[2][1]);
                terminal.writeAtPosition(String.valueOf(frame.getLeftBottomCornerSymbol()), vectors[2][2]);
                terminal.writeAtPosition(String.valueOf(frame.getRightBottomCornerSymbol()), vectors[2][3]);
                if(title != null)
                terminal.writeAtPosition( " " + title + " ", new TVector(3, 0));
            }
        });

    }

    private TVector[][] getFrameRectSpecVectors(TVector vector, TVector position) {

        List<TVector> horizontalVectors = new ArrayList<>();
        List<TVector> verticalVectors = new ArrayList<>();
        int x = position.x;
        int y = position.y;
        int sx = vector.x;
        int sy = vector.y;
        for(int i = x;i<sx;i++) {
            horizontalVectors.add(new TVector(i, y));
            horizontalVectors.add(new TVector(i, sy));
        }
        for(int i = y;i<=sy;i++) {
            verticalVectors.add(new TVector(x, i));
            verticalVectors.add(new TVector(sx, i));
        }
        TVector[] corners = new TVector[]{new TVector(x, y), new TVector(sx, y), new TVector(x, sy), new TVector(sx, sy)};
        TVector[] hVectors = horizontalVectors.toArray(new TVector[0]);
        TVector[] vVectors = verticalVectors.toArray(new TVector[0]);
        return new TVector[][]{hVectors, vVectors, corners};
    }

    public void setFrame(String title) {
        screenFrame = new ScreenFrame('┌', '┐', '└'
                , '┘', '─', '│', title);
        if(displayed) {
            drawFrame(screenFrame, title);
        }
    }

    public void setFrame(ScreenFrame frame) {
        screenFrame = frame;
        if(displayed) {
            drawFrame(screenFrame, screenFrame.getTitle());
        }
    }

    public TerminalApplication getApplication() {
        return application;
    }

    private TComponent getFirst(TVector v, MoveDirection s) {
        TComponent co = null;
        double distance = 99999D;
        for (TComponent c : getComponents()) {
            if (!c.isSelectable()) continue;
            TVector tv = getDrawer(c).getCurrentPosition();
            if (s == MoveDirection.UP) {
                if (v.getHeight() > tv.getHeight()) {
                    double d = v.distance(tv);
                    if (distance > d) {
                        co = c;
                        distance = d;
                    }
                }
            } else if (s == MoveDirection.DOWN) {
                if (v.getHeight() < tv.getHeight()) {
                    double d = v.distance(tv);
                    if (distance > d) {
                        co = c;
                        distance = d;
                    }
                }
            } else if (s == MoveDirection.LEFT) {
                if (v.getWidth() > tv.getWidth()) {
                    double d = v.distance(tv);
                    if (distance > d) {
                        co = c;
                        distance = d;
                    }
                }
            } else if (s == MoveDirection.RIGHT) {
                if (v.getWidth() < tv.getWidth()) {
                    double d = v.distance(tv);
                    if (distance > d) {
                        co = c;
                        distance = d;
                    }
                }
            }
        }
        return co;
    }
}
