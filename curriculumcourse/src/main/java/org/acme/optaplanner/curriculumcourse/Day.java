package org.acme.optaplanner.curriculumcourse;

import java.util.List;

/**
 * This class was automatically generated by the data modeler tool.
 */

public class Day implements java.io.Serializable {

    static final long serialVersionUID = 1L;

    public Day() {
    }

    private static final String[] WEEKDAYS = {"Mo", "Tu", "We", "Th", "Fr", "Sat", "Sun"};

    private int dayIndex;

    private List<Period> periodList;

    public int getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex(int dayIndex) {
        this.dayIndex = dayIndex;
    }

    public List<Period> getPeriodList() {
        return periodList;
    }

    public void setPeriodList(List<Period> periodList) {
        this.periodList = periodList;
    }

    public String getLabel() {
        String weekday = WEEKDAYS[dayIndex % WEEKDAYS.length];
        if (dayIndex > WEEKDAYS.length) {
            return "Day " + dayIndex;
        }
        return weekday;
    }

    @Override
    public String toString() {
        return Integer.toString(dayIndex);
    }

}