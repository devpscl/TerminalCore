package net.pascal.terminal.key;

public class KeyInput {

    private final char character;
    private final boolean letterKey;

    public KeyInput(char character, boolean b) {
        this.character = character;
        this.letterKey = b;
    }

    public boolean isLetterKey() {
        return letterKey;
    }

    public boolean isNonSpecialKey() {
        return character == '!' || character == '.' || character == '\"' || character == '§' || character == '$' ||
                character == '%' || character == '&' || character == '/' || character == '(' || character == ')' ||
                character == '=' || character == '-' || character == ';' || character == ',' || character == '\'' ||
                character == '?' || character == '\\' || character == '}' || character == '{' ||
                character == '[' || character == ']' || character == '<' || character == '>' || character == '|' ||
                character == '#' || character == '+' || character == '*' || character == '~' || character == 'ß' || character == '_'
                || character == ':' || character == '°'|| character == '^';
    }

    public boolean isNumeric() {
        return Character.isDigit(character);
    }

    public char getCharacter() {
        return character;
    }

    public boolean isUmlaut() {
        return character == 'ü' || character == 'ä' || character == 'ö' || character == 'Ü' || character == 'Ä' ||
                character == 'Ö';
    }
}
