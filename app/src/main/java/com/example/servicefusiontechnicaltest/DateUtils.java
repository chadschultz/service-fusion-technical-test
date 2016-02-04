package com.example.servicefusiontechnicaltest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Reusable methods collected here instead of copied and pasted throughout the app
 * Created by Chad Schultz on 1/31/2016.
 */
public class DateUtils {
    private DateUtils() {}

    private static DateFormat mDateFormat;

    /**
     * Code throughout the app can rely on this one SimpleDateFormat. If the format changes,
     * it only needs to change here (and in any saved Person objects, of course).
     * @return DateFormat object
     */
    public static DateFormat getDateFormat() {
        if (mDateFormat == null) {
            mDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        }
        return mDateFormat;
    }
}
