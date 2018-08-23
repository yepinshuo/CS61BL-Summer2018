import java.util.List;
import java.util.ArrayList;
public class CodingChallenges {

    /**
     * Return the missing number from an array of length N - 1 containing all
     * the values from 0 to N except for one missing number.
     */
    public static int missingNumber(int[] values) {
        List<Integer> lst = new ArrayList<>();
        for (int i = 0; i <= values.length - 1; i++) {
            lst.add(i, values[i]);
        }
        for (int j = 0; j <= values.length; j++) {
            if (!lst.contains(j)) {
                return j;
            }
        }
        return 0;
    }

    /** Returns true if and only if two integers in the array sum up to n. */
    public static boolean sumTo(int[] values, int n) {
        for (int i = 0; i <= values.length - 2; i++) {
            for (int j = i + 1; j <= values.length - 1; j++) {
                if (values[i] + values[j] == n) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if and only if s1 is a permutation of s2. s1 is a
     * permutation of s2 if it has the same number of each character as s2.
     */
    public static boolean isPermutation(String s1, String s2) {
        char[] chars1 = s1.toCharArray();
        char[] chars2 = s2.toCharArray();
        List<Character> lst1 = new ArrayList<>();
        for (int i = 0; i <= chars1.length - 1; i++) {
            lst1.add(i, chars1[i]);
        }
        List<Character> lst2 = new ArrayList<>();
        for (int i = 0; i <= chars2.length - 1; i++) {
            lst2.add(i, chars2[i]);
        }

        for (int i = 0; i <= chars1.length - 1; i++) {
            if (lst2.contains(lst1.get(i)) && chars1.length == chars2.length) {
                lst2.remove(lst1.get(i));
            } else {
                return false;
            }
        }
        return true;
    }
}
