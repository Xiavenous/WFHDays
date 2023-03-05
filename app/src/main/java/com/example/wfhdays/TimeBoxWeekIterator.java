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
    private List<Set<DayOfWeek>> wfhDays = new ArrayList<>();

    public void addWeekWfhDays(Set<DayOfWeek> wfhWeekDays) {
        wfhDays.add(wfhWeekDays);
        numWeeks++;
    }

    public void addWeekWfhDays() {
        wfhDays.add(Set.of());
        numWeeks++;
    }

    public boolean isWfhDay(LocalDate inputDate) {
        while (!containsDate(inputDate)) nextTimeboxWeek();
        boolean isWfhDay = isWfhDay(inputDate.getDayOfWeek());
        resetIterator();
        return isWfhDay;
    }

    public boolean isWfhDay(DayOfWeek dayOfWeek) {
        return wfhDays.get(selectedTimeboxWeek).contains(dayOfWeek);
    }

    public void resetIterator() {
        selectedMonday = LocalDate.of(firstMonday.getYear(), firstMonday.getMonthValue(), firstMonday.getDayOfMonth());
        selectedTimeboxWeek = 0;
        timeBoxNumber = 1;
    }

    public void nextTimeboxWeek() {
        selectedMonday = selectedMonday.plusWeeks(1);
        selectedTimeboxWeek++;
        if (selectedTimeboxWeek == numWeeks) {
            selectedTimeboxWeek = 0;
            timeBoxNumber++;
        }
    }

    public TimeBoxWeekIterator(LocalDate inputMonday, int timeBoxWeek, int timeBoxNumber) throws DateTimeException {
        if (inputMonday.getDayOfWeek() != DayOfWeek.MONDAY) throw new DateTimeException("Input date is not a \"Monday\"");
        this.selectedTimeboxWeek = timeBoxWeek;
        this.timeBoxNumber = timeBoxNumber;
        this.firstMonday = LocalDate.of(inputMonday.getYear(), inputMonday.getMonthValue(), inputMonday.getDayOfMonth());
        this.selectedMonday = LocalDate.of(inputMonday.getYear(), inputMonday.getMonthValue(), inputMonday.getDayOfMonth());
    }

    public boolean containsDate(LocalDate other) {
        if (this.selectedMonday.isEqual(other)) return true;

        return this.selectedMonday.isBefore(other) && this.selectedMonday.plusWeeks(1).isAfter(other);
    }

    public int getTimeBoxNumber() {
        return timeBoxNumber;
    }
}
