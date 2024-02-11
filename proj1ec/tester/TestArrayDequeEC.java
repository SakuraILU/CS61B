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

        int cmdNum = 7;
        int testNum = 1000;
        int valueRange = 2000;
        for (int i = 0; i < testNum; i++) {
            int kind = StdRandom.uniform(cmdNum);
            switch (kind) {
                case 0: {
                    int v = StdRandom.uniform(valueRange);
                    ad.addFirst(v);
                    sad.addFirst(v);
                    callSeq.append(String.format("addFirst(%s)\n", v));
                    break;
                }
                case 1: {
                    int v = StdRandom.uniform(valueRange);
                    ad.addLast(v);
                    sad.addLast(v);
                    callSeq.append(String.format("addLast(%s)\n", v));
                    break;
                }
                case 2: {
                    if (ad.isEmpty()) {
                        break;
                    }
                    Integer expected = ad.removeFirst();
                    Integer actual = sad.removeFirst();
                    callSeq.append("removeFirst()\n");
                    assertEquals(callSeq.toString(), expected, actual);
                    break;
                }
                case 3: {
                    if (ad.isEmpty()) {
                        break;
                    }
                    Integer expected = ad.removeLast();
                    Integer actual = sad.removeLast();
                    callSeq.append("removeLast()\n");

                    String result = callSeq.toString();
                    assertEquals(result.substring(0, result.length() - 1), expected, actual);
                    break;
                }
                case 4: {
                    int expected = ad.size();
                    int actual = sad.size();
                    callSeq.append("size()\n");

                    String result = callSeq.toString();
                    assertEquals(result.substring(0, result.length() - 1), expected, actual);
                    break;
                }
                case 5: {
                    if (ad.isEmpty()) {
                        break;
                    }
                    int index = StdRandom.uniform(ad.size());
                    Integer actual = sad.get(index);
                    Integer expected = ad.get(index);

                    String result = callSeq.toString();
                    assertEquals(result.substring(0, result.length() - 1), expected, actual);
                    break;
                }
                case 6: {
                    boolean expected = ad.isEmpty();
                    boolean actual = sad.isEmpty();
                    callSeq.append("isEmpty()\n");

                    String result = callSeq.toString();
                    assertEquals(result.substring(0, result.length() - 1), expected, actual);
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }
}
