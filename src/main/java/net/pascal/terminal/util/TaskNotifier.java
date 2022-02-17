package net.pascal.terminal.util;

public class TaskNotifier {

    private boolean b = false;

    public synchronized void waitTask() {
        while (b) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        b = true;
    }

    public synchronized void notifyTask() {
        notifyAll();
        b = false;
    }

}
