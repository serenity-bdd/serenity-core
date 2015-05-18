package net.serenitybdd.core.time;

/**
 * Created by john on 14/03/15.
 */
public class Stopwatch {

    public static Stopwatch SYSTEM = new Stopwatch();
    long counter = 0;

    public void start() {
        counter = System.currentTimeMillis();
    }

    public long stop() {
        long result = System.currentTimeMillis() - counter;
        counter = 0;
        return result;
    }

}
