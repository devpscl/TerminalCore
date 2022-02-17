package net.pascal.terminalcore.example.screen;

import net.pascal.terminal.application.TerminalApplication;
import net.pascal.terminal.application.TerminalScreen;
import net.pascal.terminal.component.TButton;
import net.pascal.terminal.component.TLabel;
import net.pascal.terminal.component.TPasswordField;
import net.pascal.terminal.component.TTextField;
import net.pascal.terminal.key.ControlKeyInput;
import net.pascal.terminal.key.ControlKeyType;
import net.pascal.terminal.key.KeyInput;
import net.pascal.terminal.layout.RelativeLayout;
import net.pascal.terminal.text.BackgroundColor;
import net.pascal.terminal.text.ForegroundColor;
import net.pascal.terminal.util.DockType;
import net.pascal.terminal.util.TVector;
import net.pascal.terminal.util.event.KeyEventHandler;
import net.pascal.terminalcore.example.paint.GraphicalRandomPaint;
import net.pascal.terminalcore.example.paint.InfoBoxPaint;
import net.pascal.terminalcore.example.util.ApplicationUtil;

public class LoginScreen extends TerminalScreen {

    private static final String loginName = "abc";
    private static final String loginPassword = "1234";

    private InfoBoxPaint infoBoxPaint;
    private TTextField nameField;
    private TPasswordField passwordField;
    private TLabel passwordLabel;
    private TLabel nameLabel;
    private TLabel infoLabel;
    private TButton loginButton;
    private TLabel loginState;
    private GraphicalRandomPaint graphicalRandomPaint;
    private final TerminalApplication application;
    private final RelativeLayout pane;

    public LoginScreen(TerminalApplication application) {
        super(application);
        this.application = application;
        application.getTerminal().setTitle("Login");
        setForegroundColor(ForegroundColor.WHITE_BRIGHT);
        setBackgroundColor(BackgroundColor.getColorFrom256ColorSet(27));
        pane = new RelativeLayout();
        setLayout(pane);
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
    }

    private void initialize() {
        infoBoxPaint = new InfoBoxPaint(new TVector(application.getCachedTerminalSize().x, 1));
        infoBoxPaint.setForegroundColor(ForegroundColor.BLACK);
        infoBoxPaint.setBackgroundColor(BackgroundColor.WHITE);

        nameField = new TTextField(32);
        nameField.setBackgroundColor(BackgroundColor.WHITE);
        nameField.setForegroundColor(ForegroundColor.BLACK);
        nameField.setPositionFixed(true);

        passwordField = new TPasswordField(32);
        passwordField.setBackgroundColor(BackgroundColor.WHITE);
        passwordField.setForegroundColor(ForegroundColor.BLACK);
        passwordField.setPositionFixed(true);

        infoLabel = new TLabel("This is a simple login view as screen.\n" +
                "The loginname is abc and the secret\n" +
                "password is 1234.\n" +
                "This view can be resized and all\n" +
                "components will be moved to the\n" +
                "owned site of the display.");
        infoLabel.setNoOutScreenMoving(true); //Label in the whole screen at resize

        loginButton = new TButton("Login");
        loginButton.setSelectColors(BackgroundColor.WHITE, ForegroundColor.RED);
        loginButton.setPositionFixed(true);
        loginButton.setClickEvent(new Runnable() {
            @Override
            public void run() {
                loginInteract();
            }
        });
        loginState = new TLabel("");
        loginState.setPositionFixed(true);

        nameLabel = new TLabel("Name:");
        nameLabel.setPositionFixed(true);
        passwordLabel = new TLabel("Password:");
        passwordLabel.setPositionFixed(true);




        TVector dSize = application.getCachedTerminalSize();

        graphicalRandomPaint = new GraphicalRandomPaint(new TVector(dSize.x, 10));

        addComponent(infoBoxPaint, new TVector(1, 0)); //lowest priority
        addComponent(nameLabel, new TVector(2, 5));
        addComponent(nameField, new TVector(2, 6));
        addComponent(passwordLabel, new TVector(2, 9));
        addComponent(passwordField, new TVector(2, 10)); //higher priority
        addComponent(loginButton, new TVector(2, 12));
        addComponent(loginState, new TVector(4 + loginButton.getAbsoluteSize().x, 12));
        addComponent(infoLabel, new TVector(dSize.x-3-infoLabel.getAbsoluteSize().x, 5));
        addComponent(graphicalRandomPaint, new TVector(1, dSize.y-10));  //highest priority


        pane.setDockingPoint(infoBoxPaint, DockType.TOP);
        pane.setDockingPoint(nameLabel, DockType.LEFT);
        pane.setDockingPoint(nameField, DockType.LEFT);
        pane.setDockingPoint(passwordLabel, DockType.LEFT);
        pane.setDockingPoint(loginButton, DockType.LEFT);
        pane.setDockingPoint(loginState, DockType.LEFT);
        pane.setDockingPoint(infoLabel, DockType.TOP);
        pane.setDockingPoint(graphicalRandomPaint, DockType.BOTTOM);

    }



    private void loginInteract() {

        String name = nameField.getText();
        String password = passwordField.getPassword();

        if(name.equals(loginName) && password.equals(loginPassword)) {
            loginState.setForegroundColor(ForegroundColor.GREEN);
            loginState.setText("Logged in!");
            application.switchScreen(new InfoScreen(application));
        } else {
            loginState.setForegroundColor(ForegroundColor.RED);
            loginState.setText("Invalid login information. Try again!");
        }
    }
}