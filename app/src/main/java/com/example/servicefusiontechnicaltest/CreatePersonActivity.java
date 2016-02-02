package com.example.servicefusiontechnicaltest;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.text.ParseException;
import java.util.Calendar;

/**
 * Created by Chad Schultz on 1/31/2016.
 */
public class CreatePersonActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        AlertDialogFragment.AlertDialogFragmentListener {
    private final static String DIALOG_FRAGMENT_DATE_PICKER = "datePicker";
    public final static String DIALOG_FRAGMENT_DISCARD_CHANGES = "discardChanges";

    private TextInputLayout mFirstNameTextInput;
    private TextInputLayout mLastNameTextInput;
    private TextInputLayout mBirthdateTextInput;
    private TextInputLayout mZipCodeTextInput;

    private Firebase mFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_person);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFirstNameTextInput = (TextInputLayout) findViewById(R.id.first_name_text_input);
        mLastNameTextInput = (TextInputLayout) findViewById(R.id.last_name_text_input);
        mBirthdateTextInput = (TextInputLayout) findViewById(R.id.birthdate_text_input);
        // Open date picker dialog fragment when user tabs into birthdate field or
        // explicitly taps it
        //noinspection ConstantConditions
        mBirthdateTextInput.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DialogFragment newFragment = new DatePickerFragment();
                    newFragment.show(getFragmentManager(), DIALOG_FRAGMENT_DATE_PICKER);
                }
            }
        });
        mZipCodeTextInput = (TextInputLayout) findViewById(R.id.zip_code_text_input);

        //noinspection ConstantConditions
        mZipCodeTextInput.getEditText().setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    trySave();
                    return true;
                }
                return false;
            }
        });

        mFirebase = new Firebase(getString(R.string.firebase_url));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_person, menu);

        // From http://stackoverflow.com/questions/26780046/menuitem-tinting-on-appcompat-toolbar
        // Tint
        for (int i = 0; i < menu.size(); i++) {
            final MenuItem menuItem = menu.getItem(i);
            Drawable drawable = menuItem.getIcon();
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.colorAppBarIcon));
            menuItem.setIcon(drawable);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                trySave();

        }
        return false;
    }

    private void trySave() {
        if (!isInputValid()) {
            return;
        }

        //noinspection ConstantConditions
        Person person = new Person(mFirstNameTextInput.getEditText().getText().toString(),
                mLastNameTextInput.getEditText().getText().toString(),
                mBirthdateTextInput.getEditText().getText().toString(),
                mZipCodeTextInput.getEditText().getText().toString());

        // Save to database
        mFirebase.push().setValue(person, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Toast.makeText(CreatePersonActivity.this, "Person could not be saved." + firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        Toast.makeText(this, "Saving new person...", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void clearErrors() {
        mFirstNameTextInput.setError(null);
        mLastNameTextInput.setError(null);
        mBirthdateTextInput.setError(null);
        mZipCodeTextInput.setError(null);
    }

    private boolean isInputValid() {
        clearErrors();

        boolean inputValid = true;

        // First name must be present
        if (isTextInputEmpty(mFirstNameTextInput)) {
            inputValid = false;
            mFirstNameTextInput.setError(getString(R.string.error_missing_first_name));
        }

        // Last name must be present
        if (isTextInputEmpty(mLastNameTextInput)) {
            inputValid = false;
            mLastNameTextInput.setError(getString(R.string.error_missing_last_name));
        }

        // Birth date must be present
        if (isTextInputEmpty(mBirthdateTextInput)) {
            inputValid = false;
            mBirthdateTextInput.setError(getString(R.string.error_missing_birthdate));
        }
        // Birth date must be valid
        //noinspection ConstantConditions
        String dateString = mBirthdateTextInput.getEditText().getText().toString();
        try {
            DateUtils.getDateFormat().parse(dateString);
        } catch (ParseException pe) {
            inputValid = false;
            mBirthdateTextInput.setError(getString(R.string.error_invalid_birthdate));
        }

        // Zip code must be present
        if (isTextInputEmpty(mZipCodeTextInput)) {
            inputValid = false;
            mZipCodeTextInput.setError(getString(R.string.error_missing_zip_code));
        }
        // Zip code must be five digits (field should ensure only digits can be typed)
        //noinspection ConstantConditions
        if (mZipCodeTextInput.getEditText().getText().toString().length() < 5) {
            inputValid = false;
            mZipCodeTextInput.setError(getString(R.string.error_invalid_zip_code));
        }

        return inputValid;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        //noinspection ConstantConditions
        mBirthdateTextInput.getEditText().setText(DateUtils.getDateFormat().format(cal.getTime()));
    }

    public static class DatePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker

            // TODO: get current value from field, if set
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
        }
    }

    private boolean isTextInputEmpty(TextInputLayout textInput) {
        //noinspection ConstantConditions
        return TextUtils.isEmpty(textInput.getEditText().getText().toString());
    }

    private boolean isWorkInProgress() {
        return (!isTextInputEmpty(mFirstNameTextInput)
            || !isTextInputEmpty(mLastNameTextInput)
            || !isTextInputEmpty(mBirthdateTextInput)
            || !isTextInputEmpty(mZipCodeTextInput));
    }

    @Override
    public void onBackPressed() {
        if (isWorkInProgress()) {
            AlertDialogFragment dialogFragment = AlertDialogFragment.newInstance(null,
                    getString(R.string.dialog_message_discard_changes),
                    getString(R.string.dialog_button_discard),
                    getString(R.string.dialog_button_cancel));
            dialogFragment.show(getFragmentManager(), DIALOG_FRAGMENT_DISCARD_CHANGES);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPositiveDialogButtonClick(String tag) {
        if (tag.equals(DIALOG_FRAGMENT_DISCARD_CHANGES)) {
            finish();
        }
    }

    @Override
    public void onNegativeDialogButtonClick(String tag) {
        // Just close dialog - no special action needed in this use case
    }
}
