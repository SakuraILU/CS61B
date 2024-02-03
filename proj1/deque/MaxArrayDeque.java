package deque;

import java.util.Comparator;
import java.util.Iterator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> c;

    public MaxArrayDeque(Comparator<T> c) {
        this.c = c;
    }

    public T max() {
        if (size() == 0) {
            return null;
        }

        T max = get(0);
        Iterator<T> itr = iterator();
        while (itr.hasNext()) {
            T value = itr.next();
            if (c.compare(max, value) < 0) {
                max = value;
            }
        }

        return max;
    }

    public T max(Comparator<T> customC) {
        if (size() == 0) {
            return null;
        }

        T max = get(0);
        Iterator<T> itr = iterator();
        while (itr.hasNext()) {
            T value = itr.next();
            if (customC.compare(max, value) < 0) {
                max = value;
            }
        }

        return max;
    }
}
