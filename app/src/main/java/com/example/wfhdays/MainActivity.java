package com.example.wfhdays;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final LocalDate firstMondayOfIterator =
            LocalDate.of(2023,1,9);
    private static final Set<DayOfWeek> WEEKEND = Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    private static final String IS_WEEKEND =  "is on a weekend";
    private static final String IS_WFH = "is a WFH day";
    private static final String NOT_WFH = "is an office day";

    private EditText dayText, monthText, yearText;
    private List<TextView> prevWeekView;
    private List<TextView> nextWeekView;
    private List<TextView> selectedWeekView;
    private TextView output;
    private TextView beginMonth;
    private TextView endMonth;

    private TimeBoxWeekIterator timeBoxWeekIterator;
    private GradientDrawable border;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        border = new GradientDrawable();
        border.setShape(GradientDrawable.RECTANGLE);
        border.setStroke(6, Color.BLACK);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dayText = findViewById(R.id.editTextTextPersonName1);
        dayText.setText(String.valueOf(LocalDate.now().getDayOfMonth()));
        monthText = findViewById(R.id.editTextTextPersonName2);
        monthText.setText(String.valueOf(LocalDate.now().getMonthValue()));
        yearText = findViewById(R.id.editTextTextPersonName3);
        yearText.setText(String.valueOf(LocalDate.now().getYear()));
        output = findViewById(R.id.textView);
        beginMonth = findViewById(R.id.beginMonthTextView);
        endMonth = findViewById(R.id.endMonthTextView);
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
        onClick(null);
    }

    private void setupTimeboxIterator() {
        timeBoxWeekIterator =
                new TimeBoxWeekIterator(firstMondayOfIterator, 0, 1);
        timeBoxWeekIterator.addWeekWfhDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY));
        timeBoxWeekIterator.addWeekWfhDays(Set.of(DayOfWeek.FRIDAY, DayOfWeek.THURSDAY));
    }

    private void onClick(View view) {
        try {
            LocalDate selectedDate = LocalDate.of(
                    Integer.parseInt(yearText.getText().toString()),
                    Integer.parseInt(monthText.getText().toString()),
                    Integer.parseInt(dayText.getText().toString()));

            if (selectedDate.isBefore(firstMondayOfIterator) ||
                    selectedDate.isAfter(LocalDate.of(2023,12,31)))
                throw new Exception("too early");

            LocalDate mondayOfSelectedWeek = selectedDate
                    .minusDays(selectedDate.getDayOfWeek().getValue() - 1);
            LocalDate prevMonday = mondayOfSelectedWeek.minusWeeks(1);
            LocalDate nextMonday = mondayOfSelectedWeek.plusWeeks(1);

            updateVisualAid(selectedDate, mondayOfSelectedWeek, prevMonday, nextMonday);
            updateTextViews(selectedDate, prevMonday, nextMonday);
        } catch (Exception e) {
            errorScenario();
        }
    }

    private void errorScenario() {
        output.setText("INVALID INPUT!");
        beginMonth.setText("");
        endMonth.setText("");
        prevWeekView.forEach(v -> {
            v.setBackgroundColor(Color.WHITE);
            v.setText("");
        });
        selectedWeekView.forEach(v -> {
            v.setBackgroundColor(Color.WHITE);
            v.setText("");
        });
        nextWeekView.forEach(v -> {
            v.setBackgroundColor(Color.WHITE);
            v.setText("");
        });
    }

    private void updateTextViews(LocalDate selectedDate, LocalDate prevMonday, LocalDate nextMonday) {
        beginMonth.setText(prevMonday.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        endMonth.setText(resolveEndMonthText(prevMonday, nextMonday.plusDays(6)));
        output.setText(resolveOutputText(selectedDate));
    }

    private String resolveOutputText(LocalDate selectedDate) {
        return String.format(
                "%s of %s %s",
                dayWithPrefix(selectedDate.getDayOfMonth()),
                selectedDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                resolveResponse(selectedDate));
    }

    private String resolveEndMonthText(LocalDate firstDay, LocalDate lastDay) {
        return (firstDay.getMonth() != lastDay.getMonth())
                ? lastDay.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                : "";
    }

    private void updateVisualAid(LocalDate selectedDate, LocalDate mondayOfSelectedWeek,
                                 LocalDate prevMonday, LocalDate nextMonday) {
        setWeekView(prevMonday, prevWeekView);
        setWeekView(mondayOfSelectedWeek, selectedWeekView);
        setWeekView(nextMonday, nextWeekView);
        setMarkerOnSelectedDate(selectedDate, mondayOfSelectedWeek);
    }

    private void setMarkerOnSelectedDate(LocalDate selectedDate, LocalDate mondayOfSelectedWeek) {
        for (int i = 0; i < 7; i++) {
            if (mondayOfSelectedWeek.plusDays(i).getDayOfWeek() == selectedDate.getDayOfWeek()) {
                border.setColor(
                        ((ColorDrawable) selectedWeekView.get(i).getBackground()).getColor());
                selectedWeekView.get(i).setBackground(border);
            }
        }
    }

    private void setWeekView(LocalDate prevMonday, List<TextView> weekView) {
        for (int i = 0; i < 7; i++) {
            weekView.get(i).setText(String.valueOf(prevMonday.plusDays(i).getDayOfMonth()));
            if (timeBoxWeekIterator.isWfhDay(prevMonday.plusDays(i))) {
                weekView.get(i).setBackgroundColor(Color.parseColor("#2196F3"));
            } else if (WEEKEND.contains(prevMonday.plusDays(i).getDayOfWeek())) {
                weekView.get(i).setBackgroundColor(Color.parseColor("#FF808080"));
            } else {
                weekView.get(i).setBackgroundColor(Color.parseColor("#FFEB3B"));
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

    private String dayWithPrefix(int day) {
        String input = String.valueOf(day);
        if (input.endsWith("1")) return day + "st";
        else if (input.endsWith("2")) return day + "nd";
        else if (input.endsWith("3")) return day + "rd";

        return day + "th";
    }
}