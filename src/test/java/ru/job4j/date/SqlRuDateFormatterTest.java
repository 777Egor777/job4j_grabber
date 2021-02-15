
package ru.job4j.date;

import org.junit.Test;

import java.util.Calendar;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
public class SqlRuDateFormatterTest {
    @Test
    public void whenFormatStandardDate() {
        SqlRuDateFormatter f = new SqlRuDateFormatter();
        String inDateStr = "2 дек 19, 22:29";
        assertThat(f.format(inDateStr), is("02.12.2019 22:29"));
    }

    @Test
    public void whenFormatCalendar() {
        SqlRuDateFormatter f = new SqlRuDateFormatter();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2020, Calendar.FEBRUARY, 13, 11, 22);
        assertThat(f.format(calendar), is("13.02.2020 11:22"));
    }
}
