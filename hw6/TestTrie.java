import org.junit.Test;
import org.junit.Assert;
import java.util.Set;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;

public class TestTrie {
    @Test
    /** Test add and contains */
    public void testAddContains() {
        // add several keys
        Trie t = new Trie();

        int n = 100;
        Set<String> keys = new HashSet<String>();
        for (int i = 0; i < n; i++) {
            String key = "key" + i;
            keys.add(key);
            t.insert(key);
        }

        // check that they are all there
        assertEquals("size is correct", n, t.size(), keys.size());
        for (String key : keys) {
            Assert.assertTrue("contains " + key, t.contains(key));
        }
    }

    @Test
    /** Difference test: Trie and HashSet -- add, contains, size */
    public void testAddContainsSize() {
        // add several keys
        int n = 100;
        String[] keys = new String[n];
        for (int i = 0; i < n; i++) {
            // random string
            String key = "";
            for (int j = 0; j < 20 * Math.random(); j++) {
                key += (char) ('a' + (int) (Math.random() * 26));
            }
            keys[i] = key;
        }

        Trie t = new Trie();
        HashSet<String> h = new HashSet<String>();

        StringBuilder sb = new StringBuilder();
        int round = 10000;
        for (int i = 0; i < round; i++) {
            int j = (int) (Math.random() * n);
            String key = keys[j];
            int opCode = (int) (Math.random() * 3);
            if (opCode == 0) {
                h.add(key);
                t.insert(key);
                sb.append("t.insert(\"" + key + "\");\n");
            } else if (opCode == 1) {
                boolean expected = h.contains(key);
                boolean actual = t.contains(key);
                sb.append("t.contains(\"" + key + "\");\n");
                assertEquals(sb.toString(), expected, actual);
            } else if (opCode == 2) {
                int expected = h.size();
                int actual = t.size();
                sb.append("t.size();\n");
                assertEquals(sb.toString(), expected, actual);
            }
        }
    }
}
