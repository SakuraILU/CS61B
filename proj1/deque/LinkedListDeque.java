package deque;

import java.util.Iterator;

public class LinkedListDeque<Item> implements Deque<Item>, Iterable<Item> {
    private class Node {
        public Item value;
        public Node next;
        public Node prev;

        public Node(Item value, Node next, Node prev) {
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
    public void addFirst(Item item) {
        size++;

        Node first = sentinel.next;

        Node n = new Node(item, first, sentinel);
        sentinel.next = n;
        first.prev = n;
    }

    @Override
    public void addLast(Item item) {
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
    public Item removeFirst() {
        if (isEmpty()) {
            return null;
        }

        Item val = sentinel.next.value;

        size--;

        Node first = sentinel.next.next;
        sentinel.next = first;
        first.prev = sentinel;

        return val;
    }

    @Override
    public Item removeLast() {
        if (isEmpty()) {
            return null;
        }

        Item val = sentinel.prev.value;

        size--;

        Node last = sentinel.prev.prev;
        sentinel.prev = last;
        last.next = sentinel;

        return val;
    }

    @Override
    public Item get(int index) {
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

    public Item getRecursive(int index) {
        if (index >= size()) {
            return null;
        }

        return getRecursive(sentinel.next, index);
    }

    private Item getRecursive(Node node, int index) {
        if (index == 0) {
            return node.value;
        }

        return getRecursive(node.next, index - 1);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }

        LinkedListDeque<Item> other = (LinkedListDeque<Item>) o;

        if (other.size() != this.size()) {
            return false;
        }

        Iterator<Item> itr_this = iterator();
        Iterator<Item> itr_other = other.iterator();
        while (itr_this.hasNext()) {
            Item value1 = itr_this.next();
            Item value2 = itr_other.next();
            if (!value1.equals(value2)) {
                return false;
            }
        }

        return true;
    }

    private class AllItemIterator implements Iterator<Item>, Iterable<Item> {
        private Node itr;

        AllItemIterator() {
            itr = sentinel.next;
        }

        public boolean hasNext() {
            return itr != sentinel;
        }

        public Item next() {
            Item value = itr.value;

            itr = itr.next;

            return value;
        }

        public Iterator<Item> iterator() {
            return this;
        }
    }

    public Iterator<Item> iterator() {
        return new AllItemIterator();
    }
}
