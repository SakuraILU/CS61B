import java.util.HashMap;
import java.util.Map;

public class Trie {
    public class Node {
        private boolean isKey;
        private Map<Character, Node> children;

        Node() {
            isKey = false;
            children = new HashMap<>();
        }

        public boolean isKey() {
            return isKey;
        }

        public Map<Character, Node> children() {
            return children;
        }
    }

    private Node root;
    int size;

    Trie() {
        root = createNode();
        size = 0;
    }

    public void insert(String key) {
        insert(root, key, 0);
    }

    private void insert(Node node, String key, int i) {
        if (i == key.length()) {
            if (!node.isKey) {
                node.isKey = true;
                size++;
            }
            return;
        }

        char c = key.charAt(i);
        if (!node.children.containsKey(c)) {
            node.children.put(c, createNode());
        }
        insert(node.children.get(c), key, i + 1);

        return;
    }

    public boolean contains(String key) {
        Node node = get(root, key, 0);
        if (node == null || !node.isKey) {
            return false;
        }

        return true;
    }

    public boolean startWith(String prefix) {
        return get(root, prefix, 0) != null;
    }

    private Node get(Node node, String prefix, int i) {
        if (i == prefix.length()) {
            return node;
        }

        char c = prefix.charAt(i);
        if (node.children.containsKey(c)) {
            return get(node.children.get(c), prefix, i + 1);
        }

        return null;
    }

    public int size() {
        return size;
    }

    private Node createNode() {
        return new Node();
    }
}
