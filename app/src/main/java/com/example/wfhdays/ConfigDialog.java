package com.example.wfhdays;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ConfigDialog extends AppCompatDialogFragment {

    private List<Switch> week1;
    private List<Switch> week2;
    private ConfigDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);
        instantiateElements(view);

        builder.setView(view)
                .setTitle("Config")
                .setNegativeButton("Cancel",
                        (dialog, which) -> System.out.println("'Cancel' clicked!"))
                .setPositiveButton("Ok",
                        (dialog, which) -> {
                            System.out.println("'Ok' clicked!");
                            writeToFile();
                            List<Boolean> var1 = week1.stream()
                                    .map(CompoundButton::isChecked).collect(Collectors.toList());
                            List<Boolean> var2 = week2.stream()
                                    .map(CompoundButton::isChecked).collect(Collectors.toList());
                            listener.applySelectedConfig(var1, var2);
                        });


        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ConfigDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + "must implement DialogConfigListener");
        }
    }

    /**
     * Method could be used to initialise the checkboxes to the current config (or default)
     * */
    private String readFromFile() {
        File path = requireContext().getFilesDir();
        File readFrom = new File(path, "config.txt");
        byte[] content = new byte[(int) readFrom.length()];
        try (FileInputStream stream = new FileInputStream(readFrom)) {
            stream.read(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new String(content);
    }

    private void writeToFile() {
        StringBuilder sb = new StringBuilder();
        week1.forEach(s -> sb.append(s.isChecked() ? "T" : "F"));
        sb.append("\n");
        week2.forEach(s -> sb.append(s.isChecked() ? "T" : "F"));
        String content = sb.toString();
        File path = requireContext().getFilesDir();
        try {
            FileOutputStream writer = new FileOutputStream(new File(path, "config.txt"));
            writer.write(content.getBytes());
            writer.close();
            Toast.makeText(requireContext(), "Wrote to file", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void instantiateElements(View view) {
        week1 = new ArrayList<>();
        week1.add(view.findViewById(R.id.switch1));
        week1.add(view.findViewById(R.id.switch2));
        week1.add(view.findViewById(R.id.switch3));
        week1.add(view.findViewById(R.id.switch4));
        week1.add(view.findViewById(R.id.switch5));

        week2 = new ArrayList<>();
        week2.add(view.findViewById(R.id.switch6));
        week2.add(view.findViewById(R.id.switch7));
        week2.add(view.findViewById(R.id.switch8));
        week2.add(view.findViewById(R.id.switch9));
        week2.add(view.findViewById(R.id.switch10));
    }

    public interface ConfigDialogListener {
        void applySelectedConfig(List<Boolean> week1, List<Boolean> week2);
    }
}
