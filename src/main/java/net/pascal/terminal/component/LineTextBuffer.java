package net.pascal.terminal.component;

import net.pascal.terminal.util.TVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class LineTextBuffer {

    private final List<String> lines;

    private int currentLine;
    private int currentPointer;

    private int lineBufferLength;


    public LineTextBuffer() {
        lines = new ArrayList<>();
        lines.add("");
        currentLine = 1;
        currentPointer = 0;
        lineBufferLength = -1;
    }

    public LineTextBuffer(String[] strLines) {
        lines = new ArrayList<>();
        lines.addAll(Arrays.asList(strLines));
        if(lines.size() == 0) lines.add("");
        currentLine = 1;
        currentPointer = 0;
        lineBufferLength = -1;
    }

    public int getLineBufferLength() {
        return lineBufferLength;
    }

    public void setLineBufferLength(int lineBufferLength) {
        this.lineBufferLength = lineBufferLength;
    }

    public void setLineBufferLengthInfinity() {
        this.lineBufferLength = -1;
    }

    public int count() {
        return lines.size();
    }

    public void clear() {
        lines.clear();
    }

    public void addLine() {
        lines.add("");
    }

    public List<String> lines() {
        return lines;
    }

    public void removeLine() {
        lines.remove(lines.size()-1);
    }

    public void addLine(int line) {
        if(lines.size()<line) {
            while (lines.size()<line) {
                lines.add("");
            }
        } else {
            lines.add(line-1, "");
        }
    }

    public void removeLine(int line) {
        lines.remove(line-1);
    }

    public String getLine(int line) {
        if(lines.size()<line) return null;
        return lines.get(line-1);
    }

    public void setLine(int line, String value) {
        if(value.length() > lineBufferLength) {
            if(lineBufferLength != -1) {
                value = value.substring(0, lineBufferLength);
            }

        }
        while (lines.size()<line) {
            lines.add("");
        }
        lines.set(line-1, value);
    }

    public char[] getLineCharArray(int line) {
        String s = getLine(line);
        if(s == null) return null;
        if(lineBufferLength == -1) return s.toCharArray();
        char[] buf = new char[lineBufferLength];
        char[] chars = s.toCharArray();
        for(int i = 0;i<lineBufferLength;i++) {
            if(chars.length>i) {
                buf[i] = chars[i];
            } else buf[i] = '\u0000';
        }
        return buf;
    }

    public int getLineLength(int line) {
        if(getLine(line) == null) return 0;
        return getLine(line).length();
    }

    public int getCurrentLine() {
        return currentLine;
    }

    public int getCurrentPointer() {
        return currentPointer;
    }

    public int getTotalLength() {
        int length = 0;
        for (String line : lines) {
            if (line.length() > length) {
                length = line.length();
            }
        }
        return length;
    }

    public void setCurrentLine(int currentLine) {
        this.currentLine = currentLine;
    }

    public void setCurrentPointer(int currentPointer) {
        this.currentPointer = currentPointer;
    }

    public TVector toPointerPosition() {
        return new TVector(currentPointer, currentLine-1);
    }

    public TVector toPointerPosition(int line) {
        return new TVector(currentPointer, line-1);
    }

    public TVector toPointerPosition(TVector vec) {
        return new TVector(vec.x + currentPointer, vec.y + currentLine-1);
    }

    public TVector toPointerPosition(TVector vec, int line) {
        return new TVector(vec.x + currentPointer, vec.y + line-1);
    }

    public TVector toPointerPosition(TVector vec, int line, int pos) {
        return new TVector(vec.x + pos, vec.y + line-1);
    }

    public void pointerNewLine() {
        int line = currentLine;
        int pos = currentPointer;
        String content = getLine(line);
        int lSize = content.length();
        if(pos >= lSize) {
            addLine(line+1);
        } else {
            String toNewLine = content.substring(pos);
            setLine(line, content.substring(0, pos));
            addLine(line+1);
            setLine(line+1, toNewLine);
        }
        currentLine++;
        currentPointer = 0;
    }

    public void pointerRemoveLine() {
        int line = currentLine;
        int pos = currentPointer;
        String content = getLine(line);
        if(content == null) return;
        if(lineBufferLength != -1) {
            int free = lineBufferLength - getLine(line-1).length();
            if(free < content.length()) {
                String s = content.substring(0, free);
                setLine(line, content.substring(free));
                setLine(line-1, getLine(line-1) + s);
                return;
            }
        }

        removeLine(line);
        setLine(line-1, getLine(line-1) + content);
        currentLine--;
        currentPointer = getLineLength(currentLine);
    }

    public String convertToString(char[] chars) {
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            if (c == '\u0000') break;
            sb.append(c);
        }
        return sb.toString();
    }











}
