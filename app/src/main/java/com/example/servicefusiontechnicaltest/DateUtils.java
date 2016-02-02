package com.example.servicefusiontechnicaltest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by Chad Schultz on 1/31/2016.
 */
public class DateUtils {
    private DateUtils() {}

    private static DateFormat mDateFormat;

    public static DateFormat getDateFormat() {
        if (mDateFormat == null) {
            mDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        }
        return mDateFormat;
    }
}
