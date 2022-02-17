package net.pascal.terminal.component;

import net.pascal.terminal.application.DrawQueue;
import net.pascal.terminal.application.TDisplayDrawer;
import net.pascal.terminal.application.TerminalScreen;
import net.pascal.terminal.key.KeyInput;
import net.pascal.terminal.util.Cancellable;
import net.pascal.terminal.util.TVector;

public class TProgressBar extends TComponent{

    private int value;


    private String obtainedSymbol;
    private String freeSymbol;

    public TProgressBar(int length) {
        super(new TVector(length + 2, 1));
        value = 0;
        obtainedSymbol = "|";
        freeSymbol = "-";
    }

    public String getFreeSymbol() {
        return freeSymbol;
    }

    public String getObtainedSymbol() {
        return obtainedSymbol;
    }

    public void setFreeSymbol(String freeSymbol) {
        this.freeSymbol = freeSymbol;
    }

    public void setObtainedSymbol(String obtainedSymbol) {
        this.obtainedSymbol = obtainedSymbol;
    }

    public int getLength() {
        return getSize().getWidth()-2;
    }

    public int getTotalLength() {
        return getSize().getWidth();
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        if(this.value < 0) this.value = 0;
        else if(this.value > 100) this.value = 100;
        if(isDisplaying()) {
            TerminalScreen ts = getCurrentDisplayingScreen();
            if(ts != null) {
                ts.getDrawer(this).addToQueue(new DrawQueue() {
                    @Override
                    public void run(TDisplayDrawer drawer) {
                        draw(ts.getDrawer(TProgressBar.this), ts);
                    }
                });

            }
        }
    }

    @Override
    public void draw(TDisplayDrawer drawer, TerminalScreen screen) {
        drawer.point();
        drawer.reset();
        drawer.loadColors();
        drawer.writeAtPosition(buildString(getSize().getWidth()-2, value));
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
        return false;
    }

    private String buildString(int length, double percentage) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        long ct = Math.round((float) length/100F*percentage);
        for(int i = 0;i<length;i++) {
            if(i < ct) {
                sb.append(obtainedSymbol);
            } else {
                sb.append(freeSymbol);
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
