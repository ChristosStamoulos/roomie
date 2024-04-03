package org.example.backend.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class SimpleCalendar implements  Comparable<SimpleCalendar> {
    private static final long MILLIS_PER_DAY = 86400000;
    private Calendar date;

    public SimpleCalendar(int year, int month, int day) {
        date = Calendar.getInstance();
        date.set(year, month - 1, day);
        trimToDays(this.date);
    }

    public SimpleCalendar(String datestr){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
        LocalDate dat = LocalDate.parse(datestr, formatter);
        date = Calendar.getInstance();
        date.set(dat.getYear(), dat.getMonthValue(), dat.getDayOfMonth());
        trimToDays(this.date);
    }


    public SimpleCalendar(Calendar date) {
        this.date = Calendar.getInstance();
        this.date.setTimeInMillis(date.getTimeInMillis());
        trimToDays(this.date);
    }

    private void trimToDays(Calendar javaDate) {
        javaDate.set(Calendar.HOUR_OF_DAY, 0);
        javaDate.set(Calendar.MINUTE, 0);
        javaDate.set(Calendar.SECOND, 0);
        javaDate.set(Calendar.MILLISECOND, 0);
    }


    public long durationInDays(SimpleCalendar other) {
        long timeDiff = other.date.getTimeInMillis() - date.getTimeInMillis();
        return timeDiff / MILLIS_PER_DAY;
    }

    public int getYear() {
        return date.get(Calendar.YEAR);
    }

    public int getMonth() {
        return date.get(Calendar.MONTH) + 1;
    }

    public int getDayOfMonth() {
        return date.get(Calendar.DAY_OF_MONTH);
    }

    public int getDayOfWeek() {
        return date.get(Calendar.DAY_OF_WEEK);
    }

    public boolean after(SimpleCalendar other) {
        if (equals(other)) {
            return false;
        }

        return date.after(other.date);
    }

    public boolean before(SimpleCalendar other) {
        if (equals(other)) {
            return false;
        }

        return date.before(other.date);
    }

    public SimpleCalendar addDays(int days) {
        Calendar newDate = Calendar.getInstance();
        newDate.setTimeInMillis(date.getTimeInMillis());
        newDate.add(Calendar.DAY_OF_MONTH, days);
        return new SimpleCalendar(newDate);
    }

    public Calendar getJavaCalendar() {
        Calendar javaCalendar = Calendar.getInstance();
        javaCalendar.setTimeInMillis(date.getTimeInMillis());
        trimToDays(javaCalendar);
        return javaCalendar;
    }

    public int compareTo(SimpleCalendar other) {
        return date.compareTo(other.date);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (this == other) {
            return true;
        }

        if (!(other instanceof SimpleCalendar)) {
            return false;
        }

        SimpleCalendar theDate = (SimpleCalendar) other;

        if (getYear() != theDate.getYear()) {
            return false;
        }

        if (getMonth() != theDate.getMonth()) {
            return false;
        }

        if (getDayOfMonth() != theDate.getDayOfMonth()) {
            return false;
        }

        return true;
    }

    /**
     * The HashCode of a date
     * @return the HashCode
     */
    @Override
    public int hashCode() {
        return date == null ? 0 : date.hashCode();
    }
}

