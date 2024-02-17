package hw4.puzzle;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestBoard {
    @Test
    public void verifyImmutability() {
        int r = 2;
        int c = 2;
        int[][] x = new int[r][c];
        int cnt = 0;
        for (int i = 0; i < r; i += 1) {
            for (int j = 0; j < c; j += 1) {
                x[i][j] = cnt;
                cnt += 1;
            }
        }
        Board b = new Board(x);
        assertEquals("Your Board class is not being initialized with the right values.", 0, b.tileAt(0, 0));
        assertEquals("Your Board class is not being initialized with the right values.", 1, b.tileAt(0, 1));
        assertEquals("Your Board class is not being initialized with the right values.", 2, b.tileAt(1, 0));
        assertEquals("Your Board class is not being initialized with the right values.", 3, b.tileAt(1, 1));

        x[1][1] = 1000;
        assertEquals(
                "Your Board class is mutable and you should be making a copy of the values in the passed tiles array. Please see the FAQ!",
                3, b.tileAt(1, 1));
    }

    @Test
    public void testZeroDistance() {
        int[][] tiles = new int[][] {
                { 1, 2, 3 },
                { 4, 5, 6 },
                { 7, 8, 0 },
        };

        Board b = new Board(tiles);

        assertEquals("The manhattan distance should be 0.", 0, b.manhattan());
        assertEquals("The hamming distance should be 0.", 0, b.hamming());
    }

    @Test
    public void testDistance1() {
        int[][] tiles = new int[][] {
                { 1, 2, 3 },
                { 4, 5, 6 },
                { 0, 7, 8 },
        };

        Board b = new Board(tiles);

        assertEquals("The hamming distance should be 0.", 2, b.hamming());
        assertEquals("The manhattan distance should be 0.", 2, b.manhattan());

        tiles = new int[][] {
                { 1, 2, 3 },
                { 5, 0, 6 },
                { 4, 7, 8 },
        };

        b = new Board(tiles);

        assertEquals("The hamming distance should be 0.", 4, b.hamming());
        assertEquals("The manhattan distance should be 0.", 4, b.manhattan());

        tiles = new int[][] {
                { 4, 1, 2 },
                { 3, 0, 6 },
                { 5, 7, 8 },
        };

        b = new Board(tiles);

        assertEquals("The hamming distance should be 0.", 7, b.hamming());
        assertEquals("The manhattan distance should be 0.", 10, b.manhattan());
    }

}
