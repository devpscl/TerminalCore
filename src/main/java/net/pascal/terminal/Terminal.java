package net.pascal.terminal;


import net.pascal.terminal.key.ControlKeyInput;
import net.pascal.terminal.key.ControlKeyType;
import net.pascal.terminal.key.KeyInput;
import net.pascal.terminal.text.Color;
import net.pascal.terminal.text.ForegroundColor;
import net.pascal.terminal.text.TextDecoration;
import net.pascal.terminal.util.MoveDirection;
import net.pascal.terminal.util.OperationSystemUtil;
import net.pascal.terminal.util.TVector;
import net.pascal.terminal.util.TerminalCharsetEncoding;

import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Terminal {

    private final TerminalHandle handle;
    private BufferedReader reader;
    private PrintStream out;
    private String title;
    private boolean cursorVisible;

    public Terminal(boolean utf8) throws IOException {
        if(utf8) {
            if(!TerminalCharsetEncoding.isEnabledUTF8()) {
                try {
                    TerminalCharsetEncoding.enableUTF8();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(OperationSystemUtil.isWindows()) {
            if(!TerminalHandle.terminal32DLL.exists()) {
                throw new FileNotFoundException(TerminalHandle.terminal32DLL.getName() + " for loading");
            }
        } else if(OperationSystemUtil.isLinux()) {
            if(!TerminalHandle.terminalUnixSO.exists()) {
                throw new FileNotFoundException(TerminalHandle.terminalUnixSO.getName() + " for loading");
            }
        } else {
            throw new IllegalStateException("Operation system is not supported!");
        }

        handle = new TerminalHandle();
        this.out = System.out;
        Terminal terminal = this;
        System.setOut(new PrintStream(out) {

            @Override
            public void println(String x) {
                terminal.writeLine(x);
            }

            @Override
            public void print(String s) {
                terminal.write(s);
            }

            @Override
            public void println(Object o) {
                terminal.writeLine(String.valueOf(o));
            }

            @Override
            public void print(Object o) {
                terminal.write(String.valueOf(o));
            }
        });

        reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            clearScreen();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cursorVisible = true;
    }

    public Terminal() throws IOException {
        this(false);
    }

    /**
     * Clear terminal screen.
     *
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    public void clearScreen() throws IOException, InterruptedException {
        if(OperationSystemUtil.isWindows()) {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } else {
            new ProcessBuilder("/bin/bash", "-c", "clear").inheritIO().start().waitFor();
        }

    }

    public void beep() throws IOException, InterruptedException {
        if(OperationSystemUtil.isWindows()) {
            Toolkit.getDefaultToolkit().beep();
        } else {
            new ProcessBuilder("/bin/bash", "-c", "echo -e \"\07\"").inheritIO().start().waitFor();
        }

    }


    public void saveCursorPosition() {
        writeRaw("\033[s");
    }

    public void restoreCursorPosition() {
        writeRaw("\033[u");
    }

    /**
     * Read line of Terminal.
     *
     * */
    public String readLine() throws IOException {
        return reader.readLine();
    }

    public void setTitle(String s) {
        handle.setTitle(s);
        this.title = s;
    }

    public String getTitle() {
        return title;
    }

    public TerminalHandle getHandle() {
        return handle;
    }

    public TVector getSize() {
        return new TVector(handle.getSize());
    }



    /**
     * Clear from cursor until end of screen.
     */
    public void clearCursorToEndScreen() {
        writeRaw("\u001b[0J");
    }

    /**
     * Clear from cursor to start of screen.
     */
    public void clearCursorToStartScreen() {
        writeRaw("\u001b[1J");
    }

    /**
     * Clear full line.
     */
    public void clearLine() {
        writeRaw("\u001b[2K");
    }

    /**
     * Read input character
     *
     * */
    public int getch() {
        return handle.getch();
    }

    public void setCursorVisible(boolean b) {
        handle.setCursorVisible(b);
        cursorVisible = b;
    }

    public boolean getCursorVisible() {
        return cursorVisible;
    }

    /**
     * Clear from cursor to end of line
     */
    public void clearCursorToEndLine() {
        writeRaw("\u001b[0K");
    }

    /**
     * Clear from cursor to start of line.
     */
    public void clearCursorToStartLine() {
        writeRaw("\u001b[0K");
    }

    /**
     * Write terminalstring.
     *
     * @param str the str
     */
    public void write(String str) {
        String t = str.replaceAll("\n\n", "\n \n");
        out.print(t);
    }

    /**
     * Write line.
     *
     * @param str the str
     */
    public void writeLine(String str) {
        write(str + "\n");
    }

    /**
     * Write raw string.
     *
     * @param s the s
     */
    public void writeRaw(String s) {
        out.print(s);
    }

    /**
     * Sets color.
     *
     * @param colors the colors
     */
    public void setColor(Color...colors) {
        for(Color c : colors) {
            out.print(c.getAsciiCode());
        }
    }

    /**
     * Sets text decoration.
     *
     * @param t the t
     */
    public void setTextDecoration(TextDecoration t) {
        out.print(t.getAsciiCode());
    }

    /**
     * Sets console cursor.
     *
     * @param vector the vector
     */
    public void setConsoleCursor(TVector vector) {
        if(OperationSystemUtil.isWindows()) {
            writeRaw("\033[" + vector.getRows() + ";" + vector.getColumns() + "H");
        } else {
            writeRaw("\033[" + vector.getRows() + ";" + vector.getColumns() + "f");
        }
    }

    public boolean isUTF8CharsetEncoding() {
        return TerminalCharsetEncoding.isEnabledUTF8();
    }



    /**
     * Move console cursor to position.
     *
     * @param moveDirection the move direction
     * @param i             the
     */
    public void moveConsoleCursor(MoveDirection moveDirection, int i) {
        if(moveDirection == MoveDirection.UP) {
            writeRaw("\033[" + i + "A");
        } else if(moveDirection == MoveDirection.DOWN) {
            writeRaw("\033[" + i + "B");
        } else if(moveDirection == MoveDirection.LEFT) {
            writeRaw("\033[" + i + "D");
        } else if(moveDirection == MoveDirection.RIGHT) {
            writeRaw("\033[" + i + "C");
        }
    }

    public void resetInputBuffer() {
        handle.resetInputBuffer();
    }

    public void dispose() {
        writeRaw(ForegroundColor.RESET.getAsciiCode());
        resetInputBuffer();
    }

    public void writeAtPosition(String s, TVector vec) {
        write("\033[" + vec.getRows() + ";" + vec.getColumns() + "H" + s);
    }

    public KeyInput readInput() {
        int ch = getch();
        char c = (char) ch;
        if(OperationSystemUtil.isWindows()) {
            if(ch == 9) {
                return new ControlKeyInput(c, ControlKeyType.TAB);
            } else if(ch == 13) {
                return new ControlKeyInput(c, ControlKeyType.ENTER);
            } else if(ch == 8) {
                return new ControlKeyInput(c, ControlKeyType.DELETE);
            } else if(ch == 27) {
                return new ControlKeyInput(c, ControlKeyType.ESCAPE);
            } else if(ch == 32) {
                return new ControlKeyInput(c, ControlKeyType.SPACE);
            } else if(ch == 224) {
                int result = getch();
                char rc = (char) result;
                if(result == 72) {
                    return new ControlKeyInput(rc, ControlKeyType.ARROW_UP);
                } else if(result == 77) {
                    return new ControlKeyInput(rc, ControlKeyType.ARROW_RIGHT);
                } else if(result == 80) {
                    return new ControlKeyInput(rc, ControlKeyType.ARROW_DOWN);
                } else if(result == 75) {
                    return new ControlKeyInput(rc, ControlKeyType.ARROW_LEFT);
                } else if(result == 83) {
                    return new ControlKeyInput(rc, ControlKeyType.REMOVE);
                } else if(result == 71) {
                    return new ControlKeyInput(rc, ControlKeyType.POS1);
                } else if(result == 89) {
                    return new ControlKeyInput(rc, ControlKeyType.END);
                } else if(result == 73) {
                    return new ControlKeyInput(rc, ControlKeyType.PIC_1);
                } else if(result == 81) {
                    return new ControlKeyInput(rc, ControlKeyType.PIC_2);
                } else if(result == 82) {
                    return new ControlKeyInput(rc, ControlKeyType.PASTE);
                } else if(result == 133) {
                    return new ControlKeyInput(rc, ControlKeyType.F11);
                } else if(result == 134) {
                    return new ControlKeyInput(rc, ControlKeyType.F12);
                } else {
                    return new KeyInput(rc, false);
                }
            } else if(ch == 0) {
                int result = getch();
                char rc = (char) result;
                if(result == 59) {
                    return new ControlKeyInput(rc, ControlKeyType.F1);
                } else if(result == 60) {
                    return new ControlKeyInput(rc, ControlKeyType.F2);
                } else if(result == 61) {
                    return new ControlKeyInput(rc, ControlKeyType.F3);
                } else if(result == 62) {
                    return new ControlKeyInput(rc, ControlKeyType.F4);
                } else if(result == 63) {
                    return new ControlKeyInput(rc, ControlKeyType.F5);
                } else if(result == 64) {
                    return new ControlKeyInput(rc, ControlKeyType.F6);
                } else if(result == 65) {
                    return new ControlKeyInput(rc, ControlKeyType.F7);
                } else if(result == 66) {
                    return new ControlKeyInput(rc, ControlKeyType.F8);
                } else if(result == 67) {
                    return new ControlKeyInput(rc, ControlKeyType.F9);
                } else if(result == 68) {
                    return new ControlKeyInput(rc, ControlKeyType.F10);
                } else {
                    return new KeyInput(rc, false);
                }
            } else if(ch == 195) {
                int result = getch();
                char rc = (char) result;
                if(isUTF8CharsetEncoding()) {
                    if(result == 188) {
                        return new KeyInput('ü', true);
                    } else if(result == 164) {
                        return new KeyInput('ä', true);
                    } else if(result == 182) {
                        return new KeyInput('ö', true);
                    } else if(result == 156) {
                        return new KeyInput('Ü', true);
                    } else if(result == 132) {
                        return new KeyInput('Ä', true);
                    } else if(result == 150) {
                        return new KeyInput('Ö', true);
                    }
                }
            } else {
                if(!isUTF8CharsetEncoding()) {
                    if(ch == 129) {
                        return new KeyInput('ü', true);
                    } else if(ch == 132) {
                        return new KeyInput('ä', true);
                    } else if(ch == 148) {
                        return new KeyInput('ö', true);
                    } else if(ch == 154) {
                        return new KeyInput('Ü', true);
                    } else if(ch == 142) {
                        return new KeyInput('Ä', true);
                    } else if(ch == 153) {
                        return new KeyInput('Ö', true);
                    }
                }
                return new KeyInput(c, Character.isLetter(c));
            }
        } else if(OperationSystemUtil.isLinux()) {
            if(ch == 9) {
                return new ControlKeyInput(c, ControlKeyType.TAB);
            } else if(ch == 10) {
                return new ControlKeyInput(c, ControlKeyType.ENTER);
            } else if(ch == 127) {
                return new ControlKeyInput(c, ControlKeyType.DELETE);
            } else if(ch == 27) {
                int lresult = getch();
                if(lresult == 91) {
                    int result = getch();
                    char rc = (char) result;
                    if(result == 65) {
                        return new ControlKeyInput(rc, ControlKeyType.ARROW_UP);
                    } else if(result == 67) {
                        return new ControlKeyInput(rc, ControlKeyType.ARROW_RIGHT);
                    } else if(result == 66) {
                        return new ControlKeyInput(rc, ControlKeyType.ARROW_DOWN);
                    } else if(result == 68) {
                        return new ControlKeyInput(rc, ControlKeyType.ARROW_LEFT);
                    } else if(result == 50) {
                        int vresult = getch();
                        if(vresult == 126) {
                            return new ControlKeyInput((char) vresult, ControlKeyType.PASTE);
                        } else if(vresult == 48) {
                            int bresult = getch();
                            if(bresult == 126) {
                                return new ControlKeyInput(rc, ControlKeyType.F9);
                            }
                        } else if(vresult == 49) {
                            int bresult = getch();
                            if(bresult == 126) {
                                return new ControlKeyInput(rc, ControlKeyType.F10);
                            }
                        } else if(vresult == 50) {
                            int bresult = getch();
                            if(bresult == 126) {
                                return new ControlKeyInput(rc, ControlKeyType.F11);
                            }
                        } else if(vresult == 51) {
                            int bresult = getch();
                            if(bresult == 126) {
                                return new ControlKeyInput(rc, ControlKeyType.F12);
                            }
                        }
                    } else if(result == 51) {
                        int vresult = getch();
                        if(vresult == 126) {
                            return new ControlKeyInput((char) vresult, ControlKeyType.REMOVE);
                        }
                    } else if(result == 72) {
                        return new ControlKeyInput(rc, ControlKeyType.POS1);
                    } else if(result == 70) {
                        return new ControlKeyInput(rc, ControlKeyType.END);
                    } else if(result == 53) {
                        int vresult = getch();
                        if(vresult == 126) {
                            return new ControlKeyInput((char) vresult, ControlKeyType.PIC_1);
                        }
                    } else if(result == 54) {
                        int vresult = getch();
                        if(vresult == 126) {
                            return new ControlKeyInput((char) vresult, ControlKeyType.PIC_2);
                        }
                    } else if(result == 49) {
                        int vresult = getch();
                        if(vresult == 53) {
                            int bresult = getch();
                            if(bresult == 126) {
                                return new ControlKeyInput(rc, ControlKeyType.F5);
                            }
                        } else if(vresult == 55) {
                            int bresult = getch();
                            if(bresult == 126) {
                                return new ControlKeyInput(rc, ControlKeyType.F6);
                            }
                        } else if(vresult == 56) {
                            int bresult = getch();
                            if(bresult == 126) {
                                return new ControlKeyInput(rc, ControlKeyType.F7);
                            }
                        } else if(vresult == 57) {
                            int bresult = getch();
                            if(bresult == 126) {
                                return new ControlKeyInput(rc, ControlKeyType.F8);
                            }
                        }
                    }
                } else if(lresult == 79) {
                    int result = getch();
                    char rc = (char) result;
                    if(result == 80) {
                        return new ControlKeyInput(rc, ControlKeyType.F1);
                    } else if(result == 81) {
                        return new ControlKeyInput(rc, ControlKeyType.F2);
                    } else if(result == 82) {
                        return new ControlKeyInput(rc, ControlKeyType.F3);
                    } else if(result == 83) {
                        return new ControlKeyInput(rc, ControlKeyType.F4);
                    }
                } else {
                    return new ControlKeyInput((char)27, ControlKeyType.ESCAPE);
                }
            } else if(ch == 32) {
                return new ControlKeyInput(c, ControlKeyType.SPACE);
            } else if(ch == 65475) {
                int result = getch();
                char rc = (char) result;
                if(result == 65468) {
                    return new KeyInput('ü', true);
                } else if(result == 65444) {
                    return new KeyInput('ä', true);
                } else if(result == 65462) {
                    return new KeyInput('ö', true);
                } else if(result == 65436) {
                    return new KeyInput('Ü', true);
                } else if(result == 65412) {
                    return new KeyInput('Ä', true);
                } else if(result == 65430) {
                    return new KeyInput('Ö', true);
                }
            } else {
                return new KeyInput(c, Character.isLetter(c));
            }
        } else {
            return null;
        }
        return new KeyInput((char) ch, false);
    }

}
