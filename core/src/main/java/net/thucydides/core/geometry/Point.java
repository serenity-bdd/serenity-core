package net.thucydides.core.geometry;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;

public class Point {

    final BigDecimal x;
    final BigDecimal y;

    public Point(BigDecimal x, BigDecimal y) {
        this.x = x;
        this.y = y;
    }

    public static Point at(int x, int y) {
        return new Point(new BigDecimal(x), new BigDecimal(y));
    }

    public static Point at(long x, long y) {
        return new Point(new BigDecimal(x), new BigDecimal(y));
    }

    public static Point at(BigDecimal x, BigDecimal y) {
        return new Point(x, y);
    }

    public BigDecimal getX() {
        return x;
    }

    public BigDecimal getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return "("+ x + "," + y + ")";
    }
}
