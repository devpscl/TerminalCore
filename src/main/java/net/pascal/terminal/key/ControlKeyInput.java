package net.pascal.terminal.key;

public class ControlKeyInput extends KeyInput {

    private ControlKeyType type;

    public ControlKeyInput(char character, ControlKeyType type) {
        super(character, false);
        this.type = type;
    }

    public ControlKeyType getType() {
        return type;
    }
}
