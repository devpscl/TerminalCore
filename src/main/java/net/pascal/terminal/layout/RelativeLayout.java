package net.pascal.terminal.layout;

import net.pascal.terminal.Terminal;
import net.pascal.terminal.application.DrawQueue;
import net.pascal.terminal.application.TDisplayDrawer;
import net.pascal.terminal.application.TerminalScreen;
import net.pascal.terminal.component.TComponent;
import net.pascal.terminal.key.KeyInput;
import net.pascal.terminal.util.Cancellable;
import net.pascal.terminal.util.DockType;
import net.pascal.terminal.util.RenderVectorHandler;
import net.pascal.terminal.util.TVector;

import java.io.IOException;
import java.util.HashMap;

public class RelativeLayout extends TLayout {

    private HashMap<TComponent, DockType> dockingMap = new HashMap<>();
    private HashMap<TComponent, RenderVectorHandler> renderHandles = new HashMap<>();

    @Override
    public void render(TComponent c, TerminalScreen screen, TDisplayDrawer drawer) {

        if(getRenderVectorHandler(c) != null) {
            TVector terminalSize = screen.getApplication().getCachedTerminalSize();
            if(!c.isSizeFixed()) {
                if(c.isStretchable()) {
                    TVector vec = getRenderVectorHandler(c).getSize(c, drawer.getOriginalPosition(), drawer.getBufferedOriginalSize(), drawer, terminalSize.c());
                    c.setAbsoluteSize(vec);
                }
            }
            if(!c.isPositionFixed()) {
                TVector pos = getRenderVectorHandler(c).getPosition(c, drawer.getOriginalPosition(), drawer.getBufferedOriginalSize(), drawer, terminalSize.c());
                drawer.setCurrentPosition(pos);
            }
        }
        drawer.addToQueue(new DrawQueue() {
            @Override
            public void run(TDisplayDrawer drawer) {
                c.setDisplayingScreen(screen);
                c.draw(drawer, screen);
            }
        });
    }

    @Override
    public void remove(TComponent component, TerminalScreen screen, TDisplayDrawer drawer) {
        if(TDisplayDrawer.isCalledFromQueue()) {
            drawer.remove();
        } else {
            drawer.addToQueue(new DrawQueue() {
                @Override
                public void run(TDisplayDrawer drawer) {
                    drawer.remove();
                }
            });
        }

    }

    @Override
    public void resizeAction(TerminalScreen screen, int ox, int oy, int nx, int ny) {

        for(TComponent c : screen.getComponents()) {
            DockType d = getDockingPoint(c);

            resizeComponent(screen, screen.getApplication().getCachedTerminalSize(), c, screen.getDrawer(c), d);
            c.onResize(screen.getDrawer(c), screen, c.getAbsoluteSize(), screen.getDrawer(c).getCurrentPosition());
        }
        screen.display();
    }

    private void resizeComponent(TerminalScreen screen, TVector terminalSize, TComponent c, TDisplayDrawer d, DockType dockType) {
        int minX = c.getMinimumSize().x;
        int minY = c.getMinimumSize().y;
        int maxX = c.getMaximumSize().x;
        int maxY = c.getMaximumSize().y;
        if(getRenderVectorHandler(c) != null) {
            if(!c.isSizeFixed()) {
                if(c.isStretchable()) {
                    TVector vec = getRenderVectorHandler(c).getSize(c, d.getOriginalPosition(), d.getBufferedOriginalSize(), d, terminalSize.c());
                    c.setAbsoluteSize(vec);
                }
            }
            if(!c.isPositionFixed()) {
                TVector pos = getRenderVectorHandler(c).getPosition(c, d.getOriginalPosition(), d.getBufferedOriginalSize(), d, terminalSize.c());
                d.setCurrentPosition(pos);
            }
            return;
        }
        if(dockType == DockType.CENTER) {
            if(!c.isSizeFixed()) {
                if(c.isStretchable()) {
                    TVector s = d.getBufferedOriginalSize().c().dif(d.getBufferedOriginalDisplaySize(), terminalSize);
                    if(s.x < minX) s.setWidth(minX);
                    if(s.y < minY) s.setHeight(minY);
                    if(s.x > maxX) s.setWidth(maxX);
                    if(s.y > maxY) s.setHeight(maxY);

                    c.setAbsoluteSize(s);
                }
            }
            if(!c.isPositionFixed()) {
                TVector pos = d.getOriginalPosition().dif(d.getBufferedOriginalDisplaySize(), terminalSize);
                if(c.isNoOutScreenMoving()) {
                    TVector size = c.getAbsoluteSize();
                    TVector endCorner = pos.c().add(size);
                    if(endCorner.x > terminalSize.x) {
                        int offset = endCorner.x - terminalSize.x;
                        offset++;
                        pos.subtractWidth(offset);
                    }
                    if(endCorner.y > terminalSize.y) {
                        int offset = endCorner.y - terminalSize.y;
                        offset++;
                        pos.setHeight(offset);
                    }
                }
                d.setCurrentPosition(pos);
            }
        } else if(dockType == DockType.TOP) {

            if(!c.isSizeFixed()) {
                if(c.isStretchable()) {
                    TVector s = d.getBufferedOriginalSize().c().difX(d.getBufferedOriginalDisplaySize(), terminalSize);
                    if(s.x < minX) s.setWidth(minX);
                    if(s.y < minY) s.setHeight(minY);
                    if(s.x > maxX) s.setWidth(maxX);
                    if(s.y > maxY) s.setHeight(maxY);
                    c.setAbsoluteSize(s);
                }
            }
            if(!c.isPositionFixed()) {
                TVector pos = d.getOriginalPosition().difX(d.getBufferedOriginalDisplaySize(), terminalSize);
                if(c.isNoOutScreenMoving()) {
                    TVector size = c.getAbsoluteSize();
                    TVector endCorner = pos.c().add(size);
                    if(endCorner.x > terminalSize.x) {
                        int offset = endCorner.x - terminalSize.x;
                        offset++;
                        pos.subtractWidth(offset);
                    }
                    if(endCorner.y > terminalSize.y) {
                        int offset = endCorner.y - terminalSize.y;
                        offset++;
                        pos.setHeight(offset);
                    }
                }

                d.setCurrentPosition(pos);
            }
        } else if(dockType == DockType.BOTTOM || dockType == DockType.RIGHT) {
            if(!c.isSizeFixed()) {
                if(c.isStretchable()) {
                    TVector s = d.getBufferedOriginalSize().c().dif(d.getBufferedOriginalDisplaySize(), terminalSize);
                    if(s.x < minX) s.setWidth(minX);
                    if(s.y < minY) s.setHeight(minY);
                    if(s.x > maxX) s.setWidth(maxX);
                    if(s.y > maxY) s.setHeight(maxY);
                    c.setAbsoluteSize(s);
                }
            }
            if(!c.isPositionFixed()) {
                TVector pos = d.getOriginalPosition().dif(d.getBufferedOriginalDisplaySize(), terminalSize);
                if(c.isNoOutScreenMoving()) {
                    TVector size = c.getAbsoluteSize();
                    TVector endCorner = pos.c().add(size);
                    if(endCorner.x > terminalSize.x) {
                        int offset = endCorner.x - terminalSize.x;
                        offset++;
                        pos.subtractWidth(offset);
                    }
                    if(endCorner.y > terminalSize.y) {
                        int offset = endCorner.y - terminalSize.y;
                        offset++;
                        pos.setHeight(offset);
                    }
                }
                d.setCurrentPosition(pos);
            }
        } else if(dockType == DockType.LEFT) {

            if(!c.isSizeFixed()) {
                if(c.isStretchable()) {
                    TVector s = d.getBufferedOriginalSize().c().difY(d.getBufferedOriginalDisplaySize(), terminalSize);
                    if(s.x < minX) s.setWidth(minX);
                    if(s.y < minY) s.setHeight(minY);
                    if(s.x > maxX) s.setWidth(maxX);
                    if(s.y > maxY) s.setHeight(maxY);
                    c.setAbsoluteSize(s);
                }
            }
            if(!c.isPositionFixed()) {
                TVector pos = d.getOriginalPosition().difY(d.getBufferedOriginalDisplaySize(), terminalSize);
                if(c.isNoOutScreenMoving()) {
                    TVector size = c.getAbsoluteSize();
                    TVector endCorner = pos.c().add(size);
                    if(endCorner.x > terminalSize.x) {
                        int offset = endCorner.x - terminalSize.x;
                        offset++;
                        pos.subtractWidth(offset);
                    }
                    if(endCorner.y > terminalSize.y) {
                        int offset = endCorner.y - terminalSize.y;
                        offset++;
                        pos.setHeight(offset);
                    }
                }
                d.setCurrentPosition(pos);
            }
        }
    }

    @Override
    public void display(TerminalScreen screen) {
        TDisplayDrawer.addTaskToQueue(new DrawQueue() {
            @Override
            public void run(TDisplayDrawer ignored) {
                Terminal t = screen.getApplication().getTerminal();
                t.setColor(screen.getForegroundColor(), screen.getBackgroundColor());
                try {
                    t.clearScreen();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                TComponent selected = screen.getSelectedComponent();
                if(selected != null) {
                    screen.selectComponent(null);
                }
                for(TComponent c : screen.getComponents()) {
                    TDisplayDrawer drawer = screen.getDrawer(c);
                    render(c, screen, drawer);
                }
                if(selected != null) {
                    screen.selectComponent(selected);
                }
            }
        });
    }

    @Override
    public void select(TComponent component, TerminalScreen screen, TDisplayDrawer drawer) {
        drawer.addToQueue(new DrawQueue() {
            @Override
            public void run(TDisplayDrawer drawer) {
                component.select(drawer, screen);
            }
        });

    }

    @Override
    public void deselect(TComponent component, TerminalScreen screen, TDisplayDrawer drawer) {
        drawer.addToQueue(new DrawQueue() {
            @Override
            public void run(TDisplayDrawer drawer) {
                component.deselect(drawer, screen);
            }
        });
    }

    @Override
    public Cancellable input(TComponent component, TerminalScreen screen, TDisplayDrawer drawer, KeyInput keyInput) {
        Cancellable cancellable = new Cancellable(false);
        component.keyInput(drawer, screen, keyInput, cancellable);
        return cancellable;
    }

    @Override
    public void onComponentAdd(TComponent component, TerminalScreen screen) {

    }

    @Override
    public void onComponentRemove(TComponent component, TerminalScreen screen) {

    }

    @Override
    public void load(TerminalScreen screen) {
        if(dockingMap == null) dockingMap = new HashMap<>();
        if(renderHandles == null) renderHandles = new HashMap<>();
    }

    @Override
    public void unload(TerminalScreen screen) {
        dockingMap = new HashMap<>();
        renderHandles = new HashMap<>();
    }

    public void setDockingPoint(TComponent component, DockType dockType) {
        dockingMap.put(component, dockType);
    }

    public DockType getDockingPoint(TComponent component) {
        if(dockingMap.containsKey(component)) return dockingMap.get(component);
        return DockType.CENTER;
    }


    public void setRenderVectorHandler(TComponent component, RenderVectorHandler handler) {
        renderHandles.put(component, handler);
    }

    public RenderVectorHandler getRenderVectorHandler(TComponent component) {
        if(renderHandles.containsKey(component)) return renderHandles.get(component);
        return null;
    }
}
