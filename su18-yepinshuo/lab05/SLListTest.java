import org.junit.Test;
import static org.junit.Assert.*;

public class SLListTest {

    @Test
    public void testSLListAdd() {
        SLList test1 = SLList.of(1, 3, 5);
        SLList test2 = new SLList();

        test1.add(1, 2);
        test1.add(3, 4);
        assertEquals(5, test1.size());
        assertEquals(3, test1.get(2));
        assertEquals(4, test1.get(3));

        test2.add(1, 1);
        assertEquals(1, test2.get(0));
        assertEquals(1, test2.size());

        test2.add(10, 10);
        assertEquals(10, test2.get(1));
        test1.add(0, 0);
        assertEquals(SLList.of(0, 1, 2, 3, 4, 5), test1);
    }

    @Test
    public void testSLListReverse() {
        SLList s0 = new SLList();
        s0.reverse();
        assertEquals(new SLList(), s0);

        SLList s1 = SLList.of(1);
        s1.reverse();
        assertEquals(s1, s1);
        assertEquals(SLList.of(1), s1);

        SLList s2 = SLList.of(1, 3);
        s2.reverse();
        assertEquals(SLList.of(3, 1), s2);
        assertNotEquals(SLList.of(1, 3), s2);

        SLList s3 = SLList.of(1, 3, 5);
        s3.reverse();
        assertEquals(SLList.of(5, 3, 1), s3);
        assertNotEquals(SLList.of(1, 3, 5), s3);

        SLList s4 = SLList.of(1, 3, 5, 7);
        s4.reverse();
        assertEquals(SLList.of(7, 5, 3, 1), s4);
        assertNotEquals(SLList.of(1, 3, 5, 7), s4);

        SLList s5 = SLList.of(1, 3, 5, 7, 9);
        s5.reverse();
        assertEquals(SLList.of(9, 7, 5, 3, 1), s5);
        assertNotEquals(SLList.of(1, 3, 5, 7, 9), s5);
    }
}
