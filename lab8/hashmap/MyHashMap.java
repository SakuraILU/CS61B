package hashmap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

/**
 * A hash table-backed Map implementation. Provides amortized constant time
 * access to elements via get(), remove(), and put() in the best case.
 *
 * Assumes null keys will never be inserted, and does not resize down upon
 * remove().
 * 
 * @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private static final int DEFAULT_BUCKET_NUM = 16;
    private static final double DEFAULT_MAXLOAD = 0.75;
    private Collection<Node>[] buckets;
    private int bucketNum;
    private int size;
    private double maxLoad;

    /** Constructors */
    public MyHashMap() {
        this(DEFAULT_BUCKET_NUM, DEFAULT_MAXLOAD);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, DEFAULT_MAXLOAD);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad     maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.bucketNum = initialSize;
        this.maxLoad = maxLoad;
        this.buckets = createTable(bucketNum);
        this.size = 0;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     * 1. Insert items (`add` method)
     * 2. Remove items (`remove` method)
     * 3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new HashSet<Node>();
    }

    @SuppressWarnings("unchecked")
    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] buckets = new Collection[tableSize];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = createBucket();
        }
        return buckets;
    }

    // TODO: Implement the methods of the Map61B Interface below

    @Override
    // Your code won't compile until you do so!
    /** Removes all of the mappings from this map. */
    public void clear() {
        size = 0;
        for (Collection<Node> bucket : buckets) {
            bucket.clear();
        }
    }

    @Override
    /** Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        for (Collection<Node> bucket : buckets) {
            for (Node node : bucket) {
                if (key.equals(node.key)) {
                    return node.value;
                }
            }
        }

        return null;
    }

    @Override
    /** Returns the number of key-value mappings in this map. */
    public int size() {
        return size;
    }

    @Override
    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */
    public void put(K key, V value) {
        int load = size / bucketNum;
        if (load >= maxLoad) {
            resize(2 * bucketNum);
        }

        int i = Math.floorMod(key.hashCode(), bucketNum);
        Collection<Node> bucket = buckets[i];

        for (Node node : bucket) {
            if (key.equals(node.key)) {
                node.value = value;
                return;
            }
        }

        bucket.add(createNode(key, value));
        size++;
    }

    @Override
    /** Returns a Set view of the keys contained in this map. */
    public Set<K> keySet() {
        Set<K> set = new HashSet<K>();
        for (Collection<Node> bucket : buckets) {
            for (Node node : bucket) {
                set.add(node.key);
            }
        }
        return set;
    }

    @Override
    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    public V remove(K key) {
        int i = Math.floorMod(key.hashCode(), bucketNum);
        Collection<Node> bucket = buckets[i];

        Iterator<Node> itr = bucket.iterator();
        while (itr.hasNext()) {
            Node node = itr.next();
            if (key.equals(node.key)) {
                itr.remove();
                size--;
                return node.value;
            }
        }

        return null;
    }

    @Override
    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     */
    public V remove(K key, V value) {
        int i = Math.floorMod(key.hashCode(), bucketNum);
        Collection<Node> bucket = buckets[i];

        Node node = createNode(key, value);

        if (bucket.remove(node)) {
            size--;
            return value;
        }

        return null;
    }

    public Iterator<K> iterator() {
        return keySet().iterator();
    }

    private void resize(int capacity) {
        Collection<Node>[] newBuckets = createTable(capacity);

        for (Collection<Node> bucket : buckets) {
            for (Node node : bucket) {
                int i = Math.floorMod(node.key.hashCode(), capacity);
                Collection<Node> newBucket = newBuckets[i];
                newBucket.add(node);
            }
        }

        buckets = newBuckets;
        bucketNum = capacity;
    }
}
