package net.thucydides.model.requirements;

import net.thucydides.model.requirements.model.Requirement;

import java.util.Comparator;
import java.util.TreeMap;

public class ChildrenFirstOrderedMap extends TreeMap<String, Requirement> {
    private static final String DOT_REGEX = "\\.";

    public ChildrenFirstOrderedMap() {
        super(new Comparator<String>() {
            //we're ordering the map with first the children (longest segments)
            //and within segment with name
            @Override
            public int compare(String s, String s2) {
                int seg1 = s.split(DOT_REGEX).length;
                int seg2 = s2.split(DOT_REGEX).length;
                //if s has more fragment than s2
                //we want s to be lower
                if (seg1 != seg2) {
                    return seg2 - seg1;
                }
                return s.compareTo(s2);
            }
        });
    }
}
