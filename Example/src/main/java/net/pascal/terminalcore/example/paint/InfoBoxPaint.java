package net.pascal.terminalcore.example.paint;

import net.pascal.terminal.application.TDisplayDrawer;
import net.pascal.terminal.application.TerminalScreen;
import net.pascal.terminal.component.TPaint;
import net.pascal.terminal.util.TVector;

import java.nio.charset.StandardCharsets;

public class InfoBoxPaint extends TPaint {

    public InfoBoxPaint(TVector size) {
        super(size);
    }

    @Override
    public void paint(TDisplayDrawer drawer, TerminalScreen screen) {
        int x = drawer.getCurrentPosition().x;
        int y = drawer.getCurrentPosition().y;
        TVector size = getAbsoluteSize();
        for(int i = 0;i<size.y;i++) {
            for(int i2 = 0;i2<size.x;i2++) {
                drawer.writeAtPosition(" ", new TVector(x+i2,y+i));
            }
        }
        drawer.point();
        drawer.write(" Terminal    [F5] Reload    [F9] Exit    ←↑→↓ Move    [Enter] Interact");
    }
}
