package net.pascal.terminalcore.example.screen;

import net.pascal.terminal.application.TDisplayDrawer;
import net.pascal.terminal.application.TerminalApplication;
import net.pascal.terminal.application.TerminalScreen;
import net.pascal.terminal.component.*;
import net.pascal.terminal.key.ControlKeyInput;
import net.pascal.terminal.key.ControlKeyType;
import net.pascal.terminal.key.KeyInput;
import net.pascal.terminal.layout.RelativeLayout;
import net.pascal.terminal.text.BackgroundColor;
import net.pascal.terminal.text.ForegroundColor;
import net.pascal.terminal.util.DockType;
import net.pascal.terminal.util.RenderVectorHandler;
import net.pascal.terminal.util.TVector;
import net.pascal.terminal.util.event.KeyEventHandler;
import net.pascal.terminalcore.example.paint.InfoBoxPaint;
import net.pascal.terminalcore.example.util.ApplicationUtil;
import net.pascal.terminalcore.example.util.ScreenContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class InfoScreen extends TerminalScreen {

    private RelativeLayout pane;
    private InfoBoxPaint infoBoxPaint;
    private TListBox list;
    private ScreenContainer currentContainer;

    private LinkedHashMap<String, ScreenContainer> containerHashMap;

    public InfoScreen(TerminalApplication application) {
        super(application);
        containerHashMap = new LinkedHashMap<>();
        application.getTerminal().setTitle("Info");
        setForegroundColor(ForegroundColor.WHITE_BRIGHT);
        setBackgroundColor(BackgroundColor.getColorFrom256ColorSet(27));
        pane = new RelativeLayout();
        setLayout(pane);
        loadContainers();
        initialize();
        addKeyListener(new KeyEventHandler() {
            @Override
            public void onKey(KeyInput input) {
                if(input instanceof ControlKeyInput) {
                    ControlKeyInput cki = (ControlKeyInput) input;
                    if(cki.getType() == ControlKeyType.F5) {
                        display();
                    } else if(cki.getType() == ControlKeyType.F9) {
                        ApplicationUtil.exit(application);
                    }
                }
            }
        });
        selectComponent(list);
    }

    private void loadContainers() {
        TVector corner = new TVector(50, 6);
        ScreenContainer textFieldInfoContainer = new ScreenContainer(this);
        String textFieldInfo = "A limited text can be stored in\nthe text field, which can be called up.\n" +
                "The component supports the left/right\nmovement of the text cursor.";
        TLabel textFieldInfoLabel = new TLabel(textFieldInfo);
        TTextField textField = new TTextField(textFieldInfoLabel.getAbsoluteSize().x);
        textField.setForegroundColor(ForegroundColor.BLACK);
        textField.setBackgroundColor(BackgroundColor.WHITE);
        textFieldInfoContainer.addComponent(textFieldInfoLabel, corner);
        textFieldInfoContainer.addComponent(textField, corner.clone().addHeight(textFieldInfoLabel.getAbsoluteSize().y + 2));


        ScreenContainer textAreaInfoContainer = new ScreenContainer(this);
        String textAreaInfo = "In the text area a long text\nwith line break can be stored.\nThe component supports\n" +
                "the left/right/up/down movement of the text\ncursor and a scrollbar for the lines.";
        TLabel textAreaInfoLabel = new TLabel(textAreaInfo);
        TFullTextArea textArea = new TFullTextArea(new TVector(textAreaInfoLabel.getAbsoluteSize().x, 8));
        textArea.setForegroundColor(ForegroundColor.BLACK);
        textArea.setBackgroundColor(BackgroundColor.WHITE);
        //textArea.setSelectColors(ForegroundColor.WHITE, BackgroundColor.BLACK_BRIGHT);
        textAreaInfoContainer.addComponent(textAreaInfoLabel, corner);
        textAreaInfoContainer.addComponent(textArea, corner.clone().addHeight(textAreaInfoLabel.getAbsoluteSize().y + 2));

        ScreenContainer checkboxInfoContainer = new ScreenContainer(this);
        String checkboxInfo = "A checkbox is a component to set two states\nwhether true or false. this can also be deposited\nwith a text.";
        TLabel checkboxInfoLabel = new TLabel(checkboxInfo);
        TCheckBox checkbox = new TCheckBox("Custom Tag");
        checkboxInfoContainer.addComponent(checkboxInfoLabel, corner);
        checkboxInfoContainer.addComponent(checkbox, corner.clone().addHeight(checkboxInfoLabel.getAbsoluteSize().y + 2));

        ScreenContainer progressBarInfoContainer = new ScreenContainer(this);
        String progressBarInfo = "A ProgressBar shows a percentage of 100%.\nHere the value is also used as a percentage.\n" +
                "It is also possible to use own characters for\nthe achieved progress or not achieved progress.";
        TLabel progressBarInfoLabel = new TLabel(progressBarInfo);
        TProgressBar progressBar = new TProgressBar(progressBarInfoLabel.getAbsoluteSize().x);
        progressBar.setValue(33);
        progressBarInfoContainer.addComponent(progressBarInfoLabel, corner);
        progressBarInfoContainer.addComponent(progressBar, corner.clone().addHeight(progressBarInfoLabel.getAbsoluteSize().y + 2));

        containerHashMap.put("TextField", textFieldInfoContainer);
        containerHashMap.put("TextArea", textAreaInfoContainer);
        containerHashMap.put("CheckBox", checkboxInfoContainer);
        containerHashMap.put("ProgressBar", progressBarInfoContainer);
        currentContainer = textFieldInfoContainer;
        currentContainer.set();
    }

    private void initialize() {
        infoBoxPaint = new InfoBoxPaint(new TVector(getApplication().getCachedTerminalSize().x, 1));
        infoBoxPaint.setForegroundColor(ForegroundColor.BLACK);
        infoBoxPaint.setBackgroundColor(BackgroundColor.WHITE);

        list = new TListBox(new TVector(24, getApplication().getCachedTerminalSize().y-6), new ArrayList<>(containerHashMap.keySet()));
        list.setSelectEvent(new TListBox.SelectEvent() {
            @Override
            public void onSelect(String s, int index) {
                currentContainer.remove();
                currentContainer = containerHashMap.get(s);
                currentContainer.set();
            }
        });
        list.setPositionFixed(true);

        addComponent(infoBoxPaint, new TVector(1, 0));
        addComponent(list, new TVector(2, 5));

        pane.setDockingPoint(list, DockType.LEFT);
        pane.setDockingPoint(infoBoxPaint, DockType.TOP);
        pane.setRenderVectorHandler(list, new ListVectorHandler());
    }

    public static class ListVectorHandler implements RenderVectorHandler {

        @Override
        public TVector getSize(TComponent component, TVector originalPosition, TVector originalSize, TDisplayDrawer drawer, TVector displaySize) {
            int endX = 24;
            int endY = displaySize.y - 2;

            return new TVector(endX, endY).subtract(originalPosition);
        }

        @Override
        public TVector getPosition(TComponent component, TVector originalPosition, TVector originalSize, TDisplayDrawer drawer, TVector displaySize) {
            return originalPosition;
        }
    }





}
