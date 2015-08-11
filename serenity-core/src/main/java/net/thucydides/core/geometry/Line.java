package net.thucydides.core.geometry;

import com.google.common.base.Optional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class Line {
    private final Point origin;
    private final Point destination;

    private final static MathContext CONTEXT = new MathContext(2, RoundingMode.HALF_UP);

    public Line(Point origin, Point destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public Point getOrigin() {
        return origin;
    }

    public Point getDestination() {
        return destination;
    }

    public BigDecimal getSlope() {
        BigDecimal yDistance = destination.getY().subtract(origin.getY());
        BigDecimal xDistance = destination.getX().subtract(origin.getX());
        return (yDistance.divide(xDistance,CONTEXT));
    }

    public BigDecimal getYIntercept() {
        BigDecimal m = getSlope();
        return origin.getY().subtract(m.multiply(origin.getX()));
    }

    public Optional<Point> intersectionWith(Line line) {
        if (parallelWith(line)) {
            return Optional.absent();
        } else if (isVertical()) {
            BigDecimal b2 = line.getYIntercept();
            BigDecimal m2 = line.getSlope();
            // y = mx + b
            BigDecimal y = m2.multiply(origin.getX()).add(b2);
            return Optional.of(Point.at(origin.getX(),y));
        } else if (line.isVertical()) {
            BigDecimal b1 = getYIntercept();
            BigDecimal m1 = getSlope();
            // y = mx + b
            BigDecimal y = m1.multiply(line.getOrigin().getX()).add(b1);
            return Optional.of(Point.at(line.getOrigin().getX(),y));
        } else {
            BigDecimal m1 = getSlope();
            BigDecimal m2 = line.getSlope();

            BigDecimal b1 = getYIntercept();
            BigDecimal b2 = line.getYIntercept();
            BigDecimal x = (b2.subtract(b1)).divide(m1.subtract(m2),CONTEXT);
            BigDecimal y = (m1.multiply(x)).add(b1);
            return Optional.of(Point.at(x,y));
        }
    }

    private boolean parallelWith(Line line) {
        if (isVertical() && line.isVertical()) {
            return true;
        } else if (!isVertical() && !line.isVertical()) {
            return getSlope().equals(line.getSlope());
        } else {
            return false;
        }
    }

    private boolean isVertical() {
        return destination.getX().subtract(origin.getX()).equals(BigDecimal.ZERO);
    }

    public static LineBuilder from(Point origin) {
        return new LineBuilder(origin);
    }

    public static class LineBuilder {
        private final Point origin;
        private boolean preferHorizontalLineForSinglePoint = true;

        public LineBuilder(Point origin) {
            this.origin = origin;
        }

        public Line to(Point destination) {
            if (!origin.equals(destination)) {
                return new Line(origin, destination);
            } else {
                return new Line(origin, extended(destination));
            }
        }

        private Point extended(Point destination) {
            if (preferHorizontalLineForSinglePoint) {
                return Point.at(destination.getX().add(BigDecimal.ONE), destination.getY());
            } else {
                return Point.at(destination.getX(), destination.getY().add(BigDecimal.ONE));
            }
        }

        public LineBuilder horizontally() {
            this.preferHorizontalLineForSinglePoint = true;
            return this;
        }

        public LineBuilder vertically() {
            this.preferHorizontalLineForSinglePoint = false;
            return this;
        }
    }

    @Override
    public String toString() {
        return origin + "->" + destination;
    }
}
