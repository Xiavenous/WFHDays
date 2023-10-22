package com.example.wfhdays;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatDialogFragment;

public class ConfigDialog extends AppCompatDialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        builder.setView(view)
                .setTitle("Config")
                .setNegativeButton("Cancel",
                        (dialog, which) -> System.out.println("'Cancel' clicked!"))
                .setPositiveButton("Ok",
                        (dialog, which) -> System.out.println("'Ok' clicked!"));

        return builder.create();
    }
}
