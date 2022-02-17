package net.pascal.terminal.layout;

import net.pascal.terminal.Terminal;
import net.pascal.terminal.application.TDisplayDrawer;
import net.pascal.terminal.application.TerminalScreen;
import net.pascal.terminal.component.TComponent;
import net.pascal.terminal.key.KeyInput;
import net.pascal.terminal.util.Cancellable;
import net.pascal.terminal.util.TVector;

public abstract class TLayout {

    public abstract void render(TComponent component, TerminalScreen screen, TDisplayDrawer drawer);

    public abstract void remove(TComponent component, TerminalScreen screen, TDisplayDrawer drawer);

    public abstract void resizeAction(TerminalScreen screen, int ox, int oy, int nx, int ny);

    public abstract void display(TerminalScreen screen);

    public abstract void select(TComponent component, TerminalScreen screen, TDisplayDrawer drawer);

    public abstract void deselect(TComponent component, TerminalScreen screen, TDisplayDrawer drawer);

    public abstract Cancellable input(TComponent component, TerminalScreen screen, TDisplayDrawer drawer, KeyInput keyInput);

    public abstract void onComponentAdd(TComponent component, TerminalScreen screen);

    public abstract void onComponentRemove(TComponent component, TerminalScreen screen);

    public abstract void load(TerminalScreen screen);

    public abstract void unload(TerminalScreen screen);

}
