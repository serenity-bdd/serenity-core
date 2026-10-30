package shop.ordering;

import java.util.ArrayList;
import java.util.List;

/** A tiny in-memory basket so the example needs no browser or server. */
public class Basket {
    private final List<String> titles = new ArrayList<>();
    public void add(String title) { titles.add(title); }
    public int size() { return titles.size(); }
    public boolean isEmpty() { return titles.isEmpty(); }
}
