package deque;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Iterator;

public class ArrayDequeTest {
    @Test
    /*
     * Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     */
    public void addIsEmptySizeTest() {
        ArrayDeque<String> ad = new ArrayDeque<String>();

        assertTrue("A newly initialized ArrayDeque should be empty", ad.isEmpty());
        ad.addFirst("front");

        assertEquals(1, ad.size());
        assertFalse("ad should now contain 1 item", ad.isEmpty());

        ad.addLast("middle");
        assertEquals(2, ad.size());

        ad.addLast("back");
        assertEquals(3, ad.size());
    }

    @Test
    /*
     * Adds an item, then removes an item, and ensures that ad is empty afterwards.
     */
    public void addRemoveTest() {
        ArrayDeque<Integer> ad = new ArrayDeque<Integer>();
        assertTrue("ad should be empty upon initialization", ad.isEmpty());

        ad.addFirst(10);
        assertFalse("ad should contain 1 item", ad.isEmpty());

        ad.removeFirst();
        assertTrue("ad should be empty after removal", ad.isEmpty());
    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {
        ArrayDeque<Integer> ad = new ArrayDeque<Integer>();
        assertTrue("ad should be empty upon initialization", ad.isEmpty());

        ad.removeFirst();
        assertTrue("ad should be empty after removal", ad.isEmpty());
    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest2() {
        ArrayDeque<Integer> ad = new ArrayDeque<Integer>();
        assertTrue("ad should be empty upon initialization", ad.isEmpty());

        ad.removeLast();
        assertTrue("ad should be empty after removal", ad.isEmpty());
    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest3() {
        ArrayDeque<Integer> ad = new ArrayDeque<Integer>();
        assertTrue("ad should be empty upon initialization", ad.isEmpty());

        ad.addFirst(3);
        ad.removeLast();
        ad.removeFirst();
        ad.removeLast();
        assertTrue("ad should be empty after removal", ad.isEmpty());
    }

    @Test
    /*
     * Check if you can create ArrayDeques with different parameterized types
     */
    public void multipleParamTest() {
        ArrayDeque<String> ad = new ArrayDeque<String>();
        assertTrue("ad should be empty upon initialization", ad.isEmpty());

        ad.addFirst("front");
        ad.addLast("middle");
        ad.addLast("back");

        assertEquals(3, ad.size());
        assertFalse("ad should now contain 3 items", ad.isEmpty());
    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void removeEmptyTest4() {
        ArrayDeque<Integer> ad = new ArrayDeque<Integer>();
        assertTrue("ad should be empty upon initialization", ad.isEmpty());

        assertNull("ad should return null when removing from an empty deque", ad.removeFirst());
        assertNull("ad should return null when removing from an empty deque", ad.removeLast());
    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void largeAddTest() {

        ArrayDeque<Integer> ad = new ArrayDeque<Integer>();
        for (int i = 0; i < 32; i++) {
            ad.addLast(i);
        }

        for (double i = 0; i < 16; i++) {
            assertEquals("Should have the same value", i, (double) ad.removeFirst(), 0.0);
        }

        for (double i = 31; i > 15; i--) {
            assertEquals("Should have the same value", i, (double) ad.removeLast(), 0.0);
        }
    }

    @Test
    /* Test add and get **/
    public void addGetTest() {
        ArrayDeque<Integer> ad = new ArrayDeque<Integer>();
        for (int i = 0; i < 100; i++) {
            ad.addLast(i);
        }

        for (int i = 0; i < 100; i++) {
            Integer val = ad.get(i);
            assertEquals("Should have the same value (get)", i, val, 0);
        }

        for (int i = 100; i < 200; i++) {
            Integer val = ad.get(i);
            assertNull("Should be null", val);
        }
    }

    @Test
    /* Test add and iterator **/
    public void addIteratorTest() {
        ArrayDeque<Integer> ad = new ArrayDeque<Integer>();
        for (int i = 0; i < 100; i++) {
            ad.addLast(i);
        }

        int i = 0;
        for (Integer val : ad) {
            assertEquals("Should have the same value", i, val, 0);
            i++;
        }
    }

    @Test
    /* Test Iterator hasNext with items */
    public void testHasNextBaseCase() {
        ArrayDeque<Integer> ad = new ArrayDeque<Integer>();
        ad.addFirst(1);
        Iterator<Integer> itr = ad.iterator();
        assertTrue(itr.hasNext());
        int i = itr.next();
        assertEquals(1, i);
    }

    @Test
    /* Test hash next on empty deque **/
    public void hasNextEmpty() {
        ArrayDeque<Integer> ad = new ArrayDeque<Integer>();
        assertFalse(ad.iterator().hasNext());
    }

    @Test
    public void testNext() {
        ArrayDeque<Integer> ad = new ArrayDeque<Integer>();
        for (int i = 0; i < 10000; i++) {
            ad.addLast(i);
        }

        int i = 0;
        Iterator<Integer> itr = ad.iterator();
        while (itr.hasNext()) {
            assertEquals("Should have the same value", i, (int) itr.next(), 0);
            i++;
        }
    }

    @Test
    /* Test Iterator hasNext after calling next() many times */
    public void testHasNextFalse() {
        ArrayDeque<Integer> ad = new ArrayDeque<Integer>();
        for (int i = 0; i < 10000; i++) {
            ad.addLast(i);
        }

        Iterator<Integer> itr = ad.iterator();
        for (int i = 0; i < 10000; i++) {
            itr.next();
        }
        assertFalse(itr.hasNext());
    }

    @Test
    /* test two deque equal */
    public void testEqualTrue() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        ArrayDeque<Integer> ad2 = new ArrayDeque<Integer>();
        for (int i = 0; i < 130; i++) {
            ad1.addLast(i);
            ad2.addLast(i);
        }
        assertTrue(ad1.equals(ad2));
    }

    @Test
    /* test two different type equal */
    public void testEqualMultDequeType() {
        ArrayDeque<Integer> ad = new ArrayDeque<Integer>();
        LinkedListDeque<Integer> ld = new LinkedListDeque<Integer>();

        for (int i = 0; i < 10000; i++) {
            ad.addLast(i);
            ld.addLast(i);
        }

        assertTrue(ad.equals(ld));
    }
}
