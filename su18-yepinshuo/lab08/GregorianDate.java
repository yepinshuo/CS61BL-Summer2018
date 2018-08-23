public class GregorianDate extends Date {

    private static final int[] MONTH_LENGTHS = {
        31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
    };

    public GregorianDate(int year, int month, int dayOfMonth) {
        super(year, month, dayOfMonth);
    }

    @Override
    public int dayOfYear() {
        int precedingMonthDays = 0;
        for (int m = 1; m < month; m += 1) {
            precedingMonthDays += getMonthLength(m);
        }
        return precedingMonthDays + dayOfMonth;
    }

    @Override
    public Date nextDate() {
        if (dayOfMonth < MONTH_LENGTHS[month - 1]) {
            return new GregorianDate(year, month, dayOfMonth + 1);
        } else {
            if (month < 12) {
                return new GregorianDate(year, month + 1, 1);
            } else {
                return new GregorianDate(year + 1, 1, 1);
            }
        }
    }

    private static int getMonthLength(int m) {
        return MONTH_LENGTHS[m - 1];
    }
}
