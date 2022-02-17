package net.pascal.terminal;



import net.pascal.terminal.application.TDisplayDrawer;
import net.pascal.terminal.application.TerminalApplication;
import net.pascal.terminal.application.TerminalScreen;
import net.pascal.terminal.component.*;
import net.pascal.terminal.layout.RelativeLayout;
import net.pascal.terminal.text.BackgroundColor;
import net.pascal.terminal.text.ForegroundColor;
import net.pascal.terminal.util.DockType;
import net.pascal.terminal.util.TVector;
import net.pascal.terminal.util.TerminalCharsetEncoding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException, Exception {
        TerminalCharsetEncoding.enableUTF8();
        Terminal terminal = new Terminal();
        TerminalApplication application = new TerminalApplication(terminal);

        TPaint filledRect = new TPaint(new TVector(application.getCachedTerminalSize().x-2, 2)) {
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
            }
        };
        filledRect.setBackgroundColor(BackgroundColor.WHITE);

        TerminalScreen screen = new TerminalScreen(application);
        screen.setForegroundColor(ForegroundColor.WHITE_BRIGHT);
        screen.setBackgroundColor(BackgroundColor.RGB_BLUE);
        RelativeLayout rl = new RelativeLayout();
        screen.setLayout(rl);
        TLabel label = new TLabel("lul");
        TButton b1 = new TButton("test1");
        b1.setClickEvent(new Runnable() {
            @Override
            public void run() {
                terminal.setTitle("T1: " + Math.random());
            }
        });
        TButton b2 = new TButton("test2");
        b2.setClickEvent(new Runnable() {
            @Override
            public void run() {
                terminal.setTitle("T2: " + Math.random());
            }
        });

        TProgressBar b = new TProgressBar(32);
        label.setBackgroundColor(BackgroundColor.RED);
        screen.addComponent(label, new TVector(4, 4));
        screen.addComponent(b1, new TVector(30, 6));
        screen.addComponent(new TCheckBox("Enabled"), new TVector(30, 9));
        screen.addComponent(b, new TVector(4, 18));
        rl.setDockingPoint(label, DockType.LEFT);
        rl.setDockingPoint(b1, DockType.TOP);
        //rl.setDockingPoint(filledRect, DockType.BOTTOM);
        application.openScreen(screen);
        int i = 0;
        while (true) {
            Thread.sleep(100L);
            label.setText("R: " + Math.random() + "\n" + "R2: " + Math.random() + "\n\n" + "R3: " + Math.random());
            b.setValue(b.getValue()+5);
            i++;
            if(i == 5) break;
        }
    }

}
