package deque;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Comparator;

public class MaxArrayDequeTest {
    @Test
    /* Test empty max */
    public void emptyMaxTest() {
        MaxArrayDeque<Integer> mad1 = new MaxArrayDeque<Integer>(new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return a - b;
            }
        });

        Integer max = mad1.max();
        assertNull("Should be null", max);
    }

    @Test
    /* Test add and max */
    public void addMaxTest() {
        MaxArrayDeque<Integer> mad1 = new MaxArrayDeque<Integer>(new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return a - b;
            }
        });

        for (int i = 0; i < 1000; i++) {
            mad1.addLast(i);
        }

        Integer max = mad1.max();
        assertEquals("Should be the max value", 999, max, 0);
    }

    @Test
    /* Test add, remove and max */
    public void addRemoveMaxTest() {
        MaxArrayDeque<Integer> mad1 = new MaxArrayDeque<Integer>(new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return a - b;
            }
        });

        for (int i = 0; i < 1000; i++) {
            mad1.addLast(i);
        }

        for (int i = 0; i < 500; i++) {
            mad1.removeLast();
        }

        Integer max = mad1.max();
        assertEquals("Should be the max value", 499, max, 0);
    }

    @Test
    /* Test add, remove and max with custom comparator */
    public void addRemoveMaxCustomTest() {
        MaxArrayDeque<Integer> mad1 = new MaxArrayDeque<Integer>(new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return a - b;
            }
        });

        for (int i = -1000; i < 1000; i++) {
            mad1.addLast(i);
        }

        // 实际上是找最小值
        Integer max = mad1.max(new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return b - a;
            }
        });

        assertEquals("Should be the min value", -1000, max, 0);
    }
}
