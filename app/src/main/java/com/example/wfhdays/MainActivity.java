package com.example.wfhdays;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final Set<DayOfWeek> WEEKEND = Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    private static final String IS_WEEKEND =  "is on a weekend.";
    private static final String IS_WFH = "is a WFH day.";
    private static final String NOT_WFH = "isn't a WFH day.";

    private EditText dayText, monthText, yearText;
    private List<View> prevWeekView;
    private List<View> nextWeekView;
    private List<View> selectedWeekView;
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
        output = findViewById(R.id.textView);
        selectedWeekView = new ArrayList<>();
        selectedWeekView.add(findViewById(R.id.selectedWeekDay1));
        selectedWeekView.add(findViewById(R.id.selectedWeekDay2));
        selectedWeekView.add(findViewById(R.id.selectedWeekDay3));
        selectedWeekView.add(findViewById(R.id.selectedWeekDay4));
        selectedWeekView.add(findViewById(R.id.selectedWeekDay5));
        selectedWeekView.add(findViewById(R.id.selectedWeekDay6));
        selectedWeekView.add(findViewById(R.id.selectedWeekDay7));
        prevWeekView = new ArrayList<>();
        prevWeekView.add(findViewById(R.id.prevWeekDay1));
        prevWeekView.add(findViewById(R.id.prevWeekDay2));
        prevWeekView.add(findViewById(R.id.prevWeekDay3));
        prevWeekView.add(findViewById(R.id.prevWeekDay4));
        prevWeekView.add(findViewById(R.id.prevWeekDay5));
        prevWeekView.add(findViewById(R.id.prevWeekDay6));
        prevWeekView.add(findViewById(R.id.prevWeekDay7));
        nextWeekView = new ArrayList<>();
        nextWeekView.add(findViewById(R.id.nextWeekDay1));
        nextWeekView.add(findViewById(R.id.nextWeekDay2));
        nextWeekView.add(findViewById(R.id.nextWeekDay3));
        nextWeekView.add(findViewById(R.id.nextWeekDay4));
        nextWeekView.add(findViewById(R.id.nextWeekDay5));
        nextWeekView.add(findViewById(R.id.nextWeekDay6));
        nextWeekView.add(findViewById(R.id.nextWeekDay7));
        setupTimeboxIterator();

        Button button = findViewById(R.id.button);
        button.setOnClickListener(this::onClick);
    }

    private void setupTimeboxIterator() {
        timeBoxWeekIterator = new TimeBoxWeekIterator(LocalDate.of(2023,1,9),
                0, 1);
        timeBoxWeekIterator.addWeekWfhDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY));
        timeBoxWeekIterator.addWeekWfhDays(Set.of(DayOfWeek.FRIDAY, DayOfWeek.THURSDAY));
    }

    private void onClick(View view) {
        LocalDate selectedDate = LocalDate.of(
                Integer.parseInt(yearText.getText().toString()),
                Integer.parseInt(monthText.getText().toString()),
                Integer.parseInt(dayText.getText().toString()));

        setVisualAid(selectedDate);
        output.setText(String.format("%s %s", selectedDate, resolveResponse(selectedDate)));
    }

    private void setVisualAid(LocalDate selectedDate) {
        LocalDate thatMonday = selectedDate.minusDays(selectedDate.getDayOfWeek().getValue() - 1);
        LocalDate prevMonday = thatMonday.minusWeeks(1);
        LocalDate nextMonday = thatMonday.plusWeeks(1);

        setOtherWeek(prevMonday, prevWeekView);
        setSelectedWeek(selectedDate, thatMonday, selectedWeekView);
        setOtherWeek(nextMonday, nextWeekView);
    }

    private void setSelectedWeek(LocalDate selectedDate, LocalDate thatMonday, List<View> selectedWeek) {
        for (int i = 0; i < 7; i++) {
            if (thatMonday.plusDays(i).getDayOfWeek() == selectedDate.getDayOfWeek()) {
                selectedWeek.get(i).setBackgroundColor(Color.GREEN);
            } else if (timeBoxWeekIterator.isWfhDay(thatMonday.plusDays(i))) {
                selectedWeek.get(i).setBackgroundColor(Color.parseColor("#2196F3"));
            } else if (WEEKEND.contains(thatMonday.plusDays(i).getDayOfWeek())) {
                selectedWeek.get(i).setBackgroundColor(Color.parseColor("#FF808080"));
            } else {
                selectedWeek.get(i).setBackgroundColor(Color.parseColor("#FFEB3B"));
            }
        }
    }

    private void setOtherWeek(LocalDate prevMonday, List<View> prevWeekView) {
        for (int i = 0; i < 7; i++) {
            if (timeBoxWeekIterator.isWfhDay(prevMonday.plusDays(i))) {
                prevWeekView.get(i).setBackgroundColor(Color.parseColor("#2196F3"));
            } else if (WEEKEND.contains(prevMonday.plusDays(i).getDayOfWeek())) {
                prevWeekView.get(i).setBackgroundColor(Color.parseColor("#FF808080"));
            } else {
                prevWeekView.get(i).setBackgroundColor(Color.parseColor("#FFEB3B"));
            }
        }
    }

    private String resolveResponse(LocalDate input) {
        if (Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(input.getDayOfWeek())) {
            return IS_WEEKEND;
        } else {
            return timeBoxWeekIterator.isWfhDay(input) ? IS_WFH : NOT_WFH;
        }
    }
}