package net.pascal.terminal.util;

public class Cancellable {

    private boolean b;

    public Cancellable(boolean b) {
        this.b = b;
    }

    public Cancellable() {
        this.b = false;
    }

    public void setCancel(boolean b) {
        this.b = b;
    }

    public boolean isCancelled() {
        return b;
    }
}
