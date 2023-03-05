package com.example.wfhdays;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String IS_WEEKEND =  "is on a weekend.";
    private static final String IS_WFH = "is a WFH day.";
    private static final String NOT_WFH = "isn't a WFH day.";

    private EditText dayText, monthText, yearText;
    private TextView output;

    private TimeBoxWeekIterator timeBoxWeekIterator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dayText = findViewById(R.id.editTextTextPersonName1);
        dayText.setText(String.valueOf(LocalDate.now().getDayOfMonth()));
        monthText = findViewById(R.id.editTextTextPersonName2);
        monthText.setText(String.valueOf(LocalDate.now().getMonthValue()));
        yearText = findViewById(R.id.editTextTextPersonName3);
        yearText.setText(String.valueOf(LocalDate.now().getYear()));
        Button button = findViewById(R.id.button2);
        output = findViewById(R.id.textView);
        timeBoxWeekIterator = new TimeBoxWeekIterator(LocalDate.of(2023,1,9), 0, 1);
        timeBoxWeekIterator.addWeekWfhDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY));
        timeBoxWeekIterator.addWeekWfhDays(Set.of(DayOfWeek.FRIDAY, DayOfWeek.THURSDAY));

        button.setOnClickListener(this::onClick);
    }

    private void onClick(View view) {
        LocalDate selectedDate = LocalDate.of(
                Integer.parseInt(yearText.getText().toString()),
                Integer.parseInt(monthText.getText().toString()),
                Integer.parseInt(dayText.getText().toString()));

        output.setText(
                String.format("%s %s", selectedDate.toString(), resolveResponse(selectedDate)));
    }

    private String resolveResponse(LocalDate input) {
        if (Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(input.getDayOfWeek())) {
            return IS_WEEKEND;
        } else {
            return timeBoxWeekIterator.isWfhDay(input) ? IS_WFH : NOT_WFH;
        }
    }
}