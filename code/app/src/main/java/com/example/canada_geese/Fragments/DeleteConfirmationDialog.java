package com.example.canada_geese.Fragments;


import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.example.canada_geese.R;

public class DeleteConfirmationDialog extends DialogFragment {
    private final Runnable onDeleteConfirmed;

    public DeleteConfirmationDialog(Runnable onDeleteConfirmed) {
        this.onDeleteConfirmed = onDeleteConfirmed;
    }

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
