package gitlet;

import java.util.*;

public class MyUtils {
    public static void exit(String msg, Object... args) {
        System.out.printf(msg + "\n", args);
        System.exit(0);
    }

    public static <T> Set<T> intersectionSet(Set<T> set1, Set<T> set2) {
        Set<T> result = new HashSet<>();
        for (T o : set1) {
            if (set2.contains(o)) {
                result.add(o);
            }
        }
        return result;
    }

    public static <T> Set<T> unionSet(Set<T> set1, Set<T> set2) {
        Set<T> result = new HashSet<>(set1);
        result.addAll(set2);
        return result;
    }

    public static <T> Set<T> differenceSet(Set<T> set1, Set<T> set2) {
        Set<T> result = new HashSet<>(set1);
        result.removeAll(set2);
        return result;
    }
}
