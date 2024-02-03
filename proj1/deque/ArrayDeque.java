package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {

    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 4;
        nextLast = 5;
    }

    private void resize(int capacity) {
        T[] newItems = (T[]) new Object[capacity];

        int i = mod(nextFirst + 1);
        for (int j = 0; j < size(); j++, i = mod(i + 1)) {
            newItems[j] = items[i];
        }
        items = newItems;

        nextFirst = mod(-1);
        nextLast = mod(size());
    }

    @Override
    public void addFirst(T item) {
        if (size() == items.length) {
            resize(2 * items.length);
        }

        size++;

        items[mod(nextFirst)] = item;
        nextFirst = mod(nextFirst - 1);
    }

    @Override
    public void addLast(T item) {
        if (size() == items.length) {
            resize(2 * items.length);
        }

        size++;

        items[mod(nextLast)] = item;
        nextLast = mod(nextLast + 1);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        int i = mod(nextFirst + 1);
        for (int k = 0; k < size(); k++, i = mod(i + 1)) {
            System.out.printf("%s ", items[i]);
        }
        System.out.println("");
    }

    @Override
    public T removeFirst() {
        if (size() >= 16 && size() <= items.length * 0.25) {
            resize((int) (0.5 * items.length));
        }

        if (size() == 0) {
            return null;
        }

        size--;

        T value = items[mod(nextFirst + 1)];
        nextFirst = mod(nextFirst + 1);

        return value;
    }

    @Override
    public T removeLast() {
        if (size() >= 16 && size() <= items.length * 0.25) {
            resize((int) (0.5 * items.length));
        }

        if (size() == 0) {
            return null;
        }

        size--;

        T value = items[mod(nextLast - 1)];
        nextLast = mod(nextLast - 1);

        return value;
    }

    @Override
    public T get(int index) {
        if (index >= size()) {
            return null;
        }

        index = mod(nextFirst + 1 + index);

        return items[index];
    }

    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o == null || !(o instanceof Deque)) {
            return false;
        }

        Deque<T> other = (Deque<T>) o;

        if (other.size() != this.size()) {
            return false;
        }

        if (other.size() != this.size()) {
            return false;
        }

        for (int i = 0; i < size(); i++) {
            if (!other.get(i).equals(this.get(i))) {
                return false;
            }
        }

        return true;
    }

    private class AllTIterator implements Iterator<T>, Iterable<T> {
        private int index;

        AllTIterator() {
            index = mod(nextFirst + 1);
        }

        public boolean hasNext() {
            return index != nextLast;
        }

        public T next() {
            T value = items[index];
            index = mod(index + 1);

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

    private int mod(int i) {
        if (i < 0) {
            return i + items.length;
        } else {
            return i % items.length;
        }
    }
}
