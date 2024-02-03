package deque;

import static org.junit.Assert.assertTrue;

import java.util.Iterator;

public class ArrayDeque<Item> implements Deque<Item>, Iterable<Item> {

    private Item[] items;
    private int size;
    private int next_first;
    private int next_last;

    public ArrayDeque() {
        items = (Item[]) new Object[8];
        size = 0;
        next_first = 4;
        next_last = 5;
    }

    private void resize(int capacity) {
        Item[] new_items = (Item[]) new Object[capacity];

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
    public void addFirst(Item item) {
        if (size() == items.length - 1) {
            resize(2 * items.length);
        }

        size++;

        items[mod(next_first)] = item;
        next_first = mod(next_first - 1);
    }

    @Override
    public void addLast(Item item) {
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
    public Item removeFirst() {
        if (size() > 16 && size() < items.length * 0.25) {
            resize((int) (0.5 * items.length));
        }

        if (size() == 0) {
            return null;
        }

        size--;

        Item value = items[mod(next_first + 1)];
        next_first = mod(next_first + 1);

        return value;
    }

    @Override
    public Item removeLast() {
        if (size() > 16 && size() < items.length * 0.25) {
            resize((int) (0.5 * items.length));
        }

        if (size() == 0) {
            return null;
        }

        size--;

        Item value = items[mod(next_last - 1)];
        next_last = mod(next_last - 1);

        return value;
    }

    @Override
    public Item get(int index) {
        if (index >= size()) {
            return null;
        }

        index = mod(next_first + 1 + index);

        return items[index];
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }

        ArrayDeque<Item> other = (ArrayDeque<Item>) o;

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
        private int index;

        AllItemIterator() {
            index = mod(next_first + 1);
        }

        public boolean hasNext() {
            return index != next_last;
        }

        public Item next() {
            Item value = items[index];
            index = mod(index + 1);

            return value;
        }

        public Iterator<Item> iterator() {
            return this;
        }
    }

    public Iterator<Item> iterator() {
        return new AllItemIterator();
    }

    private int mod(int i) {
        if (i < 0) {
            return i + items.length;
        } else {
            return i % items.length;
        }
    }
}
