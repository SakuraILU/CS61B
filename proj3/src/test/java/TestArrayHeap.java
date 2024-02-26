import org.junit.Test;
import static org.junit.Assert.*;
import java.util.PriorityQueue;
import java.util.Map;
import java.util.HashMap;

public class TestArrayHeap {
    @Test
    public void testInsertAndRemoveAllButLast() {
        ArrayHeap<String> pq = new ArrayHeap<>();
        pq.insert("c", 3);
        pq.insert("i", 9);
        pq.insert("g", 7);
        pq.insert("d", 4);
        pq.insert("a", 1);
        pq.insert("h", 8);
        pq.insert("e", 5);
        pq.insert("b", 2);
        pq.insert("c", 3);
        pq.insert("d", 4);

        int i = 0;
        String[] expected = { "a", "b", "c", "c", "d", "d", "e", "g", "h", "i" };
        while (pq.size() > 1) {
            assertEquals(expected[i], pq.removeMin());
            i += 1;
        }
    }

    @Test
    /**
     * difference test with ArrayHeap and PriorityQueue:
     * insert, removeMin/remove, size, isEmpty, changePriority/remove+add
     */
    public void testDifference() {
        Map<Integer, Double> priorityMap = new HashMap<>();

        PriorityQueue<Integer> pq = new PriorityQueue<>(
                (o1, o2) -> Double.compare(priorityMap.get(o1), priorityMap.get(o2)));
        ArrayHeap<Integer> myPq = new ArrayHeap<>();

        int numRange = 500;
        int round = 10000;
        int type = 5;
        for (int i = 0; i < round; i++) {
            int op = (int) (Math.random() * type);
            int num = (int) (Math.random() * numRange);
            double priority = (Math.random() * numRange);
            switch (op) {
                case 0: {
                    // handle insert
                    if (priorityMap.containsKey(num)) {
                        break;
                    }

                    priorityMap.put(num, priority);
                    pq.add(num);

                    myPq.insert(num, priority);
                    break;
                }
                case 1: {
                    // handle removeMin/remove
                    if (pq.isEmpty()) {
                        break;
                    }
                    int expected = pq.poll();
                    priorityMap.remove(expected);

                    int actual = myPq.removeMin();
                    assertEquals("ArrayHeap should remove " + expected, expected, actual);
                    break;
                }
                case 2: {
                    // handle size
                    int expected = pq.size();
                    int actual = myPq.size();
                    assertEquals("ArrayHeap should have size " + expected, expected, actual);
                    break;
                }
                case 3: {
                    // handle isEmpty
                    boolean expected = pq.isEmpty();
                    boolean actual = myPq.isEmpty();
                    assertEquals("ArrayHeap should be empty " + expected, expected, actual);
                    break;
                }
                case 4: {
                    // handle changePriority/remove+add
                    if (pq.isEmpty()) {
                        break;
                    }
                    int idx = (int) (Math.random() * numRange);
                    if (!priorityMap.containsKey(idx)) {
                        break;
                    }

                    priorityMap.put(idx, priority);

                    pq.remove(idx);
                    pq.add(idx);

                    myPq.changePriority(idx, priority);
                    break;
                }
            }
        }
    }

}
