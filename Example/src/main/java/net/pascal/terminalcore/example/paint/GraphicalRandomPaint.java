package net.pascal.terminalcore.example.paint;

import net.pascal.terminal.application.TDisplayDrawer;
import net.pascal.terminal.application.TerminalScreen;
import net.pascal.terminal.component.TPaint;
import net.pascal.terminal.util.TVector;

import java.util.Random;

public class GraphicalRandomPaint extends TPaint {


    public GraphicalRandomPaint(TVector size) {
        super(size);
    }

    @Override
    public void paint(TDisplayDrawer drawer, TerminalScreen screen) {
        int x = drawer.getCurrentPosition().x;
        int y = drawer.getCurrentPosition().y;
        TVector size = getAbsoluteSize();
        Random r = new Random();
        for(int i = 0;i<size.y;i++) {
            for(int i2 = 0;i2<size.x;i2++) {
                boolean b = r.nextBoolean();
                char c = b ? '#' : ' ';
                drawer.writeAtPosition(String.valueOf(c), new TVector(x+i2,y+i));
            }
        }
    }



}
