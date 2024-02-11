package bstmap;

import java.util.*;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private class Node {
        public K key;
        public V value;
        public Node left;
        public Node right;

        Node(K k, V v) {
            this.key = k;
            this.value = v;
            left = null;
            right = null;
        }
    }

    Node root;
    int size;

    @Override
    /** Removes all of the mappings from this map. */
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    /* Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        return find(root, key) != null;
    }

    @Override
    /*
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        Node n = find(root, key);
        if (n == null) {
            return null;
        }

        return n.value;
    }

    private Node find(Node node, K key) {
        if (node == null) {
            return null;
        }

        int cmpRes = key.compareTo(node.key);
        if (cmpRes == 0) {
            return node;
        } else if (cmpRes < 0) {
            return find(node.left, key);
        } else {
            return find(node.right, key);
        }
    }

    @Override
    /* Returns the number of key-value mappings in this map. */
    public int size() {
        return size;
    }

    @Override
    /* Associates the specified value with the specified key in this map. */
    public void put(K key, V value) {
        root = insert(root, key, value);
    }

    private Node insert(Node node, K key, V value) {
        if (node == null) {
            size++;
            return new Node(key, value);
        }

        int cmpRes = key.compareTo(node.key);
        if (cmpRes == 0) {
            node.value = value;
        } else if (cmpRes < 0) {
            node.left = insert(node.left, key, value);
        } else {
            node.right = insert(node.right, key, value);
        }

        return node;
    }

    @Override
    /*
     * Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException.
     */
    public Set<K> keySet() {
        return keySet(root);
    }

    private Set<K> keySet(Node node) {
        Set<K> set = new HashSet<K>();
        if (node == null) {
            return set;
        }

        set.addAll(keySet(node.left));
        set.addAll(keySet(node.right));
        set.add(node.key);

        return set;
    }

    @Override
    /*
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    public V remove(K key) {
        V v = get(key);
        if (v == null) {
            return null;
        }
        root = delete(root, key);
        return v;
    }

    @Override
    /*
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.
     */
    public V remove(K key, V value) {
        V v = get(key);
        if (v == null || !v.equals(value)) {
            return null;
        }
        root = delete(root, key);
        return v;
    }

    private Node delete(Node node, K key) {
        if (node == null) {
            return null;
        }

        int cmpRes = key.compareTo(node.key);
        if (cmpRes == 0) {
            size--;
            if (node.left == null && node.right == null) {
                return null;
            } else if (node.right == null) {
                return node.left;
            } else if (node.left == null) {
                return node.right;
            } else {
                Node rightMin = findMin(node.right);
                node.right = delete(node.right, rightMin.key);
                rightMin.left = node.left;
                rightMin.right = node.right;
                return rightMin;
            }
        } else if (cmpRes < 0) {
            node.left = delete(node.left, key);
        } else {
            node.right = delete(node.right, key);
        }

        return node;
    }

    private Node findMin(Node node) {
        Node curNode = node;
        while (curNode.left != null) {
            curNode = curNode.left;
        }

        return curNode;
    }

    public Iterator<K> iterator() {
        return keySet().iterator();
    }
}
