import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TestTrieMap {
    @Test
    /** Insert several key and check contains */
    public void testTrieMapAdd() {
        TrieMap<Integer> t = new TrieMap<>();

        Map<String, Integer> kvs = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            String key = "key" + i;
            kvs.put(key, i);
            t.put(key, i);
        }

        for (String key : kvs.keySet()) {
            assertTrue("TrieMap should contain key " + key, t.containsKey(key));
            assertTrue("TrieMap should contain value " + kvs.get(key), t.get(key) == kvs.get(key));
        }
    }

    @Test
    /** Insert several key and check prefix */
    public void testTrieMapPrefix() {
        TrieMap<Integer> t = new TrieMap<>();

        int n = 10;
        Map<String, Integer> kvs = new HashMap<>();
        for (int i = 0; i < n; i++) {
            String key = "key" + i;
            kvs.put(key, i);
            t.put(key, i);
        }

        Set<String> keys = t.keySetWithPrefix("key");
        assertTrue("Keys with prefix should have size " + n, keys.size() == n);
        for (String key : keys) {
            assertTrue("TrieMap should contain key " + key, kvs.containsKey(key));
        }
    }

    @Test
    /** Insert keys with different prefix and check prefix */
    public void testTrieMapPrefix2() {
        TrieMap<Integer> t = new TrieMap<>();

        int n = 10;
        Map<String, Integer> kvs = new HashMap<>();
        for (int i = 0; i < n; i++) {
            String key = "apple" + i;
            kvs.put(key, i);
            t.put(key, i);
        }

        int m = 5;
        Map<String, Integer> kvs2 = new HashMap<>();
        for (int i = 0; i < m; i++) {
            String key = "banana" + i;
            kvs2.put(key, i);
            t.put(key, i);
        }

        int k = 5;
        Map<String, Integer> kvs3 = new HashMap<>();
        for (int i = 0; i < k; i++) {
            String key = "app" + i;
            kvs3.put(key, i);
            t.put(key, i);
        }

        // check apple
        Set<String> keys = t.keySetWithPrefix("apple");
        assertTrue("Keys with prefix should have size " + n, keys.size() == n);

        // check banana
        keys = t.keySetWithPrefix("banana");
        assertTrue("Keys with prefix should have size " + m, keys.size() == m);

        // check app
        keys = t.keySetWithPrefix("app");
        assertTrue("Keys with prefix should have size " + n + k, keys.size() == n + k);
    }

    @Test
    /** check remove */
    public void testTrieMapRemove() {
        TrieMap<Integer> t = new TrieMap<>();

        int n = 10;
        Map<String, Integer> kvs = new HashMap<>();
        for (int i = 0; i < n; i++) {
            String key = "key" + i;
            kvs.put(key, i);
            t.put(key, i);
        }

        // remove last 5 keys
        for (int i = 5; i < n; i++) {
            String key = "key" + i;
            t.remove(key);
        }

        // check first 5 keys
        for (int i = 0; i < 5; i++) {
            String key = "key" + i;
            assertTrue("TrieMap should contain key " + key, t.containsKey(key));
        }

        // check last 5 keys
        for (int i = 5; i < n; i++) {
            String key = "key" + i;
            assertTrue("TrieMap should not contain key " + key, !t.containsKey(key));
        }
    }

    @Test
    /**
     * Difference Test with other Map, a very heavy test for
     * put, remove, get, containsKey, keySet, size, isEmpty
     */
    public void testDifference() {
        TrieMap<Integer> trieMap = new TrieMap<>();
        Map<String, Integer> hashMap = new HashMap<>();

        int kvNum = 500;
        int round = 10000;
        int type = 5;
        for (int i = 0; i < round; i++) {
            int op = (int) (Math.random() * type);
            int idx = (int) (Math.random() * kvNum);
            String key = "key" + idx;
            switch (op) {
                case 0:
                    // handle put
                    hashMap.put(key, idx);
                    trieMap.put(key, idx);
                    break;
                case 1:
                    // handle remove
                    if (hashMap.isEmpty()) {
                        break;
                    }
                    hashMap.remove(key);
                    trieMap.remove(key);
                    break;
                case 2: {
                    // handle containsKey
                    boolean expected = hashMap.containsKey(key);
                    boolean actual = trieMap.containsKey(key);
                    assertEquals("TrieMap should contain key " + key, expected, actual);

                    if (expected) {
                        // handle get
                        int expected2 = hashMap.get(key);
                        int actual2 = trieMap.get(key);
                        assertEquals("TrieMap should contain value " + expected2, expected2, actual2);
                    }
                    break;
                }
                case 3: {
                    // handle keySet
                    Set<String> expected = hashMap.keySet();
                    Set<String> actual = trieMap.keySet();
                    assertEquals("TrieMap should have size " + expected.size(), expected.size(),
                            actual.size());
                    for (String k : actual) {
                        assertTrue("TrieMap and hashmap should contain key " + k,
                                expected.contains(k));
                    }
                    break;
                }
                case 4: {
                    // handle size
                    int expected = hashMap.size();
                    int actual = trieMap.size();
                    assertEquals("TrieMap should have size " + expected, expected, actual);
                    break;
                }
            }
        }
    }
}
