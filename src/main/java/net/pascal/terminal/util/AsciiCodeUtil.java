package net.pascal.terminal.util;

import java.util.regex.Pattern;

public class AsciiCodeUtil {

    public static final char ESC = '\033';
    public static final char ESC2 = '\u001b';

    public static String removeAsciiCodes(String s) {
        String str = s;
        int index;
        while ((index = str.indexOf(ESC)) != -1) {
            StringBuilder rem = new StringBuilder(ESC + "[");
            for(int i = index+2;i<str.length();i++) {
                char c = str.charAt(i);
                rem.append(c);
                if(!isAsciiCodeParam(c)) {
                    break;
                }
            }
            str = str.replaceAll(Pattern.quote(rem.toString()), "");
        }

        return str;
    }

    public static String getLastAsciiCodes(String s) {
        String str = s;
        int index;
        String last = null;
        while ((index = str.indexOf(ESC)) != -1) {
            StringBuilder rem = new StringBuilder(ESC + "[");
            for(int i = index+2;i<str.length();i++) {
                char c = str.charAt(i);
                rem.append(c);
                if(!isAsciiCodeParam(c)) {
                    break;
                }
            }
            last = rem.toString();
            str = str.replaceAll(Pattern.quote(rem.toString()), "");
        }

        return last;
    }



    private static boolean isAsciiCodeParam(char c) {
        return c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' ||
                c == '9' || c == ';';
    }


}
