package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class Node {
        public T value;
        public Node next;
        public Node prev;

        public Node(T value, Node next, Node prev) {
            this.value = value;
            this.next = next;
            this.prev = prev;
        }
    }

    private Node sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        size++;

        Node first = sentinel.next;

        Node n = new Node(item, first, sentinel);
        sentinel.next = n;
        first.prev = n;
    }

    @Override
    public void addLast(T item) {
        size++;

        Node last = sentinel.prev;

        Node n = new Node(item, sentinel, last);
        last.next = n;
        sentinel.prev = n;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node itr = sentinel.next;

        while (itr != sentinel) {
            System.out.printf("%s ", itr.value);
            sentinel = sentinel.next;
        }
        System.out.println("");
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }

        T val = sentinel.next.value;

        size--;

        Node first = sentinel.next.next;
        sentinel.next = first;
        first.prev = sentinel;

        return val;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }

        T val = sentinel.prev.value;

        size--;

        Node last = sentinel.prev.prev;
        sentinel.prev = last;
        last.next = sentinel;

        return val;
    }

    @Override
    public T get(int index) {
        if (index >= size()) {
            return null;
        }

        int rank = 0;

        Node itr = sentinel.next;
        while (itr != sentinel) {
            if (rank == index) {
                return itr.value;
            }
            itr = itr.next;
            rank++;
        }

        return null;
    }

    public T getRecursive(int index) {
        if (index >= size()) {
            return null;
        }

        return getRecursive(sentinel.next, index);
    }

    private T getRecursive(Node node, int index) {
        if (index == 0) {
            return node.value;
        }

        return getRecursive(node.next, index - 1);
    }

    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        // deques with different types can be equal if they have the same items
        if (o == null || !(o instanceof Deque)) {
            return false;
        }

        LinkedListDeque<T> other = (LinkedListDeque<T>) o;

        if (other.size() != this.size()) {
            return false;
        }

        Iterator<T> itr_this = iterator();
        Iterator<T> itr_other = other.iterator();
        while (itr_this.hasNext()) {
            T value1 = itr_this.next();
            T value2 = itr_other.next();
            if (!value1.equals(value2)) {
                return false;
            }
        }

        return true;
    }

    private class AllTIterator implements Iterator<T>, Iterable<T> {
        private Node itr;

        AllTIterator() {
            itr = sentinel.next;
        }

        public boolean hasNext() {
            return itr != sentinel;
        }

        public T next() {
            T value = itr.value;

            itr = itr.next;

            return value;
        }

        public Iterator<T> iterator() {
            return this;
        }

    }

    @Override
    public Iterator<T> iterator() {
        return new AllTIterator();
    }
}
