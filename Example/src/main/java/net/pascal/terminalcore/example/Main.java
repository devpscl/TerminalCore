package net.pascal.terminalcore.example;

import net.pascal.terminal.Terminal;
import net.pascal.terminal.application.TerminalApplication;
import net.pascal.terminal.text.ForegroundColor;
import net.pascal.terminal.text.TStringBuilder;
import net.pascal.terminal.util.TerminalCharsetEncoding;
import net.pascal.terminalcore.example.screen.LoginScreen;

import java.awt.*;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        TerminalCharsetEncoding.enableUTF8();
        Terminal terminal = new Terminal();
        TerminalApplication application = new TerminalApplication(terminal);
        application.openScreen(new LoginScreen(application));
    }



}
