package tester;

import static org.junit.Assert.*;

import org.junit.Test;
import edu.princeton.cs.algs4.StdRandom;
import student.StudentArrayDeque;

public class TestArrayDequeEC {
    @Test
    public void difftestRandom() {
        StringBuilder callSeq = new StringBuilder();

        StudentArrayDeque<Integer> sad = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> ad = new ArrayDequeSolution<>();

        int cmdNum = 5;
        int testNum = 1000;
        for (int i = 0; i < testNum; i++) {
            int kind = StdRandom.uniform(cmdNum);
            switch (kind) {
                case 0: {
                    int x = StdRandom.uniform(1000);
                    sad.addFirst(x);
                    ad.addFirst(x);
                    callSeq.append(String.format("addFirst(%s)\n", x));
                    break;
                }
                case 1: {
                    int x = StdRandom.uniform(1000);
                    sad.addLast(x);
                    ad.addLast(x);
                    callSeq.append(String.format("addLast(%s)\n", x));
                    break;
                }
                case 2: {
                    if (ad.isEmpty()) {
                        break;
                    }
                    Integer actual = sad.removeFirst();
                    Integer expected = ad.removeFirst();
                    callSeq.append("removeFirst()\n");
                    assertEquals(callSeq.toString(), expected, actual);
                }
                case 3: {
                    if (ad.isEmpty()) {
                        break;
                    }
                    Integer expected = ad.removeLast();
                    Integer actual = sad.removeLast();
                    callSeq.append("removeLast()\n");
                    assertEquals(callSeq.toString(), expected, actual);
                }
                case 4: {
                    int expected = ad.size();
                    int actual = sad.size();
                    callSeq.append("size()\n");
                    assertEquals(callSeq.toString(), expected, actual);
                }
                default: {
                    break;
                }
            }
        }
    }
}
