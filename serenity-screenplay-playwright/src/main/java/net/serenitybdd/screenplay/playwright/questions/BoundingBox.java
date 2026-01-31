package net.serenitybdd.screenplay.playwright.questions;

/**
 * Represents the bounding box of an element.
 */
public class BoundingBox {

    private final double x;
    private final double y;
    private final double width;
    private final double height;

    public BoundingBox(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return String.format("BoundingBox{x=%.2f, y=%.2f, width=%.2f, height=%.2f}", x, y, width, height);
    }
}
