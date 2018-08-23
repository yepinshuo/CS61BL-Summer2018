import java.util.Iterator;
import java.util.LinkedList;

public class HashMap<K, V> implements Map61BL<K, V> {

    /* Instance variables here? */
    public LinkedList<Entry<K, V>>[] maps;
    public int length;
    public int size;
    public double loadFactor;

    public HashMap() {
        this.size = 0;
        this.length = 16;
        this.maps = new LinkedList[length];
        this.loadFactor = 0.75;

        for (int i = 0; i < length; i++) {
            maps[i] = new LinkedList<Entry<K, V>>();
        }
    }

    public HashMap(int initialCapacity) {
        this.size = 0;
        this.length = initialCapacity;
        this.maps = new LinkedList[length];
        this.loadFactor = 0.75;

        for (int i = 0; i < length; i++) {
            maps[i] = new LinkedList<Entry<K, V>>();
        }
    }

    public HashMap(int initialCapacity, float loadFactor) {
        this.size = 0;
        this.length = initialCapacity;
        this.maps = new LinkedList[length];
        this.loadFactor = loadFactor;

        for (int i = 0; i < length; i++) {
            maps[i] = new LinkedList<Entry<K, V>>();
        }
    }

    /* Returns true if the map contains the KEY. */
    public boolean containsKey(K key) {
        for (Entry name : maps[Math.floorMod(key.hashCode(), length)]) {
            if (name.key == key) {
                return true;
            }
        }
        return false;
    }

    /* Returns the value for the specified KEY. If KEY is not found, return
       null. */
    public V get(K key) {
        for (Entry<K, V> name : maps[Math.floorMod(key.hashCode(), length)]) {
            if (name.key == key) {
                return name.value;
            }
        }
        return null;
    }

    /* Puts a (KEY, VALUE) pair into this map. If the KEY already exists in the
       SimpleNameMap, replace the current corresponding value with VALUE. */
    public void put(K key, V value) {
        Entry<K, V> name = new Entry(key, value);
        if (!containsKey(key)) {
            maps[Math.floorMod(key.hashCode(), length)].add(name);
            size += 1;
        } else {
            remove(key);
            maps[Math.floorMod(key.hashCode(), length)].add(name);
            size += 1;
        }
        if (size / (length + 0.0) > loadFactor) {
            resize();
        }
    }

    /* Removes a single entry, KEY, from this table and return the VALUE if
       successful or NULL otherwise. */
    public V remove(K key) {
        if (containsKey(key)) {
            int count = 0;
            for (Entry<K, V> name : maps[Math.floorMod(key.hashCode(), length)]) {
                if (name.key == key) {
                    break;
                }
                count += 1;
            }
            size -= 1;
            return maps[Math.floorMod(key.hashCode(), length)].remove(count).value;
        }
        return null;
    }

    public boolean remove(K key, V value) {
        if (containsKey(key)) {
            Entry<K, V> name = new Entry<>(key, value);
            for (LinkedList names : maps) {
                if (names.contains(name)) {
                    return names.remove(name);
                }
            }
        }
        return false;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return length;
    }

    public void clear() {
        this.size = 0;
        this.maps = new LinkedList[length];

        for (int i = 0; i < length; i++) {
            maps[i] = new LinkedList<Entry<K, V>>();
        }
    }

    public void resize() {
        this.length = length * 2;
        LinkedList<Entry<K, V>>[] newMaps = new LinkedList[length];
        for (int i = 0; i < length; i++) {
            newMaps[i] = new LinkedList<Entry<K, V>>();
        }

        for (LinkedList<Entry<K, V>> map : maps) {
            for (Entry name : map) {
                newMaps[Math.floorMod(name.key.hashCode(), length)].add(name);
            }
        }
        maps = newMaps;
    }

    public Iterator<K> iterator() {
        return new HashMapIterator();
    }

    public class HashMapIterator implements Iterator<K> {
        LinkedList<Entry<K, V>> currLink;
        int arrayIndex;
        int currLinkIndex;
        int i;

        public HashMapIterator() {
            arrayIndex = 0;
            currLink = maps[arrayIndex];
            currLinkIndex = 0;
            i = 0;
        }

        @Override
        public K next() {
            if (currLinkIndex <= currLink.size() - 1) {
                K result = currLink.get(currLinkIndex).key;
                currLinkIndex += 1;
                i++;
                return result;
            } else {
                currLinkIndex = 0;
                arrayIndex += 1;
                currLink = maps[arrayIndex];
                return next();
            }
        }

        @Override
        public boolean hasNext() {
            return i < size() && size() != 0;
        }
    }

    private static class Entry<K, V> {

        private K key;
        private V value;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        /* Returns true if this key matches with the OTHER's key. */
        public boolean keyEquals(Entry other) {
            return key.equals(other.key);
        }

        /* Returns true if both the KEY and the VALUE match. */
        @Override
        public boolean equals(Object other) {
            return (other instanceof Entry
                    && key.equals(((Entry) other).key)
                    && value.equals(((Entry) other).value));
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    public static void main(String[] args) {
        HashMap<String, Integer> studentIDs = new HashMap<String, Integer>();
        studentIDs.put("christine", 12345);
        studentIDs.put("kevin", 345);
        studentIDs.put("alex", 612);
        studentIDs.put("carlo", 12345);

        Iterator<String> it = studentIDs.iterator();
        String key = "";
        while (it.hasNext()) {
            key = it.next();
            System.out.println(key);
        }
    }
}
