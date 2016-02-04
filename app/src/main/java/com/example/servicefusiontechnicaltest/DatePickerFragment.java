package com.example.servicefusiontechnicaltest;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.TextUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Manages a DatePickerDialog, which can default to the current date or be initialized to a set date.
 *
 * Created by Chad Schultz on 2/3/2016.
 */
public class DatePickerFragment extends DialogFragment {
    private final static String ARG_DATE = "dateString";

    public static DatePickerFragment newInstance() {
        return newInstance(null);
    }

    /**
     * Instantiate a new DatePickerFragment
     *
     * @param dateString A date in the format used in DateUtils. May be null or empty
     * @return DatePickerFragment
     */
    public static DatePickerFragment newInstance(String dateString) {
        DatePickerFragment fragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATE, dateString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        // If a date was provided and we can parse it, use that date instead
        String dateString = getArguments().getString(ARG_DATE);
        if (!TextUtils.isEmpty(dateString)) {
            try {
                Date date = DateUtils.getDateFormat().parse(dateString);
                c.setTime(date);
            } catch (ParseException pe) {
                // Do nothing - if the user typed a bad date, it's no concern
                // We're showing the dialog so they can set a correct date
            }
        }
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(),
                (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
    }
}
