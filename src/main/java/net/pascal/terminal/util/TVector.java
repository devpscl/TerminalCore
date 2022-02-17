package net.pascal.terminal.util;

public class TVector {

    public int x;
    public int y;

    public TVector() {
        this.x = 0;
        this.y = 0;
    }

    public TVector(int[] i) {
        this.x = i[0];
        this.y = i[1];
    }

    public TVector(int columns, int rows) {
        this.x = columns;
        this.y = rows;
    }

    public TVector(double columns, double rows) {
        this.x = (int) columns;
        this.y = (int) rows;
    }

    public int getColumns() {
        return x;
    }

    public int getRows() {
        return y;
    }

    public int getWidth() {
        return x;
    }

    public int getHeight() {
        return y;
    }

    public TVector setWidth(int x) {
        this.x = x;
        return this;
    }

    public TVector setHeight(int y) {
        this.y = y;
        return this;
    }

    public TVector addHeight(int y) {
        this.y += y;
        return this;
    }

    public TVector subtractHeight(int y) {
        this.y -= y;
        return this;
    }

    public TVector addWidth(int x) {
        this.x += x;
        return this;
    }

    public TVector subtractWidth(int x) {
        this.x -= x;
        return this;
    }

    public boolean isNegative() {
        return x < 0 || y < 0;
    }

    public TVector add(TVector v) {
        this.x += v.x;
        this.y += v.y;
        return this;
    }

    public TVector subtract(TVector v) {
        this.x -= v.x;
        this.y -= v.y;
        return this;
    }

    public TVector multiply(TVector v) {
        this.x *= v.x;
        this.y *= v.y;
        return this;
    }

    public TVector multiply(int v) {
        this.x *= v;
        this.y *= v;
        return this;
    }

    public TVector divide(TVector v) {
        this.x /= v.x;
        this.y /= v.y;
        return this;
    }

    public TVector divide(int v) {
        this.x /= v;
        this.y /= v;
        return this;
    }

    public TVector zero() {
        this.x = 0;
        this.y = 0;
        return this;
    }


    public double distance(TVector v) {
        int x2 = v.x;
        int y2 = v.y;
        return Math.sqrt((x-x2)*(x-x2) + (y-y2)*(y-y2));
    }

    public TVector dif(TVector vec, TVector vec2) {
        double dx = x;
        double dy = y;

        double dx1 = vec.x;
        double dy1 = vec.y;

        double dx2 = vec2.x;
        double dy2 = vec2.y;

        dx = (dx/dx1)*dx2;
        dy = (dy/dy1)*dy2;
        return new TVector(dx, dy);
    }

    public TVector difX(TVector vec, TVector vec2) {
        double dx = x;
        double dy = y;

        double dx1 = vec.x;
        double dx2 = vec2.x;

        dx = (dx/dx1)*dx2;
        return new TVector(dx, dy);
    }

    public TVector difY(TVector vec, TVector vec2) {
        double dy = y;
        double dx = x;

        double dy1 = vec.y;

        double dy2 = vec2.y;
        dy = (dy/dy1)*dy2;
        return new TVector(dx, dy);
    }

    public TVector c() {
        return clone();
    }

    public TVector clone() {
        return new TVector(x, y);
    }

    @Override
    public String toString() {
        return "w: " + x + " | h:" + y;
    }
}
