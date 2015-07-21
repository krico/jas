package com.jasify.schedule.appengine.besr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author krico
 * @since 21/07/15.
 */
public class CodeLine {
    private static final Logger log = LoggerFactory.getLogger(CodeLine.class);

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
}
