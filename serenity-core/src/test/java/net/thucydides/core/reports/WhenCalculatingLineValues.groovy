package net.thucydides.core.reports

import net.thucydides.core.geometry.Line
import net.thucydides.core.geometry.Point
import spock.lang.Specification

class WhenCalculatingLineValues extends Specification {

    def "should display a point in a meaningful way"() {
        when:
            def point = Point.at(1,2)
        then:
            point.toString() == "(1,2)"
    }

    def "should display a line in a meaningful way"() {
        when:
            def line = Line.from(Point.at(1,2)).to(Point.at(3,4))
        then:
            line.toString() == "(1,2)->(3,4)"
    }

    def "should calculate the slope of a line given two points"() {
        expect:
            def line = Line.from(point1).to(point2)
            line.slope.doubleValue() == expectedSlope

        where:
            point1            | point2             | expectedSlope
            Point.at(0,0)     | Point.at(10,0)     | 0.0
            Point.at(0,0)     | Point.at(10,10)    | 1.0
            Point.at(0,0)     | Point.at(10,5)     | 0.5
            Point.at(10,10)   | Point.at(20,20)    | 1.0
    }

    def  "should find the point of intersection between two lines"() {
        expect:
            def line1 = Line.from(point1).to(point2)
            def line2 = Line.from(point3).to(point4)
            line1.intersectionWith(line2).get() == intersection
        where:
            point1          | point2          | point3          | point4          | intersection
            Point.at(2,15)  | Point.at(40,15) | Point.at(16,1)  | Point.at(30,7)  | Point.at(49,15)
            Point.at(2,15)  | Point.at(22,16) | Point.at(16,1)  | Point.at(44,12) | Point.at(59,17.85)
            Point.at(2,15)  | Point.at(2,25)  | Point.at(16,1)  | Point.at(44,12) | Point.at(2,-4.46)
            Point.at(0,5)   | Point.at(5,5)   | Point.at(1,0)   | Point.at(1,10)  | Point.at(1,5)
            Point.at(1,0)   | Point.at(1,10)  | Point.at(0,5)   | Point.at(5,5)   | Point.at(1,5)
    }

    def "should be able to extend single points as horizontal lines"() {
        when:
            def line1 = Line.from(Point.at(1,1)).to(Point.at(1,10))
            def line2 = Line.from(Point.at(0,5)).horizontally().to(Point.at(0,5))
        then:
            line1.intersectionWith(line2).get() == Point.at(1,5)

    }


    def "should be able to extend single points as vertical lines"() {
        when:
            def line1 = Line.from(Point.at(1,1)).to(Point.at(10,1))
            def line2 = Line.from(Point.at(5,0)).vertically().to(Point.at(5,0))
        then:
            line1.intersectionWith(line2).get() == Point.at(5,1)

    }

    def  "should find no point if the lines are parallel"() {
        expect:
            def line1 = Line.from(point1).to(point2)
            def line2 = Line.from(point3).to(point4)
            !line1.intersectionWith(line2).isPresent()
        where:
            point1          | point2          | point3          | point4
            Point.at(2,15)  | Point.at(40,15) | Point.at(16,1)  | Point.at(30,1)
            Point.at(2,15)  | Point.at(2,25)  | Point.at(16,1)  | Point.at(16,10)
    }
}
