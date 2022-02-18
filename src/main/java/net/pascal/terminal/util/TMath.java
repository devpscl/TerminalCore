package net.pascal.terminal.util;

/**
 * The type Terminal math.
 */
public class TMath {

    /**
     * Gets center from two positions.
     *
     * @param pos1 the position 1
     * @param pos2 the position 2
     * @return the center of region
     */
    public static TVector getCenter(TVector pos1, TVector pos2) {
        TVector dif = pos2.clone().subtract(pos1);
        return pos1.clone().add(dif.divide(new TVector(2, 2)));
    }

    /**
     * Gets corner from center.
     *
     * @param pos1   the position 1
     * @param length the length
     * @return the corner from center
     */
    public static TVector getCornerFromCenter(TVector pos1, int length) {
        return pos1.clone().subtractWidth(length/2);
    }


    /**
     * Gets exact position.
     *
     * @param columns the columns
     * @param rows    the rows
     * @return the exact position
     */
    public static TVector getExactPosition(int columns, int rows) {
        return new TVector(columns+1, rows);
    }

    /**
     * Gets size of region.
     *
     * @param vec1 the position 1
     * @param vec2 the position 2
     * @return the size
     */
    public static TVector getSize(TVector vec1, TVector vec2) {
        return vec2.clone().subtract(vec1);
    }



}
