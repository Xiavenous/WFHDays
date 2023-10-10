package com.example.wfhdays;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TimeBoxWeekIterator {
    private int timeBoxNumber = 1;
    private int numWeeks = 0;
    private int selectedTimeboxWeek = 0;
    private final LocalDate firstMonday;
    private LocalDate selectedMonday;
    private final List<Set<DayOfWeek>> wfhDays = new ArrayList<>();

    public TimeBoxWeekIterator(LocalDate inputMonday) throws DateTimeException {
        if (inputMonday.getDayOfWeek() != DayOfWeek.MONDAY)
            throw new DateTimeException("Input date is not a \"Monday\"");
        this.firstMonday = newLocalDateCopy(inputMonday);
        this.selectedMonday = newLocalDateCopy(inputMonday);
    }

    public void addWeekWfhDays(Set<DayOfWeek> wfhWeekDays) {
        wfhDays.add(wfhWeekDays);
        numWeeks++;
    }

    public boolean isWfhDay(LocalDate inputDate) {
        while (!containsDate(inputDate)) nextTimeboxWeek();
        boolean isWfhDay = isWfhDay(inputDate.getDayOfWeek());
        resetIterator();
        return isWfhDay;
    }

    private boolean isWfhDay(DayOfWeek dayOfWeek) {
        return wfhDays.get(selectedTimeboxWeek).contains(dayOfWeek);
    }

    public void resetIterator() {
        selectedMonday = newLocalDateCopy(firstMonday);
        selectedTimeboxWeek = 0;
        timeBoxNumber = 1;
    }

    private boolean containsDate(LocalDate other) {
        if (this.selectedMonday.isEqual(other)) return true;

        return this.selectedMonday.isBefore(other) &&
                this.selectedMonday.plusWeeks(1).isAfter(other);
    }

    private void nextTimeboxWeek() {
        selectedMonday = selectedMonday.plusWeeks(1);
        selectedTimeboxWeek++;
        if (selectedTimeboxWeek == numWeeks) {
            selectedTimeboxWeek = 0;
            timeBoxNumber++;
        }
    }

    private static LocalDate newLocalDateCopy(LocalDate date) {
        return LocalDate.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    public int getTimeBoxNumber() {
        return timeBoxNumber;
    }
}
