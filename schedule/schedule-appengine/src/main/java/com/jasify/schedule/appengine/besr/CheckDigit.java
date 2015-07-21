package com.jasify.schedule.appengine.besr;

/**
 * @author krico
 * @since 22/07/15.
 */
public final class CheckDigit {
    private static final int[][] NUMBER_SERIES = {
            {0, 9, 4, 6, 8, 2, 7, 1, 3, 5},
            {9, 4, 6, 8, 2, 7, 1, 3, 5, 0},
            {4, 6, 8, 2, 7, 1, 3, 5, 0, 9},
            {6, 8, 2, 7, 1, 3, 5, 0, 9, 4},
            {8, 2, 7, 1, 3, 5, 0, 9, 4, 6},
            {2, 7, 1, 3, 5, 0, 9, 4, 6, 8},
            {7, 1, 3, 5, 0, 9, 4, 6, 8, 2},
            {1, 3, 5, 0, 9, 4, 6, 8, 2, 7},
            {3, 5, 0, 9, 4, 6, 8, 2, 7, 1},
            {5, 0, 9, 4, 6, 8, 2, 7, 1, 3}
    };

    private CheckDigit() {
    }

    /**
     * Checks if <code>number</code> is valid and has a correct check digit
     *
     * @param number a series of digits
     * @return true if is valid and the check digit is corrects
     */
    public static boolean isValid(String number) {
        String digits = number.replaceAll("[^0-9]", "");
        if (digits.length() < 2) return false;
        String toComplete = digits.substring(0, digits.length() - 1);
        return complete(toComplete).equals(digits);
    }

    /**
     * Completes <code>number</code> with the appropriate check digit
     *
     * @param number for which we will calculate the check digit
     * @return the number with the check digit appended
     */
    public static String complete(String number) {
        int over = 0;
        char[] chars = number.toCharArray();
        for (char c : chars) {
            if (!Character.isDigit(c)) continue; // Only care about digits
            int digit = Character.getNumericValue(c);
            over = NUMBER_SERIES[over][digit];
        }
        return number + ((10 - over) % 10);
    }
}
