package com.sugarcube.todo.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.sugarcube.todo.R;
import com.sugarcube.todo.activities.TodoEditActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Abed on 2/15/2017.
 */

public class DatePickerFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the current date as the default values for the picker
        super.onCreate(savedInstanceState);
        String date = getArguments().getString(TodoEditActivity.DUE_DATE_EXTRA);

        final Calendar c = Calendar.getInstance();

        if (date != null && date.isEmpty() == false) {
            SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.due_date_format));
            try {
                c.setTime(sdf.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Activity needs to implement this interface
        DatePickerDialog.OnDateSetListener listener = (DatePickerDialog.OnDateSetListener) getActivity();

        // Create a new instance of TimePickerDialog and return it
        return new DatePickerDialog(getActivity(), listener, year, month, day);
    }
}
