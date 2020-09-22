package com.example.patrinavarro.smartsafe;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

/**
 * Created by Patri Navarro on 14/05/2018.
 */

public class ConfirmationDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.confirmation_dialog)
                .setPositiveButton(R.string.yes_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Load next screen like sign in:
                        new LoginActivity().load_data();
                    }
                })
                .setNegativeButton(R.string.no_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        ConfirmationDialogFragment.this.getDialog().cancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
