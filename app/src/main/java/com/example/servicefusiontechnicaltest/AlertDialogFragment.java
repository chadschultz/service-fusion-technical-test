package com.example.servicefusiontechnicaltest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * NOTE: this relies on the calling Activity being the listener. Additional coding is necessary
 * for it to work with Fragments as well.
 *
 * Created by Chad Schultz on 1/31/2016.
 */
public class AlertDialogFragment extends DialogFragment {
    private final static String ARG_TITLE = "title";
    private final static String ARG_MESSAGE = "message";
    private final static String ARG_POSITIVE_BUTTON_LABEL = "positiveButtonLabel";
    private final static String ARG_NEGATIVE_BUTTON_LABEL = "negativeButtonLabel";

    private AlertDialogFragmentListener mListener;

    // Any more complex, and a builder pattern will be needed instead of overloaded constructors
    public static AlertDialogFragment newInstance(String title, String message) {
        return newInstance(title, message, null, null);
    }

    public static AlertDialogFragment newInstance(String title, String message,
                                                  String positiveButtonLabel) {
        return newInstance(title, message, positiveButtonLabel, null);
    }

    public static AlertDialogFragment newInstance(String title, String message,
                                                  String positiveButtonLabel,
                                                  String negativeButtonLabel) {
        AlertDialogFragment fragment = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_POSITIVE_BUTTON_LABEL, positiveButtonLabel);
        args.putString(ARG_NEGATIVE_BUTTON_LABEL, negativeButtonLabel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Title is optional. In fact, the design guidelines suggest "Most alerts don't need titles"
        String title = getArguments().getString(ARG_TITLE);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }

        // Message is optional. Every dialog should have at least a message, if not a title,
        // but just in case there's the desire to do it the other way around
        String message = getArguments().getString(ARG_MESSAGE);
        if (!TextUtils.isEmpty(message)) {
            builder.setMessage(message);
        }

        // If no labels are provided, we'll assume there's a positive button, with a default label
        String positiveButtonLabel = getArguments().getString(ARG_POSITIVE_BUTTON_LABEL);
        if (TextUtils.isEmpty(positiveButtonLabel)) {
            positiveButtonLabel = getString(android.R.string.ok);
        }
        builder.setPositiveButton(positiveButtonLabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onPositiveDialogButtonClick(getTag());
            }
        });

        // If no negative button label is provided, we'll assume it's a one-button dialog
        String negativeButtonLabel = getArguments().getString(ARG_NEGATIVE_BUTTON_LABEL);
        if (negativeButtonLabel != null) {
            if (TextUtils.isEmpty(negativeButtonLabel)) {
                // Negative button label passed as an empty string? Use system default
                negativeButtonLabel = getString(android.R.string.cancel);
            }
            builder.setNegativeButton(negativeButtonLabel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mListener.onNegativeDialogButtonClick(getTag());
                }
            });
        }

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (AlertDialogFragmentListener) activity;
    }

    /**
     * Pass along the fragment tag (set when fragment is shown) so the listener activity can
     * tell which AlertDialogFragment had a button click.
     */
    public interface AlertDialogFragmentListener {
        void onPositiveDialogButtonClick(String tag);

        void onNegativeDialogButtonClick(String tag);

        // Could extend for neutral, three-button dialogs, but it's unnecessary complexity here
    }
}
