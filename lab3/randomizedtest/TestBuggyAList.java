package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
  @Test
  public void testThreeAddThreeRemove() {
    AListNoResizing<Integer> correct = new AListNoResizing<>();
    BuggyAList<Integer> broken = new BuggyAList<>();

    correct.addLast(5);
    correct.addLast(10);
    correct.addLast(15);

    broken.addLast(5);
    broken.addLast(10);
    broken.addLast(15);

    assertEquals(correct.size(), broken.size());

    assertEquals(correct.removeLast(), broken.removeLast());
    assertEquals(correct.removeLast(), broken.removeLast());
    assertEquals(correct.removeLast(), broken.removeLast());
  }

  @Test
  public void randomizedTest() {
    AListNoResizing<Integer> correct = new AListNoResizing<>();
    BuggyAList<Integer> broken = new BuggyAList<>();

    int N = 1000;
    for (int i = 0; i < N; i += 1) {
      int operationNumber = StdRandom.uniform(0, 4);
      if (operationNumber == 0) {
        // addLast
        int randVal = StdRandom.uniform(0, 100);
        correct.addLast(randVal);
        broken.addLast(randVal);
        System.out.println("addLast(" + randVal + ")");
      } else if (operationNumber == 1) {
        // size
        int expected = correct.size();
        int actual = broken.size();
        assertEquals("size() is not the same", expected, actual);
      } else if (operationNumber == 2) {
        // getLast
        if (correct.size() <= 0) {
          continue;
        }
        Integer expected = correct.getLast();
        Integer actual = broken.getLast();
        assertEquals("getLast() should be the same", expected, actual);
      } else if (operationNumber == 3) {
        // removeLast
        if (correct.size() <= 0) {
          continue;
        }
        Integer expected = correct.removeLast();
        Integer actual = broken.removeLast();
        assertEquals("removeLast() should be the same", expected, actual);
      }
    }
  }
}
