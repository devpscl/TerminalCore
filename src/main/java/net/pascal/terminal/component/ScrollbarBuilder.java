package net.pascal.terminal.component;


public class ScrollbarBuilder {

    public static char[] buildScrollbar(char arrowUp, char arrowDown, char full, char empty, int length, double obtained, double total) {
        StringBuilder sb = new StringBuilder();
        sb.append(arrowUp);


        double share = obtained/total;
        double size = length/total;
        if(size < 1) size = 1;

        double p = share*length;

        double offset = size/2;
        if(p-size < 0) {
            p += size-p;
        }
        if(p+size > length) {
            p -= p+size-length;
        }

        double pos1 = p-size;
        double pos2 = p+size;

        for(double i = 0;i<length;i++) {
            if(i >= pos1 && i <= pos2) {
                sb.append(full);
            } else {
                sb.append(empty);
            }
        }
        sb.append(arrowDown);
        return sb.toString().toCharArray();
    }

    public static char[] buildVerticalScrollbar(int length, double obtained, double total) {
        return buildScrollbar('▲', '▼', '█', '▒', length, obtained, total);
    }

    public static char[] buildHorizontalScrollbar(int length, double obtained, double total) {
        return buildScrollbar('◄', '►', '█', '▒', length, obtained, total);
    }

}
