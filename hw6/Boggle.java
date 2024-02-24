import java.util.List;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import edu.princeton.cs.introcs.In;

public class Boggle {
    private static class Direction {
        int x;
        int y;

        public Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    // File path of dictionary file
    static String dictPath = "words.txt";
    private static Trie trieSet = new Trie();

    private static String path = "";
    private static PriorityQueue<String> words = new PriorityQueue<String>(new Comparator<String>() {
        @Override
        public int compare(String s1, String s2) {
            if (s1.length() != s2.length()) {
                return s1.length() - s2.length();
            }

            return s2.compareTo(s1);
        }
    });

    // 定义所有可能的方向
    private static final Direction[] directions = new Direction[] {
            new Direction(-1, 0), // 上
            new Direction(1, 0), // 下
            new Direction(0, -1), // 左
            new Direction(0, 1), // 右
            new Direction(-1, -1), // 左上
            new Direction(-1, 1), // 右上
            new Direction(1, -1), // 左下
            new Direction(1, 1) // 右下
    };
    private static boolean[][] marked;

    /**
     * Solves a Boggle puzzle.
     * 
     * @param k             The maximum number of words to return.
     * @param boardFilePath The file path to Boggle board file.
     * @return a list of words found in given Boggle board.
     *         The Strings are sorted in descending order of length.
     *         If multiple words have the same length,
     *         have them in ascending alphabetical order.
     */
    public static List<String> solve(int k, String boardFilePath) throws IllegalArgumentException {
        if (k <= 0) {
            throw new IllegalArgumentException();
        }

        buildTrie();
        char[][] board = readBoard(boardFilePath);

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                dfs(board, row, col, k);
            }
        }

        LinkedList<String> list = new LinkedList<String>();
        while (words.size() > 0) {
            String item = words.remove();
            list.addFirst(item);
        }

        return list;
    }

    private static void buildTrie() throws IllegalArgumentException {
        In wordsIn = new In(dictPath);
        if (!wordsIn.exists()) {
            throw new IllegalArgumentException();
        }

        while (wordsIn.hasNextLine()) {
            String word = wordsIn.readLine();
            trieSet.insert(word);
        }
    }

    private static char[][] readBoard(String boardFilePath) throws IllegalArgumentException {
        In boardIn = new In(boardFilePath);
        String[] lines = boardIn.readAllLines();
        marked = new boolean[lines.length][lines[0].length()];

        char[][] board = new char[lines.length][];
        for (int row = 0; row < lines.length; row++) {
            String line = lines[row];
            if (line.length() != lines[0].length()) {
                throw new IllegalArgumentException();
            }

            board[row] = new char[line.length()];
            for (int col = 0; col < line.length(); col++) {
                board[row][col] = line.charAt(col);
            }
        }

        return board;
    }

    private static void dfs(char[][] board, int i, int j, int k) {
        if (i < 0 || i >= board.length || j < 0 || j >= board[0].length) {
            return;
        }
        if (marked[i][j]) {
            return;
        }
        marked[i][j] = true;
        path += board[i][j];

        if (trieSet.startWith(path)) {
            if (path.length() > 2 && trieSet.contains(path)) {
                // add to words
                if (!words.contains(path)) {
                    words.add(path);
                    if (words.size() > k) {
                        words.remove();
                    }
                }
            }

            for (Direction dir : directions) {
                int x = i + dir.x;
                int y = j + dir.y;
                dfs(board, x, y, k);
            }
        }

        path = path.substring(0, path.length() - 1);
        marked[i][j] = false;

        return;
    }

    public static void main(String[] args) {
        List<String> results = solve(10, "smallBoard.txt");

        for (String word : results) {
            System.out.println(word);
        }
    }
}
