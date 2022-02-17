package net.pascal.terminal.util;

public class ScreenFrame {

    private char leftTopCornerSymbol;
    private char rightTopCornerSymbol;
    private char leftBottomCornerSymbol;
    private char rightBottomCornerSymbol;
    private char topSymbol;
    private char leftSymbol;
    private String title;

    public ScreenFrame(char leftTopCornerSymbol, char rightTopCornerSymbol, char leftBottomCornerSymbol, char rightBottomCornerSymbol
            , char topSymbol, char leftSymbol, String title) {
        this.leftTopCornerSymbol = leftTopCornerSymbol;
        this.rightTopCornerSymbol = rightTopCornerSymbol;
        this.leftBottomCornerSymbol = leftBottomCornerSymbol;
        this.rightBottomCornerSymbol = rightBottomCornerSymbol;
        this.topSymbol = topSymbol;
        this.leftSymbol = leftSymbol;
        this.title = title;
    }

    public char getLeftTopCornerSymbol() {
        return leftTopCornerSymbol;
    }

    public char getRightTopCornerSymbol() {
        return rightTopCornerSymbol;
    }

    public char getLeftBottomCornerSymbol() {
        return leftBottomCornerSymbol;
    }

    public char getRightBottomCornerSymbol() {
        return rightBottomCornerSymbol;
    }

    public char getTopSymbol() {
        return topSymbol;
    }

    public char getLeftSymbol() {
        return leftSymbol;
    }

    public String getTitle() {
        return title;
    }
}
