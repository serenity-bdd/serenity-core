package net.serenitybdd.screenplay.targets;

import com.google.common.collect.Iterables;

import java.util.Arrays;
import java.util.List;

public class IFrame {

    public final List<String> path;
    private IFrame(String... path) {
        this.path = Arrays.asList(path);
    }

    public static IFrame withPath(String... names) {
        return new IFrame(names);
    }

    @Override
    public String toString() {
        return path.toString();
    }

    public String childName(){
        return Iterables.getLast(path);
    }



}
