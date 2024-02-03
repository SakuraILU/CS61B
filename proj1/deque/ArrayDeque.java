package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {

    private T[] items;
    private int size;
    private int next_first;
    private int next_last;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        next_first = 4;
        next_last = 5;
    }

    private void resize(int capacity) {
        T[] new_items = (T[]) new Object[capacity];

        int start = mod(next_first + 1);
        int end = mod(next_last);
        for (int i = start, j = 0; i != end; i = mod(i + 1), j++) {
            new_items[j] = items[i];
        }
        items = new_items;

        next_first = mod(-1);
        next_last = mod(size());
    }

    @Override
    public void addFirst(T item) {
        if (size() == items.length - 1) {
            resize(2 * items.length);
        }

        size++;

        items[mod(next_first)] = item;
        next_first = mod(next_first - 1);
    }

    @Override
    public void addLast(T item) {
        if (size() == items.length - 1) {
            resize(2 * items.length);
        }

        size++;

        items[mod(next_last)] = item;
        next_last = mod(next_last + 1);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        int start = mod(next_first + 1);
        int end = mod(next_last);
        for (int i = start; i != end; i++) {
            System.out.printf("%s ", items[i]);
        }
        System.out.println("");
    }

    @Override
    public T removeFirst() {
        if (size() > 16 && size() < items.length * 0.25) {
            resize((int) (0.5 * items.length));
        }

        if (size() == 0) {
            return null;
        }

        size--;

        T value = items[mod(next_first + 1)];
        next_first = mod(next_first + 1);

        return value;
    }

    @Override
    public T removeLast() {
        if (size() > 16 && size() < items.length * 0.25) {
            resize((int) (0.5 * items.length));
        }

        if (size() == 0) {
            return null;
        }

        size--;

        T value = items[mod(next_last - 1)];
        next_last = mod(next_last - 1);

        return value;
    }

    @Override
    public T get(int index) {
        if (index >= size()) {
            return null;
        }

        index = mod(next_first + 1 + index);

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

        ArrayDeque<T> other = (ArrayDeque<T>) o;

        if (other.size() != this.size()) {
            return false;
        }

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
        private int index;

        AllTIterator() {
            index = mod(next_first + 1);
        }

        public boolean hasNext() {
            return index != next_last;
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
