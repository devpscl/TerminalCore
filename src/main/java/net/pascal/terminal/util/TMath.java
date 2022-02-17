package net.pascal.terminal.util;

public class TMath {

    public static TVector getCenter(TVector pos1, TVector pos2) {
        TVector dif = pos2.clone().subtract(pos1);
        return pos1.clone().add(dif.divide(new TVector(2, 2)));
    }

    public static TVector getCornerFromCenter(TVector pos1, int length) {
        return pos1.clone().subtractWidth(length/2);
    }


    public static TVector getExactPosition(int columns, int rows) {
        return new TVector(columns+1, rows);
    }

    public static TVector getSize(TVector vec1, TVector vec2) {
        return vec2.clone().subtract(vec1);
    }

}
