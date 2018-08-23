import java.util.LinkedList;

public class SimpleNameMap {

    /* Instance variables here? */
    public LinkedList<Entry>[] maps;
    public int length;
    public int size;

    public SimpleNameMap(int length) {
        this.size = 0;
        this.length = length;
        this.maps = new LinkedList[length];
    }

    /* Returns true if the given KEY is a valid name that starts with A - Z. */
    private static boolean isValidName(String key) {
        return 'A' <= key.charAt(0) && key.charAt(0) <= 'Z';
    }

    /* Returns true if the map contains the KEY. */
    boolean containsKey(String key) {
        if (isValidName(key)) {
            for (Entry name : maps[Math.floorMod(key.hashCode(), length)]) {
                if (name.key == key) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    /* Returns the value for the specified KEY. If KEY is not found, return
       null. */
    String get(String key) {
        if (isValidName(key)) {
            for (Entry name : maps[Math.floorMod(key.hashCode(), length)]) {
                if (name.key == key) {
                    return name.value;
                }
            }
            return null;
        }
        return null;
    }

    /* Puts a (KEY, VALUE) pair into this map. If the KEY already exists in the
       SimpleNameMap, replace the current corresponding value with VALUE. */
    void put(String key, String value) {
        if (isValidName(key)) {
            Entry name = new Entry(key, value);
            if (!containsKey(key)) {
                maps[Math.floorMod(key.hashCode(), length)].add(name);
                size += 1;
            } else {
                remove(key);
                maps[Math.floorMod(key.hashCode(), length)].add(name);
            }
        }
        if (size / length >= 0.75) {
            resize();
        }
    }

    /* Removes a single entry, KEY, from this table and return the VALUE if
       successful or NULL otherwise. */
    String remove(String key) {
        if (isValidName(key) && containsKey(key)) {
            int count = 0;
            for (Entry name : maps[Math.floorMod(key.hashCode(), length)]) {
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

    int size() {
        return size;
    }

    void resize() {
        length = length * 2;
        LinkedList<Entry>[] newMaps = new LinkedList[length];
        for (LinkedList<Entry> map : maps) {
            for (Entry name : map) {
                newMaps[Math.floorMod(name.key.hashCode(), length)].add(name);
            }
        }
        maps = newMaps;
    }

    private static class Entry {

        private String key;
        private String value;

        Entry(String key, String value) {
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
}
