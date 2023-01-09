import java.util.*;

public class HashTable<K, V> implements Iterable<KeyValue<K, V>> {
    private LinkedList<KeyValue<K, V>>[] slots;
    private final static double LOAD_FACTOR = 0.80d;

    private final static int INITIAL_CAPACITY = 16;

    private int count;
    private int capacity;

    public HashTable() {
        this(INITIAL_CAPACITY);
    }

    public HashTable(int capacity) {
        this.slots = new LinkedList[capacity];

        this.count = 0;
        this.capacity = capacity;
    }

    public void add(K key, V value) {
        this.growIfNeeded();

        int index = findSlotNumber(key);

        LinkedList<KeyValue<K, V>> slot = this.slots[index];

        if (slot == null) {
            slot = new LinkedList<>();
        }

        for (KeyValue<K, V> current : slot) {
            if (current.getKey().equals(key)) {
                throw new IllegalArgumentException("Key already exist " + key);
            }
        }

        KeyValue<K, V> element = new KeyValue<>(key, value);
        slot.addLast(element);

        this.slots[index] = slot;
        this.count++;
    }


    private int findSlotNumber(K key) {
        return Math.abs(key.hashCode() % this.capacity);
    }

    private void growIfNeeded() {
        if (((double) this.count + 1) / this.capacity > LOAD_FACTOR) {
            this.grow();
        }
    }

    private void grow() {
        HashTable<K, V> newTable = new HashTable<>(this.capacity * 2);

        for (LinkedList<KeyValue<K, V>> slot : this.slots) {
            if (slot != null) {
                slot.forEach(p -> newTable.add(p.getKey(), p.getValue()));
            }
        }

        this.slots = newTable.slots;
        this.capacity *= 2;
    }

    public int size() {
        return this.count;
    }

    public int capacity() {
        return this.capacity;
    }

    public boolean addOrReplace(K key, V value) {
        this.growIfNeeded();

        int index = findSlotNumber(key);

        LinkedList<KeyValue<K, V>> slot = this.slots[index];

        if (slot == null) {
            slot = new LinkedList<>();
        }

        boolean updated = false;
        for (KeyValue<K, V> current : slot) {
            if (current.getKey().equals(key)) {
                current.setValue(value);
                updated = true;
            }
        }

        if (!updated) {
            KeyValue<K, V> element = new KeyValue<>(key, value);
            slot.addLast(element);
            this.count++;
        }

        this.slots[index] = slot;

        return !updated;
    }

    public V get(K key) {
        KeyValue<K, V> pair = find(key);
        if (pair == null) {
            throw new IllegalArgumentException();
        }
        return pair.getValue();
    }

    public KeyValue<K, V> find(K key) {
        int index = this.findSlotNumber(key);

        LinkedList<KeyValue<K, V>> slot = this.slots[index];

        if (slot == null) {
            return null;
        }

        for (KeyValue<K, V> pair : slot) {
            if (pair.getKey().equals(key)) {
                return pair;
            }
        }

        return null;
    }

    public boolean containsKey(K key) {
        return this.find(key) != null;
    }

    public boolean remove(K key) {
        int index = findSlotNumber(key);

        LinkedList<KeyValue<K, V>> slot = this.slots[index];

        if (slot == null) {
            return false;
        }

        KeyValue<K, V> toRemove = null;
        for (KeyValue<K, V> pair : slot) {
            if (pair.getKey().equals(key)) {
                toRemove = pair;

                break;
            }
        }

        boolean result = toRemove != null && slot.remove(toRemove);

        if (result) {
            this.count--;
        }

        return result;
    }

    public void clear() {
        this.capacity = INITIAL_CAPACITY;
        this.count = 0;
        this.slots = new LinkedList[this.capacity];
    }

    public Iterable<K> keys() {
        List<K> keys = new ArrayList<>();

        for (KeyValue<K, V> pair : this) {
            keys.add(pair.getKey());
        }

        return keys;
    }

    public Iterable<V> values() {
        List<V> values = new ArrayList<>();

        for (KeyValue<K, V> pair : this) {
            values.add(pair.getValue());
        }

        return values;
    }

    @Override
    public Iterator<KeyValue<K, V>> iterator() {
        return new HashIterator();
    }

    private class HashIterator implements Iterator<KeyValue<K, V>> {
        Deque<KeyValue<K, V>> elements;

        HashIterator() {
            this.elements = new ArrayDeque<>();

            for (LinkedList<KeyValue<K, V>> slot : slots) {
                if (slot != null) {
                    elements.addAll(slot);
                }
            }
        }

        @Override
        public boolean hasNext() {
            return !elements.isEmpty();
        }

        @Override
        public KeyValue<K, V> next() {
            if (!hasNext()) {
                throw new IllegalArgumentException("Empty hash table");
            }
            return elements.poll();
        }
    }
}
