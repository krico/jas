package com.jasify.schedule.appengine.util;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

/**
 * @author krico
 * @since 14/02/15.
 */
public final class CurrencyUtil {
    private CurrencyUtil() {
    }

    public static String formatCurrencyNumber(String currency, Double amount) {
        currency = Preconditions.checkNotNull(StringUtils.trimToNull(currency));
        Preconditions.checkNotNull(amount);
        return String.format(Locale.ROOT, "%.2f", amount);
    }
}
