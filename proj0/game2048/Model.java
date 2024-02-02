package game2048;

import static org.junit.Assert.assertTrue;

import java.util.Formatter;
import java.util.Observable;
import java.util.Iterator;

/**
 * The state of a game of 2048.
 * 
 * @author TODO: YOUR NAME HERE
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far. Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /*
     * Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r). Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /**
     * A new 2048 game on a board of size SIZE with no pieces
     * and score 0.
     */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /**
     * A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes.
     */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /**
     * Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     * 0 <= COL < size(). Returns null if there is no tile there.
     * Used for testing. Should be deprecated and removed.
     */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /**
     * Return the number of squares on one side of the board.
     * Used for testing. Should be deprecated and removed.
     */
    public int size() {
        return board.size();
    }

    /**
     * Return true iff the game is over (there are no moves, or
     * there is a tile with value 2048 on the board).
     */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /**
     * Add TILE to the board. There must be no Tile currently at the
     * same position.
     */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /**
     * Tilt the board toward SIDE. Return true iff this changes the board.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     * the same value, they are merged into one Tile of twice the original
     * value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     * tilt. So each move, every tile will only ever be part of at most one
     * merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     * value, then the leading two tiles in the direction of motion merge,
     * and the trailing tile does not.
     */
    public boolean tilt(Side side) {
        boolean changed;
        changed = false;

        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.

        // 设置移动方向
        board.setViewingPerspective(side);

        // 合并
        for (int col = 0; col < this.board.size(); col++) {
            int moveTo = board.size() - 1; // 当前正在从上到下处理第几块Tile
            int cur_p = prevTilePosition(col, this.board.size()); // 当前Tile的位置(row)
            while (cur_p >= 0) {
                Tile cur_tile = this.board.tile(col, cur_p);

                // 如果前面有Tile
                int prev_p = prevTilePosition(col, cur_p);
                if (prev_p >= 0) {
                    Tile prev_tile = this.board.tile(col, prev_p);

                    // 如果可以合并
                    if (cur_tile.value() == prev_tile.value()) {
                        boolean merged = this.board.move(col, cur_p, prev_tile);
                        assertTrue("Merged fail!", merged);
                        cur_tile = board.tile(col, cur_p);

                        prev_p = prevTilePosition(col, prev_p);

                        score += cur_tile.value();
                        changed = true;
                    }
                }

                // 如果可以移动
                if (moveTo != cur_p) {
                    this.board.move(col, moveTo, cur_tile);
                    changed = true;
                }

                moveTo--;
                cur_p = prev_p;
            }
        }

        // 很坑的一个地方，检查了好几个样例，board和expected都是一致的。。。
        // 但是测试用例在toString()后和expect就是不一样，方向有问题。
        // 看了看toString()和测试样例才发现toString()是会按照Model的Side进行字符化的，不是归一到NORTH！
        // 而expect始终是初始化(NORTH)后toString()的。。。而我们都board不是NORTH的话toString()就会不一致。
        // 因此处理完毕后都调整成NORTH算了
        board.setViewingPerspective(Side.NORTH);

        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    private int prevTilePosition(int col, int row) {
        for (int i = row - 1; i >= 0; i--) {
            if (this.board.tile(col, i) != null) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Checks if the game is over and sets the gameOver variable
     * appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /**
     * Returns true if at least one space on the Board is empty.
     * Empty spaces are stored as null.
     */
    public static boolean emptySpaceExists(Board b) {
        // TODO: Fill in this function.
        for (Tile tile : b) {
            if (tile == null) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        // TODO: Fill in this function.
        for (Tile tile : b) {
            if (tile == null) {
                continue;
            }

            if (tile.value() == MAX_PIECE) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        // TODO: Fill in this function.
        for (int row = 0; row < b.size(); row++) {
            for (int col = 0; col < b.size(); col++) {
                // 检查自己是否为空
                Tile tile = b.tile(col, row);
                if (tile == null) {
                    return true;
                }

                // 检查右侧是否可以合并
                // 如果说最后一列，不再继续往右检查
                if (col == b.size() - 1) {
                    continue;
                }
                Tile up_tile = b.tile(col + 1, row);
                if (up_tile != null && up_tile.value() == tile.value()) {
                    return true;
                }

                // 检查上方是否可以合并
                // 如果是最后一行，不再继续往上检查
                if (row == b.size() - 1) {
                    continue;
                }
                Tile right_tile = b.tile(col, row + 1);
                if (right_tile != null && right_tile.value() == tile.value()) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}
