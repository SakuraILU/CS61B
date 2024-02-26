import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

public class TrieMap<T> {
    private class Node {
        private T value;
        private Map<Character, Node> children;

        Node() {
            children = new HashMap<>();
        }
    }

    private Node root;
    private int size;

    TrieMap() {
        root = new Node();
        size = 0;
    }

    public void put(String key, T value) {

        put(root, key, value, 0);
    }

    private void put(Node node, String key, T value, int i) {
        if (i == key.length()) {
            if (node.value == null) {
                size++;
                node.value = value;
            }
            return;
        }

        char c = key.charAt(i);
        if (!node.children.containsKey(c)) {
            Node child = new Node();
            node.children.put(c, child);
        }

        put(node.children.get(c), key, value, i + 1);
        return;
    }

    public T get(String key) {
        Node node = get(root, key, 0);
        if (node == null) {
            return null;
        }
        return node.value;
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

    public boolean containsKey(String key) {
        return get(key) != null;
    }

    public Set<String> keySet() {
        Set<String> set = collect(root);
        return set;
    }

    public Set<String> keySetWithPrefix(String prefix) {
        Node node = get(root, prefix, 0);
        if (node == null) {
            return null;
        }

        Set<String> sets = new HashSet<>();
        for (String name : collect(node)) {
            sets.add(prefix + name);
        }

        return sets;
    }

    private Set<String> collect(Node node) {
        Set<String> sets = new HashSet<>();

        for (Map.Entry<Character, Node> entry : node.children.entrySet()) {
            char c = entry.getKey();
            Node child = entry.getValue();
            for (String name : collect(child)) {
                sets.add(c + name);
            }
        }

        // if this is a key node, ready to grow up
        if (node.value != null) {
            sets.add("");
        }

        return sets;
    }

    public boolean remove(String key) {
        if (!containsKey(key)) {
            return false;
        }

        root = remove(root, key, 0);
        if (root == null) {
            root = new Node();
        }

        return true;
    }

    private Node remove(Node node, String key, int i) {
        if (i == key.length()) {
            size--;
            node.value = null;
        } else {
            char c = key.charAt(i);
            Node child = remove(node.children.get(c), key, i + 1);
            if (child == null) {
                node.children.remove(c);
            } else {
                node.children.put(c, child);
            }
        }

        if (node.children.size() == 0 && node.value == null) {
            return null;
        }

        return node;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size() == 0;
    }
}
