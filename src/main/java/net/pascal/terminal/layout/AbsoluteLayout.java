package net.pascal.terminal.layout;

import net.pascal.terminal.Terminal;
import net.pascal.terminal.application.DrawQueue;
import net.pascal.terminal.application.TDisplayDrawer;
import net.pascal.terminal.application.TerminalScreen;
import net.pascal.terminal.component.TComponent;
import net.pascal.terminal.key.KeyInput;
import net.pascal.terminal.util.Cancellable;
import net.pascal.terminal.util.DockType;

import java.io.IOException;

public class AbsoluteLayout extends TLayout{

    private TerminalScreen screen;

    @Override
    public void render(TComponent component, TerminalScreen screen, TDisplayDrawer drawer) {
        drawer.addToQueue(new DrawQueue() {
            @Override
            public void run(TDisplayDrawer drawer) {
                component.setDisplayingScreen(screen);
                component.draw(drawer, screen);
            }
        });
    }

    @Override
    public void remove(TComponent component, TerminalScreen screen, TDisplayDrawer drawer) {
        drawer.remove();
    }

    @Override
    public void resizeAction(TerminalScreen screen, int ox, int oy, int nx, int ny) {
        for(TComponent c : screen.getComponents()) {
            c.onResize(screen.getDrawer(c), screen, c.getAbsoluteSize(), screen.getDrawer(c).getCurrentPosition());
        }
        screen.display();
    }

    @Override
    public void display(TerminalScreen screen) {
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
        this.screen = screen;
    }

    @Override
    public void unload(TerminalScreen screen) {
        this.screen = null;
    }
}
