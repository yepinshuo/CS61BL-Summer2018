import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.Objects;
import java.util.stream.Collectors;


public class DBTable<T> {
    private List<T> entries;

    public DBTable() {
        this.entries = new ArrayList<>();
    }

    public DBTable(Collection<T> lst) {
        entries = new ArrayList<>(lst);
    }

    public void add(T t) {
        entries.add(t);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DBTable<?> other = (DBTable<?>) o;
        return Objects.equals(entries, other.entries);
    }

    /** Add all items from a collection to the table. */
    public void add(Collection<T> col) {
        col.forEach(this::add);
    }

    /** Returns a copy of the entries in this table. */
    List<T> getEntries() {
        return new ArrayList<>(entries);
    }

    /**
     * Returns a list of entries sorted based on the natural ordering of the
     * results of the getter. Non-destructive.
     */
    public <R extends Comparable<R>> List<T> getOrderedBy(Function<T, R> getter) {
        // TODO
        List<T> list = getEntries();
        List<T> newlist = list.stream()
                .sorted((o1, o2) -> getter.apply(o1).compareTo(getter.apply(o2)))
                .collect(Collectors.toList());
        return newlist;
    }

    /**
     * Returns a list of entries whose value returned from the getter is found
     * in the whitelist. Non-destructive.
     */
    public <R> List<T> getWhitelisted(Function<T, R> getter, Collection<R> whitelist) {
        // TODO
        List<T> list = getEntries();
        List<T> newlist = list.stream()
                .filter(o -> whitelist.contains(getter.apply(o)))
                .collect(Collectors.toList());
        return newlist;
    }

    /**
     * Returns a new DBTable that contains the elements as obtained by the
     * getter. For example, getting a DBTable of usernames would look like:
     * DBTable<String> names = table.getSubtableOf(User::getUsername);
     */
    public <R> DBTable<R> getSubtableOf(Function<T, R> getter) {
        // TODO
        List<T> list = getEntries();
        List<R> newlist = list.stream().map(getter::apply).collect(Collectors.toList());
        DBTable<R> elements = new DBTable<>(newlist);
        return elements;
    }

    public static void main(String[] args) {
        List<User> users = Arrays.asList(
                new User(2, "Christine", ""),
                new User(4, "Kevin", ""),
                new User(5, "Alex", ""),
                new User(1, "Lauren", ""),
                new User(1, "Catherine", "")
                );
        DBTable<User> t = new DBTable<>(users);
        List<User> l = t.getOrderedBy(User::getName);
        l.forEach(System.out::println);
    }
}
