package com.jasify.schedule.appengine.besr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.jasify.schedule.appengine.besr.CheckDigit.onlyDigits;

/**
 * The structure of code line has two recipes
 * <p/>
 * <b>With amount</b>
 * <p/>
 * <pre>
 *     CCAAAAAAAAAAA>RRRRRRRRRRRRRRRRRRRRRRRRRRR+ SSSSSSSSS>
 * </pre>
 * <ul>
 * <li>C - Slipe Type</li>
 * <li>A - Amount (last is check digit)</li>
 * <li>R - Reference number (last is check digit)</li>
 * <li>S - Subscriber number (last is check digit)</li>
 * </ul>
 * Examples:
 * <pre>
 *     0100003949753>120000000000234478943216899+ 010001628>
 *     2100000440001>961116900000006600000009284+ 030001625>
 * </pre>
 * <b>No amount</b>
 * <p/>
 * <pre>
 *     CCC>RRRRRRRRRRRRRRRRRRRRRRRRRRR+ SSSSSSSSS>
 * </pre>
 * <ul>
 * <li>C - Slipe Type (last is check digit)</li>
 * <li>R - Reference number (last is check digit)</li>
 * <li>S - Subscriber number (last is check digit)</li>
 * </ul>
 * Examples:
 * <pre>
 *     042>120000000000234478943216899+ 010001628>
 *     319>961116900000006600000009284+ 030001625>
 * </pre>
 *
 * @author krico
 * @since 21/07/15.
 */
public class CodeLine {
    public static final int AMOUNT_LENGTH = 11;
    public static final int REFERENCE_LENGTH = 27;
    public static final int SUBSCRIBER_LENGTH = 9;
    private static final Logger log = LoggerFactory.getLogger(CodeLine.class);
    private final SlipTypeEnum slipType;
    private final String amount;
    private final String reference;
    private final String subscriber;

    /**
     * All fields should be supplied <b>WITHOUT check digit</b> and will be left padded by zeros if required
     *
     * @param slipType   the type
     * @param amount     the amount
     * @param reference  the reference number
     * @param subscriber the subscirber number
     */
    CodeLine(SlipTypeEnum slipType, String amount, String reference, String subscriber) {
        this.slipType = slipType;
        this.amount = amount;
        this.reference = reference;
        this.subscriber = subscriber;
    }

    public static boolean isValid(String code) {
        //TODO: this is actually the reference number
        if (code == null || code.trim().isEmpty()) {
            log.trace("Invalid: EMPTY");
            return false;
        }
        String digits = code.replaceAll("[^0-9]", "");
        if (digits.length() != 27) {
            log.trace("Invalid: LENGTH={} '{}'", digits.length(), digits);
            return false;
        }
        return CheckDigit.isValid(digits);
    }

    public static String leftPad(String value, int len) {
        while (value.length() < len) {
            value = "0" + value;
        }
        return value;
    }

    public String toCodeLine() throws IllegalCodeLineException {
        String C = slipType.getCode();
        String A;
        if (slipType.isWithAmount()) { // CCAAAAAAAAAAA>RRRRRRRRRRRRRRRRRRRRRRRRRRR+ SSSSSSSSS>
            if (amount == null) {
                throw new IllegalCodeLineException("Amount is required but is NULL (slipType=" + slipType + ")");
            }
            A = leftPad(onlyDigits(amount), AMOUNT_LENGTH - 1);
            if (A.length() >= AMOUNT_LENGTH) {
                throw new IllegalCodeLineException("Amount length exceeds " + (AMOUNT_LENGTH - 1) + " [" + A + "]");
            }
            if (A.matches("^0+$")) {
                throw new IllegalCodeLineException("Amount is required but is ZERO (slipType=" + slipType + ", amount=" + amount + ")");
            }
        } else { // CCC>RRRRRRRRRRRRRRRRRRRRRRRRRRR+ SSSSSSSSS>
            A = "";
        }

        String R = leftPad(onlyDigits(reference), REFERENCE_LENGTH - 1);
        if (R.length() >= REFERENCE_LENGTH) {
            throw new IllegalCodeLineException("Reference length exceeds " + (REFERENCE_LENGTH - 1) + " [" + R + "]");
        }
        String S = leftPad(onlyDigits(subscriber), SUBSCRIBER_LENGTH - 1);
        if (S.length() >= SUBSCRIBER_LENGTH) {
            throw new IllegalCodeLineException("Subscriber length exceeds " + (SUBSCRIBER_LENGTH - 1) + " [" + S + "]");
        }


        return CheckDigit.complete(C + A) + '>'
                + CheckDigit.complete(R) + "+ "
                + CheckDigit.complete(S) + '>';
    }

    @Override
    public String toString() {
        return "CodeLine{" +
                "slipType=" + slipType +
                ", amount='" + amount + '\'' +
                ", reference='" + reference + '\'' +
                ", subscriber='" + subscriber + '\'' +
                '}';
    }
}
