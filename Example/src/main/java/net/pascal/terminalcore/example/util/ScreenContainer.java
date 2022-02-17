package net.pascal.terminalcore.example.util;

import net.pascal.terminal.application.TerminalScreen;
import net.pascal.terminal.component.TComponent;
import net.pascal.terminal.util.TVector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScreenContainer {

    private HashMap<TComponent, TVector> components;
    private TerminalScreen screen;

    public ScreenContainer(TerminalScreen screen) {
        components = new HashMap<>();
        this.screen = screen;
    }


    public void addComponent(TComponent c, TVector pos) {
        c.setPositionFixed(true);
        components.put(c, pos);
    }

    public void removeComponent(TComponent c) {
        components.remove(c);
    }

    public TVector getPosition(TComponent component) {
        return components.get(component);
    }

    public void set() {
        for(TComponent tc : components.keySet()) {
            screen.addComponent(tc, getPosition(tc));
        }
    }

    public void remove() {
        for(TComponent tc : components.keySet()) {
            screen.removeComponent(tc);
        }
    }


}
