package com.example.canada_geese.pageResources;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.example.canada_geese.R;

/**
 * A dialog fragment that displays a delete confirmation message.
 * Executes a specified action if the user confirms the deletion.
 */
public class DeleteConfirmationDialog extends DialogFragment {
    private final Runnable onDeleteConfirmed;

    /**
     * Constructs a DeleteConfirmationDialog with the specified action to run on deletion confirmation.
     *
     * @param onDeleteConfirmed a Runnable to execute if the user confirms the deletion.
     */
    public DeleteConfirmationDialog(Runnable onDeleteConfirmed) {
        this.onDeleteConfirmed = onDeleteConfirmed;
    }

    /**
     * Creates the delete confirmation dialog.
     *
     * @param savedInstanceState the saved instance state.
     * @return a Dialog displaying the delete confirmation message.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_confirmation_title)
                .setMessage(R.string.delete_confirmation_message)
                .setPositiveButton(R.string.delete_mood, (dialog, which) -> {
                    if (onDeleteConfirmed != null) {
                        onDeleteConfirmed.run();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }
}