package com.example.wfhdays;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements ConfigDialog.ConfigDialogListener {
    private static final LocalDate firstMondayOfIterator =
            LocalDate.of(2023,1,9);
    private static final Set<DayOfWeek> WEEKEND = Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    private static final List<Set<DayOfWeek>> DEFAULT_WFH_DAYS = List.of(
            Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY),
            Set.of(DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)
    );
    private static final String IS_WEEKEND =  "is on a weekend";
    private static final String IS_WFH = "is a WFH day";
    private static final String NOT_WFH = "is an office day";
    private static final String INVALID_INPUT = "INVALID INPUT!";

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
        monthText = findViewById(R.id.editTextTextPersonName2);
        yearText = findViewById(R.id.editTextTextPersonName3);
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
        setupInitialInputFieldValues(LocalDate.now());

        Button caclulateButton = findViewById(R.id.button);
        caclulateButton.setOnClickListener(this::onClickCalculate);

        Button configButton = findViewById(R.id.configButton);
        configButton.setOnClickListener(v -> onClickConfig());

        File path = getApplicationContext().getFilesDir();
        File readFrom = new File(path, "config.txt");
        boolean configSaved = readFrom.length() > 0;

        List<Set<DayOfWeek>> wfhDays = configSaved
                ? loadWfhDaysFromFile(readFrom)
                : DEFAULT_WFH_DAYS;

        setupTimeboxIterator(wfhDays);

        if (!configSaved) onClickConfig();

        onClickCalculate(null);
    }

    private static List<Set<DayOfWeek>> loadWfhDaysFromFile(File readFrom) {
        String value = readFileContents(readFrom);
        return convertFileContentsToWfhDays(value);
    }

    private static List<Set<DayOfWeek>> convertFileContentsToWfhDays(final String value) {
        return Arrays.stream(value.split("\n"))
                .map(MainActivity::deserializeWfhDays)
                .collect(Collectors.toList());
    }

    private static Set<DayOfWeek> deserializeWfhDays(String valueSubset) {
        return Arrays.stream(DayOfWeek.values())
                .filter(dow -> !WEEKEND.contains(dow))
                .filter(dow -> valueSubset.charAt(dow.getValue() - 1) == 'T')
                .collect(Collectors.toSet());
    }

    private static String readFileContents(File readFrom) {
        byte[] content = new byte[(int) readFrom.length()];
        try (FileInputStream stream = new FileInputStream(readFrom)) {
            stream.read(content);
            return new String(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupInitialInputFieldValues(LocalDate initialInputDate) {
        dayText.setText(String.valueOf(initialInputDate.getDayOfMonth()));
        monthText.setText(String.valueOf(initialInputDate.getMonthValue()));
        yearText.setText(String.valueOf(initialInputDate.getYear()));
    }

    private void setupTimeboxIterator(List<Set<DayOfWeek>> wfhDays) {
        timeBoxWeekIterator = new TimeBoxWeekIterator(firstMondayOfIterator);
        wfhDays.forEach(timeBoxWeekIterator::addWeekWfhDays);
    }

    private void onClickConfig() {
        ConfigDialog dialog = new ConfigDialog();
        dialog.show(getSupportFragmentManager(), "testing dialog");
    }

    private void onClickCalculate(View view) {
        try {
            LocalDate selectedDate = LocalDate.of(
                    Integer.parseInt(yearText.getText().toString()),
                    Integer.parseInt(monthText.getText().toString()),
                    Integer.parseInt(dayText.getText().toString()));

            if (selectedDate.isBefore(firstMondayOfIterator) ||
                    selectedDate.isAfter(LocalDate.of(2023,12,31)))
                throw new Exception("Bad date");

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
        output.setText(INVALID_INPUT);
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

    private void updateTextViews(LocalDate selectedDate, LocalDate prevMonday,
                                 LocalDate nextMonday) {
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
            weekView.get(i).setBackgroundColor(resolveWeekViewDayColour(prevMonday.plusDays(i)));
        }
    }

    private int resolveWeekViewDayColour(LocalDate day) {
        if (timeBoxWeekIterator.isWfhDay(day))
            return Color.parseColor("#2196F3");
        else if (WEEKEND.contains(day.getDayOfWeek()))
            return Color.parseColor("#FF808080");
        else
            return Color.parseColor("#FFEB3B");
    }

    private String resolveResponse(LocalDate input) {
        if (WEEKEND.contains(input.getDayOfWeek())) return IS_WEEKEND;
        return timeBoxWeekIterator.isWfhDay(input) ? IS_WFH : NOT_WFH;
    }

    private String dayWithPrefix(int day) {
        if (day >= 4 && day <= 20) return day + "th";

        if (day % 10 == 1) return day + "st";
        if (day % 10 == 2) return day + "nd";
        if (day % 10 == 3) return day + "rd";

        return day + "th";
    }

    @Override
    public void applySelectedConfig(List<Boolean> week1, List<Boolean> week2) {
        Set<DayOfWeek> wfhDaysWeek1 = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            if (week1.get(i)) wfhDaysWeek1.add(DayOfWeek.of(i + 1));
        }
        Set<DayOfWeek> wfhDaysWeek2 = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            if (week2.get(i)) wfhDaysWeek2.add(DayOfWeek.of(i + 1));
        }
        setupTimeboxIterator(List.of(wfhDaysWeek1, wfhDaysWeek2));
        onClickCalculate(null);
    }
}