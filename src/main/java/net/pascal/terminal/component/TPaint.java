package net.pascal.terminal.component;

import net.pascal.terminal.application.TDisplayDrawer;
import net.pascal.terminal.application.TerminalScreen;
import net.pascal.terminal.key.KeyInput;
import net.pascal.terminal.util.Cancellable;
import net.pascal.terminal.util.TVector;

public abstract class TPaint extends TComponent{

    public TPaint(TVector size) {
        super(size);
    }

    @Override
    public void draw(TDisplayDrawer drawer, TerminalScreen screen) {
        drawer.point();
        drawer.reset();
        drawer.loadColors();
        paint(drawer, screen);
        drawer.dispose();
    }

    @Override
    public void select(TDisplayDrawer drawer, TerminalScreen screen) {

    }

    @Override
    public void deselect(TDisplayDrawer drawer, TerminalScreen screen) {

    }

    @Override
    public void keyInput(TDisplayDrawer drawer, TerminalScreen screen, KeyInput keyInput, Cancellable outKeyInputCancelling) {

    }

    @Override
    public boolean isStretchable() {
        return true;
    }

    public abstract void paint(TDisplayDrawer drawer, TerminalScreen screen);

}
