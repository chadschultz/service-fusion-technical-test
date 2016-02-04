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
import java.util.Date;

/**
 * This screen can either be used to enter and save data for new Person objects, or to view, update
 * and delete data for existing Person objects.
 *
 * Created by Chad Schultz on 1/31/2016.
 */
public class CreateEditPersonActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        AlertDialogFragment.AlertDialogFragmentListener {
    public final static String EXTRA_PERSON = "com.example.servicefusiontechnicaltest.EXTRA_PERSON";

    private final static String DIALOG_FRAGMENT_DATE_PICKER = "datePicker";
    private final static String DIALOG_FRAGMENT_DISCARD_CHANGES = "discardChanges";
    private final static String DIALOG_FRAGMENT_DELETE = "delete";

    private TextInputLayout mFirstNameTextInput;
    private EditText mFirstNameEditText;
    private TextInputLayout mLastNameTextInput;
    private EditText mLastNameEditText;
    private TextInputLayout mBirthDateTextInput;
    private EditText mBirthDateEditText;
    private TextInputLayout mZipCodeTextInput;
    private EditText mZipCodeEditText;

    private Person mPerson;
    private Firebase mFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_person);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirstNameTextInput = (TextInputLayout) findViewById(R.id.first_name_text_input);
        mFirstNameEditText = (EditText) findViewById(R.id.first_name_edit_text);
        mLastNameTextInput = (TextInputLayout) findViewById(R.id.last_name_text_input);
        mLastNameEditText = (EditText) findViewById(R.id.last_name_edit_text);
        mBirthDateTextInput = (TextInputLayout) findViewById(R.id.birthdate_text_input);
        // Open date picker dialog fragment when user tabs into birthdate field or
        // explicitly taps it
        mBirthDateEditText = (EditText) findViewById(R.id.birthdate_edit_text);
        mBirthDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });
        mBirthDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    openDatePicker();
                }
            }
        });
        mZipCodeTextInput = (TextInputLayout) findViewById(R.id.zip_code_text_input);
        mZipCodeEditText = (EditText) findViewById(R.id.zip_code_edit_text);
        mZipCodeEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {

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

        mPerson = (Person) getIntent().getSerializableExtra(EXTRA_PERSON);
        if (mPerson == null) {
            // Create new person
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(getString(R.string.page_title_create_person));
        } else {
            // Edit existing person
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(getString(R.string.page_title_edit_person));
            mFirstNameEditText.setText(mPerson.getFirstName());
            mLastNameEditText.setText(mPerson.getLastName());
            mBirthDateEditText.setText(mPerson.getBirthDate());
            mZipCodeEditText.setText(mPerson.getZipCode());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_edit_person, menu);

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
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mPerson == null) {
            // We are creating, not editing, so there is no existing entry to delete
            menu.findItem(R.id.action_delete).setVisible(false);
        }
        super.onPrepareOptionsMenu(menu);
        return true; //display the menu
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // "Up" button at left end of app bar has been pressed
                if (!isOkToGoBack()) {
                    // Consume event--do not pass through to normal "Up" button functionality
                    return true;
                }
                break;
            case R.id.action_delete:
                promptToDelete();
                break;
            case R.id.action_done:
                trySave();
                break;

        }
        return false;
    }

    private void promptToDelete() {
        AlertDialogFragment dialogFragment = AlertDialogFragment.newInstance(null,
                getString(R.string.dialog_message_delete),
                getString(R.string.dialog_button_delete),
                getString(R.string.dialog_button_cancel));
        dialogFragment.show(getFragmentManager(), DIALOG_FRAGMENT_DELETE);
    }

    private void delete() {
        mFirebase.child(mPerson.getId()).removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Toast.makeText(CreateEditPersonActivity.this, getString(R.string.toast_delete_error) + firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        Toast.makeText(this, getString(R.string.toast_deleting_person), Toast.LENGTH_SHORT).show();
        finish();
    }

    private void trySave() {
        if (!isInputValid()) {
            return;
        }

        // Create an object to save to the online database from the input in the fields
        Person person = new Person(mFirstNameEditText.getText().toString(),
                mLastNameEditText.getText().toString(),
                mBirthDateEditText.getText().toString(),
                mZipCodeEditText.getText().toString());

        // Save to database
        if (mPerson == null) {
            // Save a new object. Use push() to create a new key
            mFirebase.push().setValue(person, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError != null) {
                        Toast.makeText(CreateEditPersonActivity.this, getString(R.string.toast_save_error) + firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            Toast.makeText(this, getString(R.string.toast_saving_person), Toast.LENGTH_SHORT).show();
        } else {
            // Update an existing object. We can use setValue() with an existing ID - it just
            // replaces all children (properties) with the ones we provide here.
            if (isWorkInProgress()) {
                // Only ping the online database if there are actually changes to make. Otherwise
                // we will return to the previous screen as if saving, but not actually updating
                // anything.
                mFirebase.child(mPerson.getId()).setValue(person, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError != null) {
                            Toast.makeText(CreateEditPersonActivity.this, getString(R.string.toast_update_error) + firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Toast.makeText(this, getString(R.string.toast_updating_person), Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    private void openDatePicker() {
        DialogFragment newFragment = DatePickerFragment.newInstance(mBirthDateEditText.getText().toString());
        newFragment.show(getFragmentManager(), DIALOG_FRAGMENT_DATE_PICKER);
    }

    private void clearErrors() {
        mFirstNameTextInput.setError(null);
        mLastNameTextInput.setError(null);
        mBirthDateTextInput.setError(null);
        mZipCodeTextInput.setError(null);
    }

    private boolean isInputValid() {
        clearErrors();

        boolean inputValid = true;

        // First name must be present
        if (isEditTextEmpty(mFirstNameEditText)) {
            inputValid = false;
            mFirstNameTextInput.setError(getString(R.string.error_missing_first_name));
        }

        // Last name must be present
        if (isEditTextEmpty(mLastNameEditText)) {
            inputValid = false;
            mLastNameTextInput.setError(getString(R.string.error_missing_last_name));
        }

        // Birth date must be present
        if (isEditTextEmpty(mBirthDateEditText)) {
            inputValid = false;
            mBirthDateTextInput.setError(getString(R.string.error_missing_birthdate));
        }
        // Birth date must be valid
        //noinspection ConstantConditions
        String dateString = mBirthDateTextInput.getEditText().getText().toString();
        try {
            DateUtils.getDateFormat().parse(dateString);
        } catch (ParseException pe) {
            inputValid = false;
            mBirthDateTextInput.setError(getString(R.string.error_invalid_birthdate));
        }

        // Zip code must be present
        if (isEditTextEmpty(mZipCodeEditText)) {
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
        mBirthDateTextInput.getEditText().setText(DateUtils.getDateFormat().format(cal.getTime()));
    }

    private boolean isEditTextEmpty(EditText editText) {
        //noinspection ConstantConditions
        return TextUtils.isEmpty(editText.getText().toString());
    }

    private boolean editTextMatches(EditText editText, String match) {
        return editText.getText().toString().equalsIgnoreCase(match);
    }

    private boolean isWorkInProgress() {
        if (mPerson == null) {
            // Creating - has any data been entered?
            return (!isEditTextEmpty(mFirstNameEditText)
                    || !isEditTextEmpty(mLastNameEditText)
                    || !isEditTextEmpty(mBirthDateEditText)
                    || !isEditTextEmpty(mZipCodeEditText));
        } else {
            // Editing - has any data been changed?
            return (!editTextMatches(mFirstNameEditText, mPerson.getFirstName())
                    || !editTextMatches(mLastNameEditText, mPerson.getLastName())
                    || !editTextMatches(mBirthDateEditText, mPerson.getBirthDate())
                    || !editTextMatches(mZipCodeEditText, mPerson.getZipCode()));
        }

    }

    /**
     * When the user pressed the Back button on their device or the Up button in the app bar,
     * check to see if we should warn the user that they started something and haven't saved.
     * @return true if there are no unsaved changed, false otherwise
     */
    public boolean isOkToGoBack() {
        if (isWorkInProgress()) {
            AlertDialogFragment dialogFragment = AlertDialogFragment.newInstance(null,
                    getString(R.string.dialog_message_discard_changes),
                    getString(R.string.dialog_button_discard),
                    getString(R.string.dialog_button_cancel));
            dialogFragment.show(getFragmentManager(), DIALOG_FRAGMENT_DISCARD_CHANGES);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if (isOkToGoBack()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onPositiveDialogButtonClick(String tag) {
        if (tag.equals(DIALOG_FRAGMENT_DISCARD_CHANGES)) {
            finish();
        } else if (tag.equals(DIALOG_FRAGMENT_DELETE)) {
            delete();
        }
    }

    @Override
    public void onNegativeDialogButtonClick(String tag) {
        // Just close dialog - no special action needed in this use case
    }
}
