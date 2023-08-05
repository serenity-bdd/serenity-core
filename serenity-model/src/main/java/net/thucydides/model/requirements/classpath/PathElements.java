package net.thucydides.model.requirements.classpath;

import com.google.common.base.Splitter;

import java.util.List;

/**
 * Created by john on 13/07/2016.
 */
public class PathElements {

    public static List<String> elementsOf(String path, String rootPackage) {
        return Splitter.on(".").splitToList(path.replace(rootPackage + ".",""));
    }

    public static String lastOf(List<String> list) {
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(list.size() - 1);
        }
    }

    public static String secondLastOf(List<String> list) {
        if (list.isEmpty() || list.size() < 2) {
            return null;
        } else {
            return list.get(list.size() - 2);
        }
    }

    public static List<String> allButLast(List<String> list) {
        return list.subList(0,Math.max(list.size() - 1,0));
    }
}
