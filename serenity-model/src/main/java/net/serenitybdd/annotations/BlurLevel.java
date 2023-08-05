package net.serenitybdd.annotations;

/**
 * Created with IntelliJ IDEA.
 * User: rahuljai
 * Date: 28/12/12
 * Time: 7:07 PM
 * To change this template use File | Settings | File Templates.
 */
public enum BlurLevel {

    NONE (0),
    LIGHT (1),
    MEDIUM (5),
    HEAVY (10);

    private int radius;

    private BlurLevel(int radius) {
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }
}
