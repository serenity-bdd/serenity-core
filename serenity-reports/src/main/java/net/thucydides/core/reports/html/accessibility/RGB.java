package net.thucydides.core.reports.html.accessibility;

public class RGB {
    private final int red;
    private final int green;
    private final int blue;
    private final double alpha;

    public RGB(int red, int green, int blue, double alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public RGB plus(RGB other) {
        return new RGB(this.red + other.red, this.green + other.green, this.blue + other.blue, this.alpha + other.alpha);
    }

    public RGB minus(RGB other) {
        return new RGB(this.red - other.red, this.green - other.green, this.blue - other.blue, this.alpha - other.alpha);
    }

    public double getAlpha() {
        return alpha;
    }

    public int getRed() {
        return red;
    }

    public int getBlue() {
        return blue;
    }

    public int getGreen() {
        return green;
    }

    public String toString() {
        return "rgba(" + red + "," + green + "," + blue + ")";
    }
}
